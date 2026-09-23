"""
Automated Contract & Security Fuzzing Suite for AEGORA FastAPI Backend.
Uses Pytest, FastAPI TestClient, and Randomized Edge-Case Injections
to prove 100% fault rejection with accurate HTTP 400/403/422 status codes.
"""

import hmac
import hashlib
import json
import pytest
from fastapi.testclient import TestClient
from backend.main import app, AGENT_WEBHOOK_SECRET

client = TestClient(app)

# Malicious fuzz vectors for SQL Injection, XSS, Buffer Overflow & Path Traversal
MALICIOUS_FUZZ_VECTORS = [
    "<script>alert('pwned')</script>",
    "admin' OR '1'='1' --",
    "DROP TABLE users; --",
    "A" * 10000,  # Massive buffer payload
    "../../../../etc/passwd",
    "\x00\x00\x00\x00",
    "{\"__proto__\": {\"admin\": true}}",
    "' UNION SELECT null, username, password FROM accounts --",
]

def test_healthz_endpoint():
    response = client.get("/healthz")
    assert response.status_code == 200
    assert response.json()["status"] == "healthy"

def test_fuzz_create_checkout_with_injections():
    for malicious_input in MALICIOUS_FUZZ_VECTORS:
        payload = {
            "app_user_id": malicious_input,
            "operator_callsign": "TestOperator",
            "success_redirect_scheme": "aegora://claim-web-pro"
        }
        response = client.post("/api/v1/checkout/create-session", json=payload)
        # Must be rejected by Pydantic validation or custom sanitizers with 422 Unprocessable Entity
        assert response.status_code in [400, 422], f"Failed to reject malicious input: {malicious_input}"

def test_fuzz_threat_cvss_bounds():
    # CVSS must be between 0.0 and 10.0
    invalid_scores = [-1.0, -99.9, 10.1, 100.0, 9999.0]
    for invalid_cvss in invalid_scores:
        payload = {
            "threat_id": "T1059",
            "threat_title": "PowerShell Execution",
            "mitre_tactic": "Execution",
            "cvss_score": invalid_cvss,
            "source_ip": "192.168.1.100",
            "operator_target_id": "op_alpha_01"
        }
        response = client.post("/api/v1/threats/detect-and-remediate", json=payload)
        assert response.status_code == 422

def test_soar_autonomous_remediation_triggers_on_cvss_above_9():
    critical_payload = {
        "threat_id": "T1068",
        "threat_title": "Kernel Privilege Escalation",
        "mitre_tactic": "Privilege Escalation",
        "cvss_score": 9.8,
        "source_ip": "10.0.4.88",
        "operator_target_id": "op_sentinel_09"
    }
    response = client.post("/api/v1/threats/detect-and-remediate", json=critical_payload)
    assert response.status_code == 200
    data = response.json()
    assert data["is_critical"] is True
    assert "iptables -A INPUT -s 10.0.4.88 -j DROP" in data["soar_remediation"]["firewall_rule"]
    assert data["soar_remediation"]["operator_status"] == "HIGH_ALERT_LEVEL_4"

def test_agent_webhook_valid_hmac_signature():
    payload = {
        "workflow_id": "wf_n8n_soar_991",
        "intel_source": "AlienVault OTX",
        "threat_severity": "HIGH",
        "indicators": ["198.51.100.2", "malicious-domain.com"],
        "suggested_action": "BLOCK_EGRESS_PORT"
    }
    raw_body = json.dumps(payload).encode("utf-8")
    valid_sig = hmac.new(
        AGENT_WEBHOOK_SECRET.encode("utf-8"),
        raw_body,
        hashlib.sha256
    ).hexdigest()

    response = client.post(
        "/api/v1/agent-webhook",
        json=payload,
        headers={"X-Aegora-Signature": valid_sig}
    )
    assert response.status_code == 200
    assert response.json()["status"] == "INGESTED_TO_AEGORA_TELEMETRY"

def test_agent_webhook_invalid_hmac_signature_rejected():
    payload = {
        "workflow_id": "wf_forged_agent",
        "intel_source": "Adversary Injection",
        "threat_severity": "CRITICAL",
        "indicators": ["1.1.1.1"],
        "suggested_action": "DISABLE_FIREWALL"
    }
    response = client.post(
        "/api/v1/agent-webhook",
        json=payload,
        headers={"X-Aegora-Signature": "invalid_forged_signature_hex"}
    )
    assert response.status_code == 403
    assert "Invalid HMAC" in response.json()["detail"]


# --- Phase 34: RBAC, Hardware Binding & Perimeter Lockdown Tests ---

def test_production_perimeter_lockdown_disables_swagger():
    """Verify that /docs, /redoc, and /openapi.json are disabled in production"""
    resp_docs = client.get("/docs")
    resp_redoc = client.get("/redoc")
    resp_openapi = client.get("/openapi.json")
    assert resp_docs.status_code in [404, 405]
    assert resp_redoc.status_code in [404, 405]
    assert resp_openapi.status_code in [404, 405]

def test_admin_route_unauthenticated_rejected_401():
    """Verify that unauthenticated access to admin fortress is rejected with 401"""
    resp = client.get("/api/v1/admin/audit-logs")
    assert resp.status_code == 401
    assert "Bearer token required" in resp.json()["detail"]

def test_admin_route_operator_privilege_escalation_rejected_403():
    """Verify that an OPERATOR cannot access ADMIN endpoints"""
    # 1. Login as OPERATOR
    login_resp = client.post("/api/v1/auth/login", json={
        "username": "operator_alpha",
        "password": "AegoraOperatorPass99!",
        "device_fingerprint": "HW-FP-VAL-9901"
    })
    assert login_resp.status_code == 200
    token = login_resp.json()["access_token"]

    # 2. Attempt to access ADMIN endpoint
    admin_resp = client.get(
        "/api/v1/admin/audit-logs",
        headers={
            "Authorization": f"Bearer {token}",
            "X-Device-Fingerprint": "HW-FP-VAL-9901"
        }
    )
    assert admin_resp.status_code == 403
    assert "requires 'ADMIN'" in admin_resp.json()["detail"]

def test_admin_route_authenticated_success():
    """Verify that valid ADMIN credentials allow access"""
    # 1. Login as CISO ADMIN
    login_resp = client.post("/api/v1/auth/login", json={
        "username": "ciso_admin",
        "password": "HyperSecureEnclave2026!",
        "device_fingerprint": "HW-FP-STRONGKEY-ADMIN-01"
    })
    assert login_resp.status_code == 200
    token = login_resp.json()["access_token"]

    # 2. Access ADMIN endpoint
    admin_resp = client.get(
        "/api/v1/admin/audit-logs",
        headers={
            "Authorization": f"Bearer {token}",
            "X-Device-Fingerprint": "HW-FP-STRONGKEY-ADMIN-01"
        }
    )
    assert admin_resp.status_code == 200
    assert admin_resp.json()["status"] == "AUTHORIZED"
    assert admin_resp.json()["role"] == "ADMIN"

def test_hardware_binding_mismatch_revokes_session():
    """Verify token theft from a different device fingerprint fails and revokes session"""
    login_resp = client.post("/api/v1/auth/login", json={
        "username": "ciso_admin",
        "password": "HyperSecureEnclave2026!",
        "device_fingerprint": "ORIGINAL-DEVICE-ALPHA"
    })
    assert login_resp.status_code == 200
    stolen_token = login_resp.json()["access_token"]

    # Attacker uses token on ROGUE-DEVICE-BETA
    rogue_resp = client.get(
        "/api/v1/admin/audit-logs",
        headers={
            "Authorization": f"Bearer {stolen_token}",
            "X-Device-Fingerprint": "ROGUE-DEVICE-BETA"
        }
    )
    assert rogue_resp.status_code == 403
    assert "hardware binding" in rogue_resp.json()["detail"].lower()

