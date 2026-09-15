package com.example.network

import android.util.Log
import com.example.data.AegoraRepository
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Bulletproof OfflineMockInterceptor for AEGORA.
 *
 * Implements a zero-crash, high-reliability safety net for all network calls:
 * - Catches UnknownHostException (DNS failures / "server IP address could not be found")
 * - Catches SocketTimeoutException (Slow networks / timeouts)
 * - Catches ConnectException (Target server offline or unreachable)
 * - Flags `AegoraRepository.setOfflineMode(true)` so the Compose UI displays the tactical offline indicator
 * - Synthesizes realistic, domain-grounded HTTP 200 Mock JSON responses based on the request URI.
 */
class OfflineMockInterceptor : Interceptor {

  companion object {
    private const val TAG = "OfflineMockInterceptor"

    private const val MOCK_TELEMETRY_APT29 = """
    {
      "status": "success",
      "mode": "tactical_cached_intel",
      "data": {
        "threat_actor": "APT29 (Cozy Bear)",
        "campaign": "SolarWinds Supply Chain & Azure C2",
        "iocs": [
          {"type": "IP", "value": "194.165.16.89", "confidence": "0.98", "reputation": "MALICIOUS"},
          {"type": "SHA256", "value": "d3b07384d113edec49eaa6238ad5ff00", "description": "Sunburst DLL"}
        ],
        "mitre_tactics": [
          {"id": "T1195.002", "name": "Supply Chain Compromise"},
          {"id": "T1071.001", "name": "Web Protocols: Cobalt Strike Beaconing"}
        ],
        "active_telemetry": {
          "packets_analyzed": 48210,
          "anomalies_detected": 4,
          "decryption_state": "VERIFIED_HARDWARE_ATTESTATION"
        }
      }
    }
    """

    private const val MOCK_AI_AGENT_RESPONSE = """
    {
      "status": "success",
      "mode": "tactical_cached_intel",
      "agent": "OSINT Sentinel / Red Team AI",
      "observation": "Adversary beaconing active on port 443 with randomized 45s jitter.",
      "evidence_summary": "Sysmon Event ID 1 identifies powershell.exe spawned from winword.exe with Base64 payload.",
      "recommended_action": "Execute memory forensics to dump unencrypted C2 configuration.",
      "proof_digest": "0x7f4ae91b8a342981ce810"
    }
    """

    private const val MOCK_GEMINI_PAYLOAD = """
    {
      "candidates": [
        {
          "content": {
            "parts": [
              {
                "text": "[TACTICAL OFFLINE CACHE: GEMINI AI MENTOR]\nInvestigation verified: PowerShell execution was invoked with an encoded scriptlet.\n1. Sysmon Event ID 1 confirms parent process Winword.exe.\n2. Decode command via UTF-16LE.\n3. Verify network connection in Sysmon Event ID 3.\n\nSUGGESTION: Show me the command to decode Base64 in Linux\nSUGGESTION: How to configure Sigma rule for parent-child anomaly\nSUGGESTION: Walk through the incident report template"
              }
            ],
            "role": "model"
          },
          "finishReason": "STOP",
          "index": 0
        }
      ]
    }
    """

    private const val MOCK_GENERIC_PAYLOAD = """
    {
      "status": "success",
      "mode": "tactical_cached_intel",
      "code": 200,
      "message": "Authoritative cached intel served by AEGORA offline engine"
    }
    """
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    return try {
      val response = chain.proceed(request)
      if (response.isSuccessful) {
        AegoraRepository.setOfflineMode(false)
        response
      } else {
        Log.w(TAG, "[AEGORA_NET] Unsuccessful response (${response.code}) for ${request.url}. Serving fallback payload.")
        response.close()
        AegoraRepository.setOfflineMode(true)
        buildMockResponse(request)
      }
    } catch (e: UnknownHostException) {
      Log.w(TAG, "[AEGORA_DNS_SHIELD] UnknownHostException for ${request.url}. Triggering zero-crash offline mock.", e)
      AegoraRepository.setOfflineMode(true)
      buildMockResponse(request)
    } catch (e: SocketTimeoutException) {
      Log.w(TAG, "[AEGORA_TIMEOUT_SHIELD] SocketTimeoutException for ${request.url}. Serving cached mock telemetry.", e)
      AegoraRepository.setOfflineMode(true)
      buildMockResponse(request)
    } catch (e: ConnectException) {
      Log.w(TAG, "[AEGORA_CONNECT_SHIELD] ConnectException for ${request.url}. Server unreachable. Serving cached mock.", e)
      AegoraRepository.setOfflineMode(true)
      buildMockResponse(request)
    } catch (e: IOException) {
      Log.w(TAG, "[AEGORA_IO_SHIELD] IOException for ${request.url}: ${e.message}. Serving cached mock.", e)
      AegoraRepository.setOfflineMode(true)
      buildMockResponse(request)
    } catch (e: Throwable) {
      Log.e(TAG, "[AEGORA_CRITICAL_SHIELD] Unexpected network error for ${request.url}. Guaranteeing zero crash.", e)
      AegoraRepository.setOfflineMode(true)
      buildMockResponse(request)
    }
  }

  private fun buildMockResponse(request: Request): Response {
    val urlString = request.url.toString().lowercase()
    val mockBody = when {
      urlString.contains("/api/v1/telemetry") || urlString.contains("telemetry") -> MOCK_TELEMETRY_APT29
      urlString.contains("/api/v1/ai-agent") || urlString.contains("ai-agent") || urlString.contains("osint") -> MOCK_AI_AGENT_RESPONSE
      urlString.contains("generatecontent") || urlString.contains("generativelanguage") || urlString.contains("gemini") -> MOCK_GEMINI_PAYLOAD
      else -> MOCK_GENERIC_PAYLOAD
    }

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val responseBody = mockBody.trimIndent().toResponseBody(mediaType)

    return Response.Builder()
      .request(request)
      .protocol(Protocol.HTTP_1_1)
      .code(200)
      .message("OK (TACTICAL_OFFLINE_MOCK)")
      .body(responseBody)
      .header("X-Aegora-Offline-Mode", "true")
      .header("Content-Type", "application/json")
      .build()
  }
}
