"""
AEGORA: n8n Automated Workflow Webhook Telemetry Dispatcher
Pushes critical security incidents, anomalous clusters, and Merkle audit blocks
to configured n8n automation pipelines for automated incident response.
"""

import os
import json
import time
import hmac
import hashlib
import logging
import urllib.request
import urllib.error
from typing import Dict, Any, Optional
from backend.supabase_client import supabase_client

logger = logging.getLogger("AEGORA_N8N")

N8N_INCIDENT_WEBHOOK_URL = os.getenv("N8N_INCIDENT_WEBHOOK_URL", "http://localhost:5678/webhook/aegora-threat-incident")
N8N_WEBHOOK_SECRET = os.getenv("N8N_WEBHOOK_SECRET", os.getenv("AGENT_WEBHOOK_SECRET", "aegora_super_secret_hmac_key_9942"))

class N8nWorkflowDispatcher:
    def __init__(self, webhook_url: Optional[str] = None):
        self.webhook_url = webhook_url or N8N_INCIDENT_WEBHOOK_URL
        self.secret_key = N8N_WEBHOOK_SECRET

    def dispatch_incident(
        self,
        incident_id: str,
        threat_title: str,
        cvss_score: float,
        severity: str,
        source_ip: str,
        merkle_block_hash: str,
        soar_action: Optional[Dict[str, Any]] = None,
        additional_metadata: Optional[Dict[str, Any]] = None
    ) -> Dict[str, Any]:
        """
        Dispatches an incident payload to n8n with an HMAC-SHA256 signature in X-Aegora-Signature header.
        Guarantees zero-crash execution through safe exception handling.
        """
        timestamp = time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
        payload = {
            "source": "AEGORA_SOC_CORE",
            "incident_id": incident_id,
            "timestamp": timestamp,
            "threat_title": threat_title,
            "cvss_score": cvss_score,
            "severity": severity,
            "source_ip": source_ip,
            "merkle_block_hash": merkle_block_hash,
            "soar_action": soar_action or {"status": "LOGGED"},
            "metadata": additional_metadata or {}
        }

        payload_bytes = json.dumps(payload, separators=(',', ':')).encode("utf-8")
        signature = hmac.new(
            self.secret_key.encode("utf-8"),
            payload_bytes,
            hashlib.sha256
        ).hexdigest()

        # Log to Supabase queue table if enabled
        supabase_client._make_rest_request(
            table="aegora_n8n_incidents",
            method="POST",
            data={
                "incident_id": incident_id,
                "threat_title": threat_title,
                "cvss_score": cvss_score,
                "severity": severity,
                "source_ip": source_ip,
                "merkle_block_hash": merkle_block_hash,
                "payload": payload,
                "webhook_url": self.webhook_url,
                "dispatch_status": "PENDING"
            }
        )

        headers = {
            "Content-Type": "application/json",
            "X-Aegora-Signature": signature,
            "X-Aegora-Timestamp": str(int(time.time())),
            "User-Agent": "Aegora-Autonomous-SOAR/1.0"
        }

        try:
            req = urllib.request.Request(
                self.webhook_url,
                data=payload_bytes,
                headers=headers,
                method="POST"
            )
            with urllib.request.urlopen(req, timeout=4) as response:
                status_code = response.getcode()
                logger.info(f"[N8N_DISPATCHER] Incident {incident_id} successfully delivered to n8n (HTTP {status_code})")
                return {
                    "success": True,
                    "incident_id": incident_id,
                    "http_status": status_code,
                    "webhook_target": self.webhook_url
                }
        except Exception as e:
            logger.info(f"[N8N_DISPATCHER] n8n webhook queued (offline/simulated response): {e}")
            return {
                "success": False,
                "incident_id": incident_id,
                "queued": True,
                "note": "n8n pipeline endpoint offline or mock mode; payload securely buffered."
            }

n8n_dispatcher = N8nWorkflowDispatcher()
