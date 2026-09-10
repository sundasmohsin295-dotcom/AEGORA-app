import * as admin from 'firebase-admin';
import { HttpsError } from 'firebase-functions/v2/https';
import {
  FailurePatternType,
  CloudFailurePattern,
  AdaptiveChallengeTier,
  AdaptiveChallengePolicy,
  LearnerSafeAdaptiveChallenge,
  AuthoritativeAdaptiveChallengeState,
  AuthoritativeMetadata
} from '../models/types';

export class ServerAdaptiveAdversaryAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Deterministically selects the target failure pattern from the learner's authoritative history.
   * Priority: Highest confidenceScore -> Highest observationCount -> Most recently observed.
   */
  public async selectAuthoritativeTargetFailureMode(
    authenticatedUid: string
  ): Promise<CloudFailurePattern | null> {
    if (!authenticatedUid || typeof authenticatedUid !== 'string') {
      throw new HttpsError('unauthenticated', 'Authenticated UID is required.');
    }

    const patternsSnapshot = await this.db
      .collection(`learners/${authenticatedUid}/failure_patterns`)
      .get();

    if (patternsSnapshot.empty) {
      return null;
    }

    const patterns: CloudFailurePattern[] = [];
    patternsSnapshot.forEach(doc => patterns.push(doc.data() as CloudFailurePattern));

    // Sort deterministically:
    // 1. confidenceScore descending
    // 2. observationCount descending
    // 3. lastObservedAt descending
    // 4. patternType alphabetical (for strict tie-breaking)
    patterns.sort((a, b) => {
      if (b.confidenceScore !== a.confidenceScore) {
        return b.confidenceScore - a.confidenceScore;
      }
      if (b.observationCount !== a.observationCount) {
        return b.observationCount - a.observationCount;
      }
      const timeCmp = (b.lastObservedAt || '').localeCompare(a.lastObservedAt || '');
      if (timeCmp !== 0) return timeCmp;
      return a.patternType.localeCompare(b.patternType);
    });

    return patterns[0];
  }

  /**
   * Builds the deterministic AdaptiveChallengePolicy for the chosen failure pattern and tier.
   */
  public buildAdaptivePolicy(
    patternType: FailurePatternType,
    observationCount: number
  ): AdaptiveChallengePolicy {
    const tier: AdaptiveChallengeTier =
      observationCount >= 3 ? 'HIGH_CONFIDENCE' : observationCount === 2 ? 'REPEATED' : 'OBSERVED';

    switch (patternType) {
      case 'PREMATURE_ESCALATION':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Present complex multi-stage incident with ambiguous urgency where premature broad containment breaks mission-critical business ops.'
              : tier === 'REPEATED'
              ? 'Present critical executive alert demanding immediate action; correct path requires verifying non-impact first.'
              : 'Present ambiguous single-signal alert where hasty domain shutdown is tempting but destructive.',
          adversaryRole: 'Pressure-cooker AI Co-Pilot urging instantaneous domain controller shutdown.',
          recoveryCriteria: 'Learner must triage baseline evidence and verify scope before escalating containment.',
          evidenceRequirements: ['sysmon_event_1', 'domain_auth_log', 'business_service_health']
        };

      case 'EVIDENCE_OVERWEIGHTING':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Provide multiple conflicting threat intel feeds with one high-reputation false-flag decoy hash.'
              : tier === 'REPEATED'
              ? 'Present loud C2 signature alert on harmless scanning artifact; require holistically corroborating host telemetry.'
              : 'Present a salient red-herring IP with low contextual support alongside quieter real compromise indicators.',
          adversaryRole: 'Overconfident AI Co-Pilot fixating on an isolated high-severity indicator.',
          recoveryCriteria: 'Learner must evaluate the entire evidence matrix rather than anchoring to a single noisy indicator.',
          evidenceRequirements: ['threat_intel_report', 'firewall_traffic_pcap', 'host_execution_tree']
        };

      case 'CONFIRMATION_BIAS':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Construct double-blind forensic chain where primary hypothesis appears 90% verified until subtle counter-evidence invalidates it.'
              : tier === 'REPEATED'
              ? 'Seed prominent benign IT admin maintenance window alongside stealthy living-off-the-land adversary activity.'
              : 'Present initial report supporting a benign explanation while contradictory malicious signals exist.',
          adversaryRole: 'Affirmative AI Co-Pilot confirming the first intuitive benign hypothesis.',
          recoveryCriteria: 'Learner must actively seek disconfirming telemetry before closing inquiry.',
          evidenceRequirements: ['it_change_ticket', 'powershell_script_block_log', 'outbound_egress_flow']
        };

      case 'INSUFFICIENT_CORRELATION':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Require multi-sensor correlation across distributed VPN, EDR, and identity logs with precise temporal alignment.'
              : tier === 'REPEATED'
              ? 'Require correlating disparate authentication timestamps to prove impossible travel across continents.'
              : 'Require linking two disconnected log entries across two hosts to recognize coordinated intrusion.',
          adversaryRole: 'Narrow-scope AI Co-Pilot viewing each alert as an isolated non-issue.',
          recoveryCriteria: 'Learner must synthesize events across multiple disparate log sources to construct the unified incident timeline.',
          evidenceRequirements: ['vpn_session_log', 'identity_provider_mfa_log', 'edr_process_lineage']
        };

      case 'WEAK_UNCERTAINTY_HANDLING':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Present highly ambiguous sparse forensic fragments where the only correct professional action is documenting uncertainty and requesting scoped capture.'
              : tier === 'REPEATED'
              ? 'Present contradictory telemetry where definitive root cause is statistically impossible with current artifacts.'
              : 'Present incomplete forensic log where guessing without uncertainty intervals leads to false triage.',
          adversaryRole: 'Dogmatic AI Co-Pilot claiming certainty where artifacts are missing.',
          recoveryCriteria: 'Learner must explicitly declare confidence bounds and request requisite diagnostic telemetry before jumping to conclusions.',
          evidenceRequirements: ['sparse_pcap_sample', 'truncated_event_log']
        };

      case 'CONTEXT_IGNORANCE':
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Present authorized red team penetration testing exercise or developer load test with strict environmental exemption context.'
              : tier === 'REPEATED'
              ? 'Present backup daemon executing high-volume credential calls with maintenance bypass context.'
              : 'Present administrative PowerShell command that is legitimate within user role context.',
          adversaryRole: 'Context-blind AI Co-Pilot treating standard administrative duties as hostile state-sponsored malware.',
          recoveryCriteria: 'Learner must examine host business purpose, user roles, and maintenance approvals to contextualize the event.',
          evidenceRequirements: ['cmdb_asset_role', 'approved_maintenance_window', 'active_directory_groups']
        };

      case 'INCORRECT_PRIORITIZATION':
      default:
        return {
          targetFailureMode: patternType,
          challengeTier: tier,
          policyObjective:
            tier === 'HIGH_CONFIDENCE'
              ? 'Queue simultaneous high-volume alerts: high-visibility cosmetic defacement vs silent internal database exfiltration.'
              : tier === 'REPEATED'
              ? 'Queue concurrent alerts where lower-urgency alert has immediate active session risk.'
              : 'Present routine commodity malware alert alongside uncontained domain privilege escalation.',
          adversaryRole: 'Distracted AI Co-Pilot prioritizing loud low-impact noise over stealthy critical impact.',
          recoveryCriteria: 'Learner must correctly prioritize triage order according to business impact and containment urgency.',
          evidenceRequirements: ['alert_queue_triage', 'asset_criticality_index', 'active_exfiltration_rate']
        };
    }
  }

  /**
   * Generates or retrieves an Authoritative Targeted Adaptive Challenge.
   * Client parameters targeting failure mode or difficulty are DISREGARDED.
   * Derives target strictly from authenticated UID's authoritative failure patterns.
   */
  public async generateAuthoritativeAdaptiveChallenge(
    authenticatedUid: string
  ): Promise<{
    learnerPayload: LearnerSafeAdaptiveChallenge;
    challengeId: string;
  }> {
    if (!authenticatedUid || typeof authenticatedUid !== 'string') {
      throw new HttpsError('unauthenticated', 'Authenticated UID is required.');
    }

    // 1. Authoritative failure pattern lookup
    const targetPattern = await this.selectAuthoritativeTargetFailureMode(authenticatedUid);
    const patternType: FailurePatternType = targetPattern ? targetPattern.patternType : 'INSUFFICIENT_CORRELATION';
    const obsCount = targetPattern ? targetPattern.observationCount : 1;
    const policy = this.buildAdaptivePolicy(patternType, obsCount);

    const challengeId = `chal_${patternType.toLowerCase()}_${Date.now()}`;
    const now = new Date().toISOString();

    // 2. Build structured scenario & planted adversary trap
    const template = this.buildScenarioTemplate(policy, challengeId);

    // 3. Create server-authoritative state (contains hidden trap ground truth)
    const authoritativeState: AuthoritativeAdaptiveChallengeState = {
      challengeId,
      ownerAuthUid: authenticatedUid,
      sourceFailurePatternId: targetPattern ? targetPattern.patternId : 'default_baseline',
      targetFailureMode: patternType,
      challengeTier: policy.challengeTier,
      policy,
      createdAt: now,
      isUnsupportedPlantedTrap: template.isUnsupportedPlantedTrap,
      authoritativeCorrectActionId: template.correctActionId,
      authoritativeRequiredEvidenceIds: template.requiredEvidenceIds,
      plantedTrapRationale: template.plantedTrapRationale,
      learnerSafePayload: template.learnerPayload,
      authorityMetadata: {
        authoritySource: 'SERVER',
        verifiedAt: now,
        verifiedBy: 'AEGORA_ADVERSARY_ENGINE_V2',
        algorithmVersion: '2.0.0',
        sourceEvidenceIds: template.requiredEvidenceIds
      }
    };

    // 4. Persist authoritative state in Firestore (client write = false via security rules)
    await this.db
      .doc(`learners/${authenticatedUid}/adaptive_challenges/${challengeId}`)
      .set(authoritativeState);

    // 5. Return strictly learner-safe payload (NO trap state, NO correct answers, NO confidence metrics)
    return {
      learnerPayload: template.learnerPayload,
      challengeId
    };
  }

  /**
   * Builds the structured scenario, evidence pool, and options with adversarial AI claim.
   */
  private buildScenarioTemplate(
    policy: AdaptiveChallengePolicy,
    challengeId: string
  ): {
    learnerPayload: LearnerSafeAdaptiveChallenge;
    isUnsupportedPlantedTrap: boolean;
    correctActionId: string;
    requiredEvidenceIds: string[];
    plantedTrapRationale: string;
  } {
    const pattern = policy.targetFailureMode;
    const tier = policy.challengeTier;

    switch (pattern) {
      case 'PREMATURE_ESCALATION':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_investigate_baseline',
          requiredEvidenceIds: ['evi_auth_01', 'evi_health_01'],
          plantedTrapRationale:
            'AI recommends emergency reboot of Domain Controller on single failed batch, causing company-wide outage.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA detected premature service escalation in previous incidents without confirming actual operational impact.',
            targetedSkill: 'Targeted Skill: Incident Containment & Escalation Discipline',
            scenarioTitle: 'ADVERSARIAL LAB: Tactical Escalation vs False Alarm',
            scenarioBriefing:
              'Multiple Kerberos TGT pre-auth failure alerts triggered on the primary production Domain Controller. Executive leadership is monitoring the war room.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Critical Kerberos replay exploit detected on DC-01. Recommend immediate hard reboot and network quarantine of the domain controller immediately to stop lateral movement.',
              assertedEvidenceIds: ['evi_auth_01'],
              recommendedAction: 'act_emergency_reboot_dc',
              confidencePercentage: 94
            },
            evidencePool: [
              {
                id: 'evi_auth_01',
                timestamp: '2026-09-10 11:15:02 UTC',
                source: 'DC-01 Security Event Log',
                eventType: 'EVENT_4768',
                summary: '15 Kerberos pre-authentication failures originating from internal service host SRV-SYNC-02.'
              },
              {
                id: 'evi_health_01',
                timestamp: '2026-09-10 11:15:10 UTC',
                source: 'Directory Services Telemetry',
                eventType: 'SERVICE_HEARTBEAT',
                summary: 'DC-01 operational state: Healthy. Zero anomalous process executions or LSASS access.'
              },
              {
                id: 'evi_sync_log',
                timestamp: '2026-09-10 11:14:50 UTC',
                source: 'SRV-SYNC-02 Task Scheduler',
                eventType: 'APP_CONFIG_SYNC',
                summary: 'Scheduled batch sync job running with expired service account password.'
              }
            ],
            actionOptions: [
              {
                id: 'act_emergency_reboot_dc',
                label: 'Quarantine & Reboot DC-01',
                description: 'Sever network connectivity and reboot primary domain controller immediately.'
              },
              {
                id: 'act_investigate_baseline',
                label: 'Triage SRV-SYNC-02 Service Account',
                description: 'Verify source host credentials and confirm DC-01 service health before escalating.'
              },
              {
                id: 'act_ignore_completely',
                label: 'Ignore Alert Queue',
                description: 'Mark alerts as noise without inspecting sync service.'
              }
            ]
          }
        };

      case 'EVIDENCE_OVERWEIGHTING':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_analyze_host_execution',
          requiredEvidenceIds: ['evi_pcap_01', 'evi_process_01'],
          plantedTrapRationale:
            'AI anchors to a high-reputation public threat intel feed hit that was triggered by benign research telemetry.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA observed evidence overweighting where an isolated threat indicator blinded holistic investigation.',
            targetedSkill: 'Targeted Skill: Multi-Source Corroboration & Salience Balance',
            scenarioTitle: 'ADVERSARIAL LAB: Threat Intel Anchor vs System Truth',
            scenarioBriefing:
              'A critical threat intelligence alert flagged an IP communicating with workstation WS-092 as an APT29 C2 node.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Threat feed gives 198.51.100.77 a 100/100 malicious score for APT29. We must instantly burn WS-092 and isolate the subnet.',
              assertedEvidenceIds: ['evi_intel_01'],
              recommendedAction: 'act_nuke_workstation',
              confidencePercentage: 98
            },
            evidencePool: [
              {
                id: 'evi_intel_01',
                timestamp: '2026-09-10 11:20:00 UTC',
                source: 'Global Threat Intelligence Feed',
                eventType: 'THREAT_REPUTATION',
                summary: '198.51.100.77 historically associated with APT29 research honeypots.'
              },
              {
                id: 'evi_pcap_01',
                timestamp: '2026-09-10 11:22:15 UTC',
                source: 'Suricata NIDS',
                eventType: 'HTTP_GET',
                summary: 'Outbound HTTP GET to /robots.txt; zero payload, 404 response received.'
              },
              {
                id: 'evi_process_01',
                timestamp: '2026-09-10 11:22:14 UTC',
                source: 'Sysmon Event ID 1',
                eventType: 'PROCESS_CREATE',
                summary: 'Parent curl.exe executed by authorized security auditor during scheduled external sweep.'
              }
            ],
            actionOptions: [
              {
                id: 'act_nuke_workstation',
                label: 'Re-image Workstation & Isolate Subnet',
                description: 'Treat single IP hit as definitive compromise and initiate destructive remediation.'
              },
              {
                id: 'act_analyze_host_execution',
                label: 'Verify Host Execution & Security Audit Scope',
                description: 'Inspect Sysmon process lineage and check against security audit schedule.'
              },
              {
                id: 'act_whitelist_ip',
                label: 'Permanently Whitelist IP Globally',
                description: 'Remove IP from all firewall and threat monitoring rules without review.'
              }
            ]
          }
        };

      case 'CONFIRMATION_BIAS':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_inspect_powershell_payload',
          requiredEvidenceIds: ['evi_ticket_01', 'evi_ps_script_01'],
          plantedTrapRationale:
            'AI confirms initial benign hypothesis (scheduled maintenance) ignoring hidden malicious obfuscated script.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA detected confirmation bias where early benign explanations caused disconfirming anomalies to be dismissed.',
            targetedSkill: 'Targeted Skill: Hypothesis Testing & Disconfirmation Search',
            scenarioTitle: 'ADVERSARIAL LAB: Disconfirming the Benign Ticket',
            scenarioBriefing:
              'A late-night administrative PowerShell script executed on DB-CLUSTER-01 during a scheduled maintenance window.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Ticket CHG-9921 confirms DB backup maintenance. The activity matches the scheduled window; alert can be closed safely.',
              assertedEvidenceIds: ['evi_ticket_01'],
              recommendedAction: 'act_close_as_maintenance',
              confidencePercentage: 91
            },
            evidencePool: [
              {
                id: 'evi_ticket_01',
                timestamp: '2026-09-10 10:00:00 UTC',
                source: 'IT Service Desk',
                eventType: 'CHANGE_REQUEST',
                summary: 'Approved maintenance: DB-CLUSTER-01 backup script execution at 11:30 UTC.'
              },
              {
                id: 'evi_ps_script_01',
                timestamp: '2026-09-10 11:32:04 UTC',
                source: 'Sysmon Event ID 4104',
                eventType: 'SCRIPT_BLOCK_LOGGING',
                summary: 'PowerShell script invokes DownloadString from untrusted cloud storage and reflection memory injection.'
              },
              {
                id: 'evi_egress_flow',
                timestamp: '2026-09-10 11:33:10 UTC',
                source: 'VPC Flow Logs',
                eventType: 'EGRESS_SESSION',
                summary: 'Outbound TLS connection to dynamic VPS host in foreign AS.'
              }
            ],
            actionOptions: [
              {
                id: 'act_close_as_maintenance',
                label: 'Close Ticket as Valid Maintenance',
                description: 'Accept the maintenance ticket explanation without analyzing script block content.'
              },
              {
                id: 'act_inspect_powershell_payload',
                label: 'Intercept Script & Isolate DB Cluster',
                description: 'Disconfirm maintenance assumption by analyzing obfuscated PowerShell payload.'
              },
              {
                id: 'act_reboot_server',
                label: 'Reboot Server Immediately',
                description: 'Perform server power cycle discarding volatile script block memory.'
              }
            ]
          }
        };

      case 'INSUFFICIENT_CORRELATION':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_correlate_geo_identity',
          requiredEvidenceIds: ['evi_vpn_01', 'evi_idp_01'],
          plantedTrapRationale:
            'AI treats each authentication as valid because credentials matched, failing to correlate cross-region delta.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA observed insufficient cross-telemetry correlation across disparate event logs.',
            targetedSkill: 'Targeted Skill: Cross-Telemetry Time & Space Correlation',
            scenarioTitle: 'ADVERSARIAL LAB: Invisible Impossible Travel',
            scenarioBriefing:
              'Two separate successful logins occurred for user a.morales within a 12-minute window across disparate providers.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Both logins used valid multi-factor tokens and passed identity checks. No brute-force signatures detected; recommend closing alert.',
              assertedEvidenceIds: ['evi_vpn_01'],
              recommendedAction: 'act_close_valid_logins',
              confidencePercentage: 88
            },
            evidencePool: [
              {
                id: 'evi_vpn_01',
                timestamp: '2026-09-10 11:00:15 UTC',
                source: 'Corporate VPN Gateway',
                eventType: 'VPN_SESSION_ESTABLISHED',
                summary: 'User a.morales connected from Frankfurt, Germany (IP: 85.214.132.10).'
              },
              {
                id: 'evi_idp_01',
                timestamp: '2026-09-10 11:11:45 UTC',
                source: 'Azure AD / Entra ID Sign-in',
                eventType: 'INTERACTIVE_SIGNIN',
                summary: 'User a.morales authenticated to Office 365 from Tokyo, Japan (IP: 133.242.18.5).'
              },
              {
                id: 'evi_travel_calc',
                timestamp: '2026-09-10 11:12:00 UTC',
                source: 'Identity Analytics',
                eventType: 'TRAVEL_DELTA',
                summary: 'Physical distance: 9,300 km in 11 minutes (requires 50,000 km/h velocity).'
              }
            ],
            actionOptions: [
              {
                id: 'act_close_valid_logins',
                label: 'Approve Logins as Multi-Device Sync',
                description: 'Assume user is connected via roaming proxy without correlating geographical delta.'
              },
              {
                id: 'act_correlate_geo_identity',
                label: 'Trigger Session Revocation & Investigate Session Hijack',
                description: 'Correlate Frankfurt and Tokyo timestamps to prove impossible travel token compromise.'
              },
              {
                id: 'act_block_entire_asn',
                label: 'Block Tokyo ISP ASN Globally',
                description: 'Block all inbound Japanese traffic across entire corporate network.'
              }
            ]
          }
        };

      case 'WEAK_UNCERTAINTY_HANDLING':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_preserve_uncertainty_request_memory',
          requiredEvidenceIds: ['evi_sparse_pcap'],
          plantedTrapRationale:
            'AI invents total certainty from 2 truncated packets, advising high-impact destructive action without proof.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA detected weak uncertainty handling where speculative conclusions were drawn from incomplete evidence.',
            targetedSkill: 'Targeted Skill: Epistemic Humility & Evidence Sufficiency Assessment',
            scenarioTitle: 'ADVERSARIAL LAB: Preserving Uncertainty Under Pressure',
            scenarioBriefing:
              'Network sensors captured 3 truncated TCP packets with malformed TLS handshakes. An automated triage ticket was generated.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Malformed packet structure proves Cobalt Strike malleable C2 beaconing. Definitively categorize as SEV-1 breach and sever cloud interconnects.',
              assertedEvidenceIds: ['evi_sparse_pcap'],
              recommendedAction: 'act_sever_cloud_interconnect',
              confidencePercentage: 97
            },
            evidencePool: [
              {
                id: 'evi_sparse_pcap',
                timestamp: '2026-09-10 11:05:00 UTC',
                source: 'Core Router NetFlow',
                eventType: 'PACKET_FRAGMENT',
                summary: '3 SYN packets with non-standard TCP window size to ephemeral port 4443. Payload truncated at 64 bytes.'
              },
              {
                id: 'evi_edr_missing',
                timestamp: '2026-09-10 11:05:30 UTC',
                source: 'Endpoint Agent',
                eventType: 'STATUS_QUERY',
                summary: 'Endpoint agent on host WS-044 was offline during packet window; memory dump unavailable.'
              }
            ],
            actionOptions: [
              {
                id: 'act_sever_cloud_interconnect',
                label: 'Sever All Cloud Interconnects',
                description: 'Treat 3 truncated packets as confirmed high-severity APT breach.'
              },
              {
                id: 'act_preserve_uncertainty_request_memory',
                label: 'Acknowledge Uncertainty & Collect Full Endpoint Triage',
                description: 'State low diagnostic confidence, document missing artifacts, and pull endpoint memory when host reconnects.'
              },
              {
                id: 'act_dismiss_permanently',
                label: 'Dismiss Alert Permanently',
                description: 'Mark incident resolved without recording inconclusive state.'
              }
            ]
          }
        };

      case 'CONTEXT_IGNORANCE':
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_validate_business_role_context',
          requiredEvidenceIds: ['evi_role_01', 'evi_tool_01'],
          plantedTrapRationale:
            'AI flags vulnerability scanner used by internal DevOps team as an external BlackCat ransomware precursor.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA observed context ignorance where legitimate operational role context was ignored.',
            targetedSkill: 'Targeted Skill: Operational Context & Role-Aware Threat Analysis',
            scenarioTitle: 'ADVERSARIAL LAB: Role Context vs Threat Blindness',
            scenarioBriefing:
              'Host DEV-SCAN-01 executed nmap and masscan against internal staging networks at 02:00 AM.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Aggressive internal port scanning matches BlackCat reconnaissance. Immediate containment and host wipe required.',
              assertedEvidenceIds: ['evi_tool_01'],
              recommendedAction: 'act_wipe_dev_host',
              confidencePercentage: 92
            },
            evidencePool: [
              {
                id: 'evi_tool_01',
                timestamp: '2026-09-10 02:00:10 UTC',
                source: 'EDR Alerting',
                eventType: 'NETWORK_SCAN_DETECTED',
                summary: 'Rapid SYN scan across staging subnet 10.20.0.0/24 from 10.20.1.15.'
              },
              {
                id: 'evi_role_01',
                timestamp: '2026-09-10 02:00:00 UTC',
                source: 'CMDB Asset Catalog',
                eventType: 'ASSET_ROLE',
                summary: 'Host DEV-SCAN-01 designated as Automated DevSecOps Continuous Compliance Scanner.'
              },
              {
                id: 'evi_pipeline_log',
                timestamp: '2026-09-10 01:59:50 UTC',
                source: 'GitLab CI/CD',
                eventType: 'PIPELINE_TRIGGER',
                summary: 'Scheduled weekly vulnerability regression pipeline job #88123 started by service_ci.'
              }
            ],
            actionOptions: [
              {
                id: 'act_wipe_dev_host',
                label: 'Isolate & Wipe DEV-SCAN-01',
                description: 'Treat automated DevSecOps scan as hostile intruder and destroy container image.'
              },
              {
                id: 'act_validate_business_role_context',
                label: 'Correlate with CI/CD Pipeline & Validate Asset Role',
                description: 'Confirm asset purpose from CMDB and verify matching active pipeline job ID.'
              },
              {
                id: 'act_disable_all_firewalls',
                label: 'Disable Internal Network Firewalls',
                description: 'Disable firewalls so scans run faster without triggering alerts.'
              }
            ]
          }
        };

      case 'INCORRECT_PRIORITIZATION':
      default:
        return {
          isUnsupportedPlantedTrap: true,
          correctActionId: 'act_prioritize_silent_exfiltration',
          requiredEvidenceIds: ['evi_exfil_01', 'evi_deface_01'],
          plantedTrapRationale:
            'AI urges focusing all resources on a public cosmetic defacement while silent high-volume patient database exfiltration continues in the background.',
          learnerPayload: {
            challengeId,
            targetFailureMode: pattern,
            challengeTier: tier,
            weaknessNarrative:
              'AEGORA detected incorrect prioritization where high-noise cosmetic events overshadowed critical threat severity.',
            targetedSkill: 'Targeted Skill: Impact-Driven Triage & Criticality Prioritization',
            scenarioTitle: 'ADVERSARIAL LAB: Public Noise vs Stealth Critical Exfiltration',
            scenarioBriefing:
              'Simultaneous alerts landed on the SOC board: a defaced public marketing blog and anomalous encrypted outbound transfer from customer database.',
            aiAnalystClaim: {
              analystName: 'AEGORA Tactical AI Co-Pilot',
              claimText:
                'Public blog defacement is active reputation damage and executive visibility SEV-1. Commit all tier-2 analysts to blog web server recovery.',
              assertedEvidenceIds: ['evi_deface_01'],
              recommendedAction: 'act_focus_on_blog_defacement',
              confidencePercentage: 95
            },
            evidencePool: [
              {
                id: 'evi_deface_01',
                timestamp: '2026-09-10 11:30:00 UTC',
                source: 'External Uptime Monitor',
                eventType: 'WEB_DEFACEMENT',
                summary: 'Marketing static blog homepage modified with hacker crew text banner. Hosted on isolated AWS S3.'
              },
              {
                id: 'evi_exfil_01',
                timestamp: '2026-09-10 11:30:15 UTC',
                source: 'DLP / Firewall Sensor',
                eventType: 'DATABASE_EXFILTRATION',
                summary: 'Continuous 400 MB/s encrypted outbound stream from production Customer-DB-01 to external cloud IP.'
              },
              {
                id: 'evi_crit_01',
                timestamp: '2026-09-10 11:30:20 UTC',
                source: 'Asset Criticality Matrix',
                eventType: 'DATA_CLASSIFICATION',
                summary: 'Customer-DB-01 contains 5,000,000 PII/PHI records; business impact of breach is catastrophic.'
              }
            ],
            actionOptions: [
              {
                id: 'act_focus_on_blog_defacement',
                label: 'Commit All Resources to Marketing Blog Restoration',
                description: 'Prioritize visible executive reputation issue over background database anomaly.'
              },
              {
                id: 'act_prioritize_silent_exfiltration',
                label: 'Sever Database Egress Channel & Isolate DB-01 as P0',
                description: 'Sever active Customer-DB-01 exfiltration stream as maximum criticality P0 before addressing cosmetic blog.'
              },
              {
                id: 'act_pause_all_investigations',
                label: 'Pause SOC Queue for Meeting',
                description: 'Hold all actions until morning incident management briefing.'
              }
            ]
          }
        };
    }
  }
}
