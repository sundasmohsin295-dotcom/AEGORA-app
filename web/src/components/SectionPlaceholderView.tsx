import React from 'react';
import {
  BookOpen,
  Terminal,
  Bug,
  Compass,
  Wrench,
  Radio,
  ArrowRight,
  ShieldCheck,
  Cpu,
  Lock,
  ChevronRight
} from 'lucide-react';

export type SectionType =
  | 'learn'
  | 'practice'
  | 'bounty'
  | 'roadmap'
  | 'tools'
  | 'intelligence';

interface SectionPlaceholderViewProps {
  section: SectionType;
  onLaunchMission: () => void;
  onReturnToCommandCenter: () => void;
}

interface SectionMeta {
  title: string;
  badge: string;
  badgeColor: string;
  icon: React.ReactNode;
  subtitle: string;
  overview: string;
  pillars: { label: string; desc: string }[];
  actionPrompt: string;
}

const SECTION_DATA: Record<SectionType, SectionMeta> = {
  learn: {
    title: 'Capability-Oriented Learning',
    badge: 'LEARN // DOMAINS',
    badgeColor: 'var(--accent-indigo)',
    icon: <BookOpen size={24} color="var(--accent-indigo)" />,
    subtitle: 'Structured cybersecurity knowledge tied directly to practical telemetry verification.',
    overview:
      'Unlike generic video course platforms, AEGORA structures cybersecurity learning around verifiable capabilities. Every theoretical concept in networking, operating systems, threat detection, and cloud security connects directly to an active investigation where you must separate truth from AI hallucination.',
    pillars: [
      {
        label: 'Cybersecurity Foundations & OS',
        desc: 'Linux internals, Windows authentication architecture, memory layout, and defensive hardening.'
      },
      {
        label: 'Networks & Protocols',
        desc: 'TCP/IP state machine, DNS exfiltration, TLS inspection, and firewall state tables.'
      },
      {
        label: 'SOC & Blue Team Operations',
        desc: 'Event ID 4624/4625 log parsing, SIEM correlation, alert prioritization, and initial triage.'
      },
      {
        label: 'Web & Cloud Security',
        desc: 'OWASP Top 10, IAM least-privilege, container security, and API authentication flows.'
      }
    ],
    actionPrompt: 'Test your understanding against an adversarial co-pilot in the SOC Range.'
  },
  practice: {
    title: 'Hands-On Defensive Practice',
    badge: 'PRACTICE // SANDBOX',
    badgeColor: 'var(--accent-cyan)',
    icon: <Terminal size={24} color="var(--accent-cyan)" />,
    subtitle: 'Controlled environments and realistic telemetry datasets for active defensive analysis.',
    overview:
      'Real cybersecurity skills cannot be developed through passive multiple-choice tests. In AEGORA practice environments, operators analyze raw authentication logs, correlate host and network telemetry, and formulate defensible containment strategies while evaluating AI assistant claims.',
    pillars: [
      {
        label: 'Authentication Log Triage',
        desc: 'Dissect logon types (Type 2 Interactive vs Type 3 Network vs Type 10 RemoteInteractive).'
      },
      {
        label: 'Adversarial Telemetry Correlation',
        desc: 'Cross-examine Kerberos ticket requests (Event 4768) against domain controller auth events.'
      },
      {
        label: 'Containment Action Formulation',
        desc: 'Formulate defensible mitigation steps while avoiding premature escalation or operational paralysis.'
      },
      {
        label: 'AI Hallucination Rejection',
        desc: 'Identify when automated co-pilots attribute attacks to unverified external indicators.'
      }
    ],
    actionPrompt: 'Begin your active practice session on the live SOC telemetry feed.'
  },
  bounty: {
    title: 'Safe Bug Bounty & Vulnerability Practice',
    badge: 'BUG BOUNTY // LABS',
    badgeColor: 'var(--accent-amber)',
    icon: <Bug size={24} color="var(--accent-amber)" />,
    subtitle: 'Controlled sandboxes for vulnerability discovery, evidence gathering, and responsible reporting.',
    overview:
      'AEGORA provides ethically safe, intentionally vulnerable sandbox targets for learning real-world vulnerability assessment. Follow the disciplined workflow: Target Recon → Vulnerability Finding → Evidence Capture → Verification → Responsible Report. No unauthorized target testing is permitted.',
    pillars: [
      {
        label: 'Sandboxed Target Reconnaissance',
        desc: 'Map application attack surfaces, parameter inputs, and authorization boundaries safely.'
      },
      {
        label: 'Evidence-Based Finding Validation',
        desc: 'Document HTTP request/response proofs confirming reproducibility without speculative claims.'
      },
      {
        label: 'AI Finding Verification',
        desc: 'Verify whether automated AI scanners have reported a true positive or a hallucinated false alarm.'
      },
      {
        label: 'Defensible Remediation Reporting',
        desc: 'Write clear vulnerability reports containing root-cause analysis and verifiable patches.'
      }
    ],
    actionPrompt: 'Sharpen your evidence-gathering skills in the Suspicious Login investigation.'
  },
  roadmap: {
    title: 'Cybersecurity Capability Roadmap',
    badge: 'ROADMAP // CAREER SIGNAL',
    badgeColor: 'var(--accent-emerald)',
    icon: <Compass size={24} color="var(--accent-emerald)" />,
    subtitle: 'A milestone-driven progression from cybersecurity foundations to expert defensive operations.',
    overview:
      'Move beyond static course certificates. The AEGORA roadmap maps your path through concrete proof-of-work milestones. Advancement requires demonstrating verified capabilities and overcoming targeted adaptive challenges when reasoning flaws are identified.',
    pillars: [
      {
        label: 'Phase 1: Foundations & Telemetry',
        desc: 'Mastery of raw system logs, operating system security controls, and authentication mechanisms.'
      },
      {
        label: 'Phase 2: Active Defense & Triage',
        desc: 'Rapid identification of brute-force patterns, password spray, and unauthorized lateral movement.'
      },
      {
        label: 'Phase 3: AI Co-Pilot Verification',
        desc: 'Independent auditing of machine intelligence claims to prevent automated confirmation bias.'
      },
      {
        label: 'Phase 4: Expert Incident Commander',
        desc: 'Full-spectrum incident triage, cryptographic proof generation, and tamper-resistant career records.'
      }
    ],
    actionPrompt: 'Advance your roadmap standing by earning your first verified capability proof.'
  },
  tools: {
    title: 'Defensive Cybersecurity Toolkit',
    badge: 'TOOLS // DEFENSIVE UTILITIES',
    badgeColor: 'var(--accent-cyan)',
    icon: <Wrench size={24} color="var(--accent-cyan)" />,
    subtitle: 'Educational utilities designed for rapid telemetry parsing, decoding, and triage.',
    overview:
      'Defenders need reliable utilities to deconstruct indicators of compromise. AEGORA provides lightweight, defensive browser-side tools to decode payloads, calculate cryptographic digests, inspect token payloads, and calculate network subnets without exposing sensitive data.',
    pillars: [
      {
        label: 'Multi-Format Encoders & Decoders',
        desc: 'Convert and inspect Base64, Hex, URL-encoded strings, and binary streams cleanly.'
      },
      {
        label: 'Cryptographic Hash Identifiers',
        desc: 'Identify and calculate SHA-256, SHA-1, and MD5 digests for file and telemetry verification.'
      },
      {
        label: 'JWT & Token Payload Inspector',
        desc: 'Decode JSON Web Token headers and claims with human-readable timestamp conversions.'
      },
      {
        label: 'Subnet & CIDR Range Calculator',
        desc: 'Calculate usable host ranges, broadcast addresses, and network boundaries for firewall rule audits.'
      }
    ],
    actionPrompt: 'Put these triage techniques to work inside the active SOC range.'
  },
  intelligence: {
    title: 'Defensive Cyber Intelligence & Briefings',
    badge: 'INTELLIGENCE // THREAT ANALYSIS',
    badgeColor: 'var(--accent-indigo)',
    icon: <Radio size={24} color="var(--accent-indigo)" />,
    subtitle: 'Incident post-mortems, attack telemetry dissections, and threat actor tactics.',
    overview:
      'Gain insight into modern cyber adversary tradecraft through educational incident analyses. We deconstruct real-world telemetry patterns and MITRE ATT&CK techniques, teaching you to distinguish actionable threat intelligence from speculative co-pilot conclusions.',
    pillars: [
      {
        label: 'Credential Access TTP Analysis',
        desc: 'Deep dives into password spraying, Kerberoasting, and token theft telemetry signatures.'
      },
      {
        label: 'Incident Autopsies & Lessons Learned',
        desc: 'Case studies showing where human analysts correctly intercepted attacks or fell into confirmation bias.'
      },
      {
        label: 'Defensive MITRE ATT&CK Mapping',
        desc: 'Correlating event IDs and network logs with specific adversarial tactics and techniques.'
      },
      {
        label: 'AI Disinformation & Attributions',
        desc: 'Why LLMs frequently misattribute threats based on keyword overlap rather than log proof.'
      }
    ],
    actionPrompt: 'Analyze live intelligence and telemetry in the active investigation.'
  }
};

export const SectionPlaceholderView: React.FC<SectionPlaceholderViewProps> = ({
  section,
  onLaunchMission,
  onReturnToCommandCenter
}) => {
  const data = SECTION_DATA[section];

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '32px 24px' }}>
      {/* Top Breadcrumb & Return */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          marginBottom: '20px'
        }}
      >
        <button
          onClick={onReturnToCommandCenter}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            fontSize: '13px',
            color: 'var(--accent-cyan)',
            backgroundColor: 'transparent',
            border: 'none',
            cursor: 'pointer',
            fontWeight: 600
          }}
        >
          <span>←</span> Return to Command Center
        </button>

        <span
          style={{
            fontSize: '11px',
            fontFamily: 'var(--font-mono)',
            padding: '3px 8px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: 'var(--bg-tertiary)',
            color: data.badgeColor,
            border: `1px solid ${data.badgeColor}`,
            fontWeight: 700
          }}
        >
          {data.badge}
        </span>
      </div>

      {/* Hero Header */}
      <div
        style={{
          padding: '28px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          marginBottom: '28px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
          <div
            style={{
              width: '48px',
              height: '48px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-strong)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0
            }}
          >
            {data.icon}
          </div>
          <div>
            <h1
              style={{
                fontSize: '24px',
                fontWeight: 800,
                letterSpacing: '-0.02em',
                marginBottom: '8px'
              }}
            >
              {data.title}
            </h1>
            <p
              style={{
                fontSize: '15px',
                color: 'var(--text-secondary)',
                lineHeight: 1.5,
                maxWidth: '850px'
              }}
            >
              {data.subtitle}
            </p>
          </div>
        </div>

        <div
          style={{
            marginTop: '20px',
            paddingTop: '20px',
            borderTop: '1px solid var(--border-subtle)',
            fontSize: '14px',
            color: 'var(--text-secondary)',
            lineHeight: 1.6
          }}
        >
          {data.overview}
        </div>
      </div>

      {/* Core Capability Pillars */}
      <div style={{ marginBottom: '28px' }}>
        <h2
          style={{
            fontSize: '14px',
            fontWeight: 700,
            textTransform: 'uppercase',
            letterSpacing: '0.06em',
            color: 'var(--text-muted)',
            marginBottom: '16px',
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <Cpu size={16} color="var(--accent-cyan)" />
          <span>Capability Modules & Focus Areas</span>
        </h2>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
            gap: '16px'
          }}
        >
          {data.pillars.map((pillar, idx) => (
            <div
              key={idx}
              style={{
                padding: '20px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between'
              }}
            >
              <div>
                <div
                  style={{
                    fontSize: '11px',
                    fontFamily: 'var(--font-mono)',
                    color: 'var(--text-muted)',
                    marginBottom: '6px'
                  }}
                >
                  MODULE 0{idx + 1}
                </div>
                <h3
                  style={{
                    fontSize: '15px',
                    fontWeight: 700,
                    marginBottom: '8px',
                    color: 'var(--text-primary)'
                  }}
                >
                  {pillar.label}
                </h3>
                <p
                  style={{
                    fontSize: '13px',
                    color: 'var(--text-secondary)',
                    lineHeight: 1.5
                  }}
                >
                  {pillar.desc}
                </p>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* AEGORA Core Innovation Callout & Action to Flagship */}
      <div
        style={{
          padding: '24px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-tertiary)',
          border: '1px solid var(--accent-cyan)',
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '20px'
        }}
      >
        <div style={{ maxWidth: '680px' }}>
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              fontSize: '11px',
              fontFamily: 'var(--font-mono)',
              color: 'var(--accent-cyan)',
              fontWeight: 700,
              textTransform: 'uppercase',
              marginBottom: '6px'
            }}
          >
            <ShieldCheck size={14} />
            <span>AEGORA Proof-of-Work Standard</span>
          </div>
          <h3 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '6px' }}>
            Don&apos;t just study cybersecurity — Prove you can operate with AI.
          </h3>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
            {data.actionPrompt} In our flagship mission, evaluate raw telemetry against an AI co-pilot claim, trigger authoritative server verification, and obtain cryptographic SHA-256 proof.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <button
            onClick={onLaunchMission}
            style={{
              padding: '12px 20px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--accent-cyan)',
              color: '#000',
              fontWeight: 700,
              fontSize: '13px',
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
              border: 'none',
              cursor: 'pointer',
              boxShadow: 'var(--shadow-md)'
            }}
          >
            <span>Launch Flagship Mission</span>
            <ArrowRight size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};
