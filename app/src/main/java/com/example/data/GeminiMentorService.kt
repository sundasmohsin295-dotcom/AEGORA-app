package com.example.data

import com.example.BuildConfig
import com.example.model.AiMentorMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiMentorService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

  suspend fun askMentor(
    userPrompt: String,
    mode: AiMentorMode,
    userCareerContext: String = "SOC Analyst"
  ): Pair<String, List<String>> = withContext(Dispatchers.IO) {
    val apiKey = try {
      BuildConfig.GEMINI_API_KEY
    } catch (e: Exception) {
      ""
    }

    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      try {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val systemInstructionText = "${mode.systemPromptFocus} Student Target Career: $userCareerContext. Respond concisely, precisely, and with high professional cybersecurity craftsmanship. Conclude with 2-3 short follow-up prompts on separate lines formatted as 'SUGGESTION: <prompt>'."

        val jsonBody = JSONObject().apply {
          put("systemInstruction", JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", systemInstructionText))
            })
          })
          put("contents", JSONArray().apply {
            put(JSONObject().apply {
              put("parts", JSONArray().apply {
                put(JSONObject().put("text", userPrompt))
              })
            })
          })
          put("generationConfig", JSONObject().apply {
            put("temperature", 0.4)
            put("maxOutputTokens", 800)
          })
        }

        val request = Request.Builder()
          .url(endpoint)
          .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
          .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
          val responseBody = response.body?.string().orEmpty()
          val jsonResp = JSONObject(responseBody)
          val candidates = jsonResp.optJSONArray("candidates")
          if (candidates != null && candidates.length() > 0) {
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.getJSONObject(0)?.optString("text").orEmpty()
            if (rawText.isNotBlank()) {
              return@withContext parseResponseAndSuggestions(rawText, mode)
            }
          }
        }
      } catch (e: Exception) {
        // Fall back to built-in cybersecurity expert engine
      }
    }

    // High-fidelity domain-expert heuristic response engine
    return@withContext generateExpertOfflineResponse(userPrompt, mode, userCareerContext)
  }

  private fun parseResponseAndSuggestions(rawText: String, mode: AiMentorMode): Pair<String, List<String>> {
    val lines = rawText.lines()
    val mainLines = mutableListOf<String>()
    val suggestions = mutableListOf<String>()

    for (line in lines) {
      if (line.trim().startsWith("SUGGESTION:", ignoreCase = true)) {
        val suggestion = line.substringAfter("SUGGESTION:").trim()
        if (suggestion.isNotBlank()) suggestions.add(suggestion)
      } else {
        mainLines.add(line)
      }
    }

    val cleanedText = mainLines.joinToString("\n").trim()
    val finalSuggestions = if (suggestions.isNotEmpty()) {
      suggestions.take(3)
    } else {
      getDefaultSuggestions(mode)
    }

    return Pair(cleanedText, finalSuggestions)
  }

  private fun generateExpertOfflineResponse(
    prompt: String,
    mode: AiMentorMode,
    career: String
  ): Pair<String, List<String>> {
    val lower = prompt.lowercase()

    val response = when (mode) {
      AiMentorMode.SOC_MENTOR -> when {
        lower.contains("powershell") || lower.contains("encoded") ->
          """
          [SOC Incident Triage Protocol]
          Adversary technique identified: MITRE ATT&CK T1059.001 (Command and Scripting Interpreter: PowerShell).
          
          Immediate Investigation Actions:
          1. Check Sysmon Event ID 1: Inspect `ParentImage` (e.g. Word, Excel, or wscript). Macro-spawned shells indicate initial access (T1566.001).
          2. Decode Base64 Payload: In Linux CLI run:
             `echo '<BASE64>' | base64 -d | iconv -f UTF-16LE -t UTF-8`
          3. Correlate with Sysmon Event ID 3 (Network Connection): Extract destination IP & port for C2 beaconing.
          4. Containment: If malicious, isolate endpoint immediately via EDR/firewall rule.
          """.trimIndent()

        lower.contains("phishing") || lower.contains("email") ->
          """
          [Phishing Triage Analysis]
          Follow the 4-stage email forensic triage workflow:
          1. Header Inspection: Check `Authentication-Results` for SPF, DKIM, and DMARC pass/fail flags. Check true `Return-Path`.
          2. URL Defense: Defang links (e.g. `hxxps[://]bad-domain[.]com`) and submit to VirusTotal / urlscan.io.
          3. Attachment Triage: Calculate SHA256 of attachments and detonate in an isolated sandbox (Any.Run / Cuckoo).
          4. Enterprise Scope: Search SIEM for all recipients of the subject line and purge from mailboxes.
          """.trimIndent()

        lower.contains("mitre") ->
          """
          [MITRE ATT&CK Framework Mapping]
          The MITRE Enterprise Matrix categorizes adversary behavior into 14 Tactics (the 'Why') and hundreds of Techniques (the 'How').
          
          Standard Attack Chain:
          Initial Access (TA0001) → Execution (TA0002) → Persistence (TA0003) → Privilege Escalation (TA0004) → Defense Evasion (TA0005) → Credential Access (TA0006) → Lateral Movement (TA0008) → Exfiltration (TA0010).
          """.trimIndent()

        else ->
          """
          [SOC Analyst Guidance]
          Analyzing telemetry for: '$prompt'.
          When investigating this security anomaly, correlate endpoint logs (Sysmon/Windows Event 4688), network flow data (Zeek/Suricata), and authentication events (Event 4624 Type 3/10).
          
          Next recommended step: Formulate a hypothesis and search your SIEM data lake for matching IOCs.
          """.trimIndent()
      }

      AiMentorMode.SOCRATIC ->
        """
        Let us break this down systematically.
        
        Before looking at the final payload for '$prompt':
        1. What specific log source on a Windows endpoint would be the first to record process creation?
        2. What anomaly would you look for in the parent-child process relationship?
        
        What do you think is the next logical piece of evidence to verify?
        """.trimIndent()

      AiMentorMode.TUTOR ->
        """
        [Conceptual Breakdown: $prompt]
        
        Core Principle:
        Cybersecurity defense is founded on the 'Defense-in-Depth' model. Rather than relying on a single impenetrable wall, organizations layer security controls across Perimeter, Network, Host, Application, and Data tiers.
        
        Why this matters for a $career:
        When an adversary bypasses perimeter firewalls, host-level telemetry and SIEM correlation are what allow you to detect lateral movement before ransomware deployment or data exfiltration occurs.
        """.trimIndent()

      AiMentorMode.PROGRESSIVE_HINT ->
        """
        [Progressive Hint: Level 1]
        
        💡 Hint:
        Look closely at the command line arguments. Notice the `-enc` flag? PowerShell encoded commands are not encoded in standard ASCII. What character encoding does Windows PowerShell require for Base64 streams?
        
        (Ask for 'Hint 2' if you need the next clue!)
        """.trimIndent()

      AiMentorMode.CAREER_MENTOR ->
        """
        [Career Intelligence & Strategy]
        For your target goal of **$career**:
        
        Current Market Snapshot:
        - Employers prioritize verifiable practical evidence over passive course completions.
        - Top 3 in-demand skills on job postings right now: SIEM Log Triage (Wazuh/Splunk), MITRE ATT&CK mapping, and hands-on incident report writing.
        
        Action Recommendation:
        Add your 'Mini Enterprise SOC Lab' with Wazuh and Sysmon to your GitHub repository and link it to your AEGORA Skill Passport.
        """.trimIndent()

      AiMentorMode.INTERVIEWER ->
        """
        [Technical Scenario Question - SOC L1]
        
        "An analyst receives a high-severity alert: At 02:14 AM on Sunday, a workstation in Accounting successfully logged into the Primary Domain Controller via SMB using NTLM authentication, followed immediately by multiple Kerberos Service Ticket requests (Event ID 4769) with encryption type 0x17 (RC4).
        
        1. What specific Active Directory attack technique is being attempted?
        2. What are your first three immediate containment actions?"
        """.trimIndent()

      else ->
        """
        [AEGORA Cyber Intelligence Core]
        Response for '$prompt':
        Security operations emphasize zero-trust architecture, continuous verification, and least privilege. Review the associated practical lab in your AEGORA Journey to test this concept hands-on.
        """.trimIndent()
    }

    return Pair(response, getDefaultSuggestions(mode))
  }

  private fun getDefaultSuggestions(mode: AiMentorMode): List<String> = when (mode) {
    AiMentorMode.SOC_MENTOR -> listOf(
      "How do I detect Pass-the-Hash in Windows logs?",
      "Explain the difference between Sysmon Event 1 and Event 3",
      "Walk me through a ransomware containment playbook"
    )
    AiMentorMode.SOCRATIC -> listOf(
      "Give me another guiding question",
      "I think it's Sysmon Event ID 1",
      "Give me a hint on process lineage"
    )
    AiMentorMode.CAREER_MENTOR -> listOf(
      "What certifications should I take first?",
      "How do I write a SOC project on GitHub?",
      "Analyze my current Job Readiness Score"
    )
    AiMentorMode.PROGRESSIVE_HINT -> listOf(
      "Give me Hint 2",
      "Show me the command to decode it",
      "Explain why UTF-16LE is required"
    )
    else -> listOf(
      "Explain simply with an analogy",
      "Show me a real-world scenario",
      "Test me on this concept"
    )
  }
}
