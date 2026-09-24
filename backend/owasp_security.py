"""
AEGORA: OWASP Vulnerability Mitigation & Enterprise Security Headers
Implements strict OWASP Cheat Sheet standards:
 - Input Sanitization & Attack Pattern Defenses (SQLi, XSS, Path Traversal, Command Injection)
 - High-Security HTTP Response Headers (CSP, HSTS, X-Frame-Options, X-Content-Type-Options)
 - Information Disclosure Prevention & Server Masking
"""

import re
import html
import logging
from typing import Optional, Dict, Any
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.requests import Request
from starlette.responses import Response, JSONResponse

logger = logging.getLogger("AEGORA_OWASP")

# ============================================================================
# 1. OWASP INPUT SANITIZATION & ATTACK SIGNATURE PATTERNS
# ============================================================================
class OWASPInputSanitizer:
    # Common SQL Injection attack patterns
    SQLI_PATTERNS = [
        re.compile(r"(\b(UNION(\s+ALL)?|SELECT|DROP|INSERT|DELETE|UPDATE|TRUNCATE|ALTER)\b)", re.IGNORECASE),
        re.compile(r"(--|#|/\*|\*/|;)", re.IGNORECASE),
        re.compile(r"(\bOR\b|\bAND\b)\s+['\"0-9a-zA-Z]+\s*=\s*['\"0-9a-zA-Z]+", re.IGNORECASE),
        re.compile(r"(\bWAITFOR\s+DELAY\b|\bBENCHMARK\s*\(|\bSLEEP\s*\()", re.IGNORECASE),
        re.compile(r"(\bEXEC\s*\(|\bEXECUTE\s*\()", re.IGNORECASE)
    ]

    # Cross-Site Scripting (XSS) vectors
    XSS_PATTERNS = [
        re.compile(r"<\s*script[^>]*>", re.IGNORECASE),
        re.compile(r"<\s*/\s*script\s*>", re.IGNORECASE),
        re.compile(r"javascript\s*:", re.IGNORECASE),
        re.compile(r"data\s*:\s*text/html", re.IGNORECASE),
        re.compile(r"on(load|error|click|mouseover|submit|focus)\s*=", re.IGNORECASE),
        re.compile(r"<\s*(iframe|embed|object|svg|img)[^>]+src\s*=", re.IGNORECASE)
    ]

    # Path Traversal & File Inclusion
    PATH_TRAVERSAL_PATTERNS = [
        re.compile(r"(\.\./|\.\.\\)", re.IGNORECASE),
        re.compile(r"(%2e%2e%2f|%2e%2e\/|\.\.%2f)", re.IGNORECASE),
        re.compile(r"%00", re.IGNORECASE) # Null byte injection
    ]

    # OS Command Injection
    COMMAND_INJECTION_PATTERNS = [
        re.compile(r"(\|\||&&|;|`|\$\()", re.IGNORECASE)
    ]

    @classmethod
    def inspect_for_threats(cls, value: str, check_command: bool = False) -> Optional[str]:
        """
        Scans a string input against OWASP threat signatures.
        Returns the threat category if detected, or None if clean.
        """
        if not value or not isinstance(value, str):
            return None

        # Check Path Traversal
        for pattern in cls.PATH_TRAVERSAL_PATTERNS:
            if pattern.search(value):
                return "PATH_TRAVERSAL_DETECTED"

        # Check XSS
        for pattern in cls.XSS_PATTERNS:
            if pattern.search(value):
                return "XSS_VECTOR_DETECTED"

        # Check SQLi
        for pattern in cls.SQLI_PATTERNS:
            if pattern.search(value):
                return "SQL_INJECTION_DETECTED"

        # Check Command Injection if requested
        if check_command:
            for pattern in cls.COMMAND_INJECTION_PATTERNS:
                if pattern.search(value):
                    return "COMMAND_INJECTION_DETECTED"

        return None

    @classmethod
    def sanitize_text(cls, value: str) -> str:
        """
        Applies HTML entity escaping and strips null bytes.
        """
        if not isinstance(value, str):
            return value
        cleaned = value.replace("\x00", "")
        return html.escape(cleaned, quote=True).strip()


# ============================================================================
# 2. OWASP SECURITY HEADERS MIDDLEWARE
# ============================================================================
class OWASPSecurityHeadersMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next) -> Response:
        # Pre-execution: check request query parameters for injection vectors
        query_str = str(request.query_params)
        threat = OWASPInputSanitizer.inspect_for_threats(query_str)
        if threat:
            logger.warning(f"[OWASP_GUARD] Intercepted {threat} in query string from {request.client.host if request.client else 'UNKNOWN'}")
            return JSONResponse(
                status_code=400,
                content={
                    "error": "BAD_REQUEST",
                    "code": "OWASP_SECURITY_VIOLATION",
                    "message": "Potential injection vector detected and blocked by Aegora Perimeter Guard."
                }
            )

        response = await call_next(request)

        # Inject OWASP Top 10 Recommended Security Headers
        response.headers["Content-Security-Policy"] = (
            "default-src 'self'; "
            "script-src 'self'; "
            "style-src 'self' 'unsafe-inline'; "
            "font-src 'self' data:; "
            "img-src 'self' data: https:; "
            "connect-src 'self' https://*.supabase.co https://onesignal.com https://api.revenuecat.com; "
            "frame-ancestors 'none'; "
            "base-uri 'self'; "
            "form-action 'self';"
        )
        response.headers["X-Content-Type-Options"] = "nosniff"
        response.headers["X-Frame-Options"] = "DENY"
        response.headers["Strict-Transport-Security"] = "max-age=63072000; includeSubDomains; preload"
        response.headers["X-XSS-Protection"] = "0" # Modern standard: disable buggy browser XSS auditor in favor of strict CSP
        response.headers["Referrer-Policy"] = "strict-origin-when-cross-origin"
        response.headers["Permissions-Policy"] = "camera=(), microphone=(), geolocation=(), payment=(), usb=()"
        response.headers["Cache-Control"] = "no-store, no-cache, must-revalidate, proxy-revalidate"
        response.headers["Pragma"] = "no-cache"

        # Mask technological fingerprint / Server banner
        response.headers["Server"] = "AEGORA-FORTRESS/1.0"

        return response
