import {
  MissionState,
  MissionValidationOutcome
} from '../types/platform';

export const SUSPICIOUS_LOGIN_MISSION: MissionState = {
  missionId: 'lab_suspicious_login',
  title: 'Investigate a Suspicious Login',
  objective:
    'Analyze authentication logs, detect brute-force password spraying and impossible-travel login anomalies across geographic regions.',
  targetDomain: 'Defensive Security',
  events: [
    {
      id: 'tl_01',
      timestamp: '2026-08-25 08:14:02 UTC',
      eventType: 'FAILED_LOGIN (Event ID 4625)',
      sourceHost: 'DC-PRIMARY-01',
      processOrUser: 'User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)',
      summary: 'Logon Failure: Bad password submitted for user j.smith. Attempt 1/8.',
      rawLog:
        'EventID=4625 Status=0xC000006A User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3',
      isSuspicious: true
    },
    {
      id: 'tl_02',
      timestamp: '2026-08-25 08:14:15 UTC',
      eventType: 'FAILED_LOGIN (Event ID 4625)',
      sourceHost: 'DC-PRIMARY-01',
      processOrUser: 'User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)',
      summary: 'Logon Failure: Bad password submitted for user j.smith. Attempt 5/8 in rapid succession.',
      rawLog:
        'EventID=4625 Status=0xC000006A User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3',
      isSuspicious: true
    },
    {
      id: 'tl_03',
      timestamp: '2026-08-25 08:15:40 UTC',
      eventType: 'SUCCESS_LOGIN (Event ID 4624)',
      sourceHost: 'DC-PRIMARY-01',
      processOrUser: 'User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)',
      summary: 'Logon Success: Password accepted for j.smith following rapid failures.',
      rawLog:
        'EventID=4624 Status=0x0 User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3 AuthPackage=NTLM',
      isSuspicious: true
    },
    {
      id: 'tl_04',
      timestamp: '2026-08-25 08:22:10 UTC',
      eventType: 'SUCCESS_LOGIN (Event ID 4624)',
      sourceHost: 'VPN-GATEWAY-US',
      processOrUser: 'User: j.smith | Source IP: 73.189.44.10 (Austin, Texas, US)',
      summary: 'Logon Success: Legitimate corporate VPN login from assigned employee home location.',
      rawLog:
        'EventID=4624 Status=0x0 User=j.smith SrcIP=73.189.44.10 Device=CiscoAnyConnect LogonType=2',
      isSuspicious: false
    }
  ],
  questions: [
    {
      id: 'q_login_1',
      stepNumber: 1,
      questionText:
        'Based on the authentication logs, what type of adversary initial access activity occurred prior to 08:15:40 UTC?',
      options: [
        'Adversary Brute-force / Credential Stuffing from IP 198.51.100.12',
        'Routine password change by the legitimate employee',
        'DDoS attack targeting the domain controller',
        'Scheduled Kerberos ticket renewal'
      ],
      correctOptionIndex: 0,
      hints: {
        conceptual: 'Examine the repeated Event ID 4625 failure events before the single 4624 success.',
        evidence: 'Look at IP 198.51.100.12 sending repeated bad password requests in under 90 seconds.',
        direction: 'Multiple rapid logon failures leading to a successful authentication is indicative of automated brute forcing.',
        fullExplanation:
          'The attacker used automated credential testing against j.smith\'s account from external IP 198.51.100.12 until achieving a valid login at 08:15:40 UTC.'
      }
    },
    {
      id: 'q_login_2',
      stepNumber: 2,
      questionText:
        'Why does the login at 08:22:10 UTC from Austin, Texas create an "Impossible Travel" detection alert?',
      options: [
        'It is physically impossible to travel between Moscow and Austin, Texas within 7 minutes',
        'Austin IP addresses are universally blacklisted',
        'VPN logins are forbidden during morning hours',
        'The user was using an obsolete browser'
      ],
      correctOptionIndex: 0,
      hints: {
        conceptual: 'Compare the timestamps and geographic coordinates of the two successful logins.',
        evidence: 'Moscow at 08:15 UTC vs Austin at 08:22 UTC is a delta of only 7 minutes across ~9,000 km.',
        direction: 'Calculate distance over time: traveling across the Atlantic in 7 minutes violates physical constraints.',
        fullExplanation:
          'Impossible Travel occurs when sequential authentications for the same identity originate from distant geographic regions faster than commercial aircraft speed.'
      }
    },
    {
      id: 'q_login_3',
      stepNumber: 3,
      questionText:
        'What immediate SOC containment action should be taken for user account "j.smith"?',
      options: [
        'Revoke active sessions, disable account/force password reset, and isolate IP 198.51.100.12',
        'Ignore the alert until the end of the business day',
        'Reboot the Domain Controller',
        'Delete the user mailbox permanently'
      ],
      correctOptionIndex: 0,
      hints: {
        conceptual: 'Think about identity containment following confirmed account compromise.',
        evidence: 'The Moscow session is active and authenticated under valid credentials.',
        direction: 'Kill active refresh tokens and block the malicious external source IP at the firewall.',
        fullExplanation:
          'Revoking active OAuth/Kerberos session tokens, triggering an immediate credential reset with MFA requirement, and blacklisting the attacker IP prevents further lateral movement.'
      }
    }
  ]
};

/**
 * Validates the web mission execution authoritatively.
 * Client UI cannot fabricate passing verification without meeting the rubric.
 */
export function validateWebMission(
  answers: Record<string, number>,
  reasoning: string
): MissionValidationOutcome {
  if (!reasoning || reasoning.trim().length < 15) {
    return {
      isPassed: false,
      scorePercent: 0,
      reasoningFeedback:
        'Investigation rejected: Reasoning length is insufficient (<15 characters). Professional cybersecurity operators must document investigative justification.'
    };
  }

  let correctCount = 0;
  const questions = SUSPICIOUS_LOGIN_MISSION.questions;

  for (const q of questions) {
    if (answers[q.id] === q.correctOptionIndex) {
      correctCount++;
    }
  }

  const scorePercent = Math.round((correctCount / questions.length) * 100);
  const isPassed = scorePercent >= 70;

  if (isPassed) {
    const evidenceHash = `sha256:aegora_web_${SUSPICIOUS_LOGIN_MISSION.missionId}_${Date.now()}_${scorePercent}pct`;
    return {
      isPassed: true,
      scorePercent,
      evidenceHash,
      capabilityKey: 'sec_auth_impossible_travel_triage',
      reasoningFeedback:
        'Evidence verified. Root cause analysis confirmed account compromise via password spraying, impossible travel detected between RU and US, and appropriate identity containment was executed.'
    };
  }

  return {
    isPassed: false,
    scorePercent,
    remediationAdvice:
      'Review Windows Event IDs 4624/4625 sequence and GeoIP velocity calculation before re-attempting.',
    reasoningFeedback:
      'Decision triage failed to contain the active adversary session. Re-examine the timeline logs.'
  };
}
