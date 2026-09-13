import React, { useState } from 'react';
import {
  BookOpen,
  Terminal,
  Compass,
  Wrench,
  Radio,
  ArrowRight,
  Shield,
  Sparkles,
  Search,
  CheckCircle2,
  Cpu,
  Network,
  Lock,
  Globe,
  Cloud,
  Key,
  HardDrive,
  Eye,
  Send,
  MessageSquare
} from 'lucide-react';

export type SectionType =
  | 'learn'
  | 'practice'
  | 'ai_mentor'
  | 'bounty'
  | 'roadmap'
  | 'tools'
  | 'intelligence';

interface SectionPlaceholderViewProps {
  section: SectionType;
  onLaunchMission: () => void;
  onReturnToCommandCenter: () => void;
}

export const SectionPlaceholderView: React.FC<SectionPlaceholderViewProps> = ({
  section,
  onLaunchMission,
  onReturnToCommandCenter
}) => {
  // AI Mentor Interactive State
  const [mentorInput, setMentorInput] = useState('');
  const [mentorMessages, setMentorMessages] = useState<Array<{ sender: 'ai' | 'user'; text: string }>>([
    {
      sender: 'ai',
      text: 'Hello! I am your AEGORA cybersecurity co-pilot. Ask me to break down an attack vector, analyze an authentication log, or prepare for an investigation.'
    }
  ]);

  // Roadmap Interactive State
  const [selectedPhase, setSelectedPhase] = useState<number>(3); // SOC selected by default

  const handleSendMentor = (promptText?: string) => {
    const textToSend = promptText || mentorInput;
    if (!textToSend.trim()) return;

    setMentorMessages(prev => [
      ...prev,
      { sender: 'user', text: textToSend },
      {
        sender: 'ai',
        text: `Analysis: "${textToSend}". Remember that while automated tools highlight anomalies, true operational capability requires corroborating host telemetry with network packets. Ready to test this in a live mission?`
      }
    ]);
    setMentorInput('');
  };

  /* =========================================================================
     1. LEARN SCREEN (Violet #9B7CFF)
     ========================================================================= */
  if (section === 'learn') {
    const categories = [
      { id: 'net', title: 'NETWORKING', subtitle: 'Understand how systems communicate', progress: 72, icon: <Network size={20} /> },
      { id: 'lin', title: 'LINUX', subtitle: 'Command line & kernel internals', progress: 48, icon: <Terminal size={20} /> },
      { id: 'win', title: 'WINDOWS', subtitle: 'Active Directory & event logs', progress: 35, icon: <HardDrive size={20} /> },
      { id: 'py', title: 'PYTHON', subtitle: 'Automation & packet scripting', progress: 60, icon: <Cpu size={20} /> },
      { id: 'web', title: 'WEB SECURITY', subtitle: 'OWASP Top 10 & API defense', progress: 52, icon: <Globe size={20} /> },
      { id: 'cld', title: 'CLOUD', subtitle: 'IAM least-privilege & cloud audit', progress: 40, icon: <Cloud size={20} /> },
      { id: 'crypto', title: 'CRYPTOGRAPHY', subtitle: 'Ciphers & public key infrastructure', progress: 30, icon: <Key size={20} /> },
      { id: 'dfir', title: 'DIGITAL FORENSICS', subtitle: 'Memory triage & disk artifacts', progress: 45, icon: <Eye size={20} /> },
      { id: 'ai', title: 'AI SECURITY', subtitle: 'Model evasion & hallucination defense', progress: 85, icon: <Sparkles size={20} /> }
    ];

    return (
      <div style={{ maxWidth: '960px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            LEARN
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
            Build cybersecurity capability.
          </p>
        </div>

        {/* Categories Grid */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '16px'
          }}
        >
          {categories.map(cat => (
            <div
              key={cat.id}
              onClick={onLaunchMission}
              style={{
                padding: '24px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)',
                cursor: 'pointer',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between'
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--color-learn)', marginBottom: '12px' }}>
                  {cat.icon}
                  <span style={{ fontSize: '13px', fontWeight: 800, letterSpacing: '0.04em' }}>
                    {cat.title}
                  </span>
                </div>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.4, marginBottom: '20px' }}>
                  {cat.subtitle}
                </div>
              </div>

              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', fontWeight: 700, marginBottom: '6px' }}>
                  <span style={{ color: 'var(--text-muted)' }}>Progress</span>
                  <span style={{ color: 'var(--color-learn)' }}>{cat.progress}%</span>
                </div>
                <div style={{ width: '100%', height: '5px', borderRadius: '3px', backgroundColor: 'var(--bg-tertiary)', overflow: 'hidden' }}>
                  <div style={{ width: `${cat.progress}%`, height: '100%', backgroundColor: 'var(--color-learn)' }} />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  /* =========================================================================
     2. PRACTICE SCREEN (Emerald #35D39A)
     ========================================================================= */
  if (section === 'practice' || section === 'bounty') {
    const scenarios = [
      { id: 'soc', title: 'SOC INVESTIGATION', desc: 'Analyze suspicious authentication activity.', time: '12 min', diff: 'Intermediate' },
      { id: 'net', title: 'NETWORK FORENSICS', desc: 'Packet inspection and beacon detection.', time: '15 min', diff: 'Intermediate' },
      { id: 'web', title: 'WEB SECURITY', desc: 'Identify parameter tampering & injection.', time: '10 min', diff: 'Beginner' },
      { id: 'lin', title: 'LINUX FORENSICS', desc: 'Inspect malicious cron jobs & persistence.', time: '18 min', diff: 'Advanced' },
      { id: 'hunt', title: 'THREAT HUNT', desc: 'Search SIEM telemetry for lateral movement.', time: '20 min', diff: 'Advanced' },
      { id: 'dfir', title: 'DFIR', desc: 'Host memory extraction and triage.', time: '25 min', diff: 'Expert' }
    ];

    return (
      <div style={{ maxWidth: '960px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            PRACTICE
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
            Turn knowledge into action.
          </p>
        </div>

        {/* Clean Scenario Tiles */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '16px'
          }}
        >
          {scenarios.map(scen => (
            <div
              key={scen.id}
              style={{
                padding: '24px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between'
              }}
            >
              <div>
                <div style={{ fontSize: '14px', fontWeight: 800, color: 'var(--color-practice)', letterSpacing: '0.04em', marginBottom: '8px' }}>
                  {scen.title}
                </div>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5, marginBottom: '20px' }}>
                  {scen.desc}
                </div>
              </div>

              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '16px' }}>
                  <span>{scen.time}</span>
                  <span>•</span>
                  <span>{scen.diff}</span>
                </div>

                <button
                  onClick={onLaunchMission}
                  style={{
                    width: '100%',
                    padding: '10px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'rgba(53, 211, 154, 0.12)',
                    border: '1px solid var(--color-practice)',
                    color: 'var(--color-practice)',
                    fontSize: '13px',
                    fontWeight: 700,
                    cursor: 'pointer'
                  }}
                >
                  START
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  /* =========================================================================
     3. AI MENTOR SCREEN (Purple #B36BFF)
     ========================================================================= */
  if (section === 'ai_mentor') {
    const promptCards = [
      'Explain TCP handshake',
      'Analyze this log',
      'Prepare for SOC interview',
      'Review my investigation'
    ];

    return (
      <div style={{ maxWidth: '840px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--color-ai)', marginBottom: '8px' }}>
            <Sparkles size={18} />
            <span style={{ fontSize: '12px', fontWeight: 700, letterSpacing: '0.06em' }}>CO-PILOT</span>
          </div>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            AEGORA AI
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
            Your cybersecurity copilot.
          </p>
        </div>

        {/* Disclaimer Pill */}
        <div
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            padding: '6px 14px',
            borderRadius: 'var(--radius-full)',
            backgroundColor: 'rgba(179, 107, 255, 0.1)',
            border: '1px solid rgba(179, 107, 255, 0.25)',
            fontSize: '12px',
            color: 'var(--color-ai)',
            marginBottom: '24px'
          }}
        >
          <Shield size={13} />
          <span>AI ASSISTANCE • Human verification required.</span>
        </div>

        {/* Prompt Suggestions */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ fontSize: '13px', color: 'var(--text-muted)', marginBottom: '10px' }}>
            What are you working on?
          </div>
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
            {promptCards.map((p, idx) => (
              <button
                key={idx}
                onClick={() => handleSendMentor(p)}
                style={{
                  padding: '8px 14px',
                  borderRadius: 'var(--radius-full)',
                  backgroundColor: 'var(--bg-secondary)',
                  border: '1px solid var(--border-subtle)',
                  color: 'var(--text-primary)',
                  fontSize: '13px',
                  cursor: 'pointer'
                }}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        {/* Conversation Box */}
        <div
          style={{
            padding: '24px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)',
            marginBottom: '20px',
            minHeight: '280px',
            display: 'flex',
            flexDirection: 'column',
            gap: '16px'
          }}
        >
          {mentorMessages.map((msg, i) => (
            <div
              key={i}
              style={{
                alignSelf: msg.sender === 'user' ? 'flex-end' : 'flex-start',
                maxWidth: '85%',
                padding: '12px 16px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: msg.sender === 'user' ? 'var(--color-ai)' : 'var(--bg-tertiary)',
                color: msg.sender === 'user' ? '#FFFFFF' : 'var(--text-primary)',
                fontSize: '14px',
                lineHeight: 1.5
              }}
            >
              {msg.text}
            </div>
          ))}
        </div>

        {/* Input Bar */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <input
            type="text"
            value={mentorInput}
            onChange={e => setMentorInput(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSendMentor()}
            placeholder="Ask about logs, network attacks, or incident response..."
            style={{
              flex: 1,
              padding: '14px 18px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              fontSize: '14px',
              outline: 'none'
            }}
          />
          <button
            onClick={() => handleSendMentor()}
            style={{
              padding: '14px 20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--color-ai)',
              color: '#FFFFFF',
              fontWeight: 700,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              cursor: 'pointer'
            }}
          >
            <Send size={16} />
          </button>
        </div>
      </div>
    );
  }

  /* =========================================================================
     4. ROADMAP SCREEN (Amber #FFC857)
     ========================================================================= */
  if (section === 'roadmap') {
    const phases = [
      { id: 1, name: 'FOUNDATION', desc: 'Computing architecture, binary representation, and operating system kernels.' },
      { id: 2, name: 'NETWORKING', desc: 'Packet inspection, TCP/IP state machines, routing, and DNS.' },
      { id: 3, name: 'LINUX', desc: 'Privilege escalation vectors, cron persistence, and auditd logs.' },
      { id: 4, name: 'SOC', desc: 'Event 4624/4625 triage, alert correlation, and initial containment.' },
      { id: 5, name: 'INVESTIGATION', desc: 'Epistemic signal classification and AI co-pilot hallucination detection.' },
      { id: 6, name: 'INCIDENT RESPONSE', desc: 'Malware containment, eradication, and post-mortem reporting.' },
      { id: 7, name: 'VERIFIED CAPABILITY', desc: 'Cryptographically proven skill passport ready for employer verification.' }
    ];

    return (
      <div style={{ maxWidth: '840px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            SOC ANALYST
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--color-roadmap)', fontWeight: 600 }}>
            Your path
          </p>
        </div>

        {/* Visual Progression Path */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {phases.map((ph, idx) => {
            const isSelected = selectedPhase === ph.id;
            return (
              <div
                key={ph.id}
                onClick={() => setSelectedPhase(ph.id)}
                style={{
                  padding: '20px 24px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: isSelected ? 'var(--bg-secondary)' : 'transparent',
                  border: isSelected ? '1px solid var(--color-roadmap)' : '1px solid var(--border-subtle)',
                  cursor: 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  transition: 'all 0.15s ease'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <div
                    style={{
                      width: '28px',
                      height: '28px',
                      borderRadius: '50%',
                      backgroundColor: isSelected ? 'var(--color-roadmap)' : 'var(--bg-tertiary)',
                      color: isSelected ? '#000000' : 'var(--text-muted)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '12px',
                      fontWeight: 800
                    }}
                  >
                    {idx + 1}
                  </div>
                  <div>
                    <div style={{ fontSize: '15px', fontWeight: 700, color: isSelected ? 'var(--color-roadmap)' : 'var(--text-primary)' }}>
                      {ph.name}
                    </div>
                    {isSelected && (
                      <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
                        {ph.desc}
                      </div>
                    )}
                  </div>
                </div>

                <div style={{ fontSize: '12px', color: 'var(--text-muted)', fontWeight: 600 }}>
                  {idx < 3 ? 'Completed' : idx === 3 ? 'In Progress' : 'Upcoming'}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    );
  }

  /* =========================================================================
     5. INTELLIGENCE SCREEN (Orange #FF9F43)
     ========================================================================= */
  if (section === 'intelligence') {
    const news = [
      {
        id: 'n1',
        headline: 'Automated Log Parsing Tools Misattribute Legitimate Cloud Backups to External Threat Actors',
        why: 'Over-reliance on uncorroborated IP reputation feeds leads analysts to execute unnecessary containment disruptions.',
        technique: 'T1078 - Valid Accounts'
      },
      {
        id: 'n2',
        headline: 'Adversaries Exploit Kerberos Ticket Forwarding in Hybrid Entra Environments',
        why: 'Traditional SIEM rules looking only at domain controllers fail to catch cross-boundary token forging.',
        technique: 'T1558 - Steal or Forge Kerberos Tickets'
      },
      {
        id: 'n3',
        headline: 'Impossible Travel Detections Flooded by Commercial VPN & Satellite ISP Subnets',
        why: 'SOC operations suffer fatigue from heuristic alerts unless authenticated session tokens are verified.',
        technique: 'T1090 - Proxy'
      }
    ];

    return (
      <div style={{ maxWidth: '840px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            INTELLIGENCE
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
            Real-world threat intelligence.
          </p>
        </div>

        {/* Feed */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {news.map(item => (
            <div
              key={item.id}
              style={{
                padding: '24px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-intel)', letterSpacing: '0.05em', marginBottom: '8px' }}>
                {item.technique}
              </div>
              <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '8px', lineHeight: 1.4 }}>
                {item.headline}
              </h2>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5, marginBottom: '16px' }}>
                {item.why}
              </p>
              <button
                onClick={onLaunchMission}
                style={{
                  padding: '8px 16px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'rgba(255, 159, 67, 0.12)',
                  border: '1px solid var(--color-intel)',
                  color: 'var(--color-intel)',
                  fontSize: '13px',
                  fontWeight: 700,
                  cursor: 'pointer'
                }}
              >
                READ
              </button>
            </div>
          ))}
        </div>
      </div>
    );
  }

  /* =========================================================================
     6. TOOLS SCREEN (Sky Blue #4DC9FF)
     ========================================================================= */
  if (section === 'tools') {
    const taskGroups = [
      {
        task: 'SCAN',
        tools: [
          { name: 'Nmap', desc: 'Network exploration tool and security scanner' },
          { name: 'Masscan', desc: 'Fast asynchronous TCP port scanner' },
          { name: 'Nuclei', desc: 'Fast and customizable vulnerability scanner' }
        ]
      },
      {
        task: 'CAPTURE',
        tools: [
          { name: 'Wireshark', desc: 'Interactive packet analyzer and protocol decoder' },
          { name: 'tcpdump', desc: 'Command-line packet capture and filter utility' }
        ]
      },
      {
        task: 'WEB',
        tools: [
          { name: 'Burp Suite', desc: 'Web application security testing platform' },
          { name: 'OWASP ZAP', desc: 'Open-source web application security scanner' }
        ]
      },
      {
        task: 'FORENSICS',
        tools: [
          { name: 'Volatility', desc: 'Advanced memory forensics and artifact extraction' },
          { name: 'YARA', desc: 'Pattern matching engine for malware identification' }
        ]
      }
    ];

    return (
      <div style={{ maxWidth: '840px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
        {/* Header */}
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
            TOOLS
          </h1>
          <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
            Cybersecurity toolkits by operational domain.
          </p>
        </div>

        {/* Task Groups */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '28px' }}>
          {taskGroups.map(group => (
            <div key={group.task}>
              <div style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--color-tools)', marginBottom: '12px' }}>
                {group.task}
              </div>
              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
                  gap: '12px'
                }}
              >
                {group.tools.map(tool => (
                  <div
                    key={tool.name}
                    style={{
                      padding: '16px',
                      borderRadius: 'var(--radius-md)',
                      backgroundColor: 'var(--bg-secondary)',
                      border: '1px solid var(--border-subtle)'
                    }}
                  >
                    <div style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '4px' }}>
                      {tool.name}
                    </div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                      {tool.desc}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // Default fallback
  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '48px 24px', textAlign: 'center' }}>
      <button
        onClick={onReturnToCommandCenter}
        style={{
          padding: '10px 20px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--color-home)',
          color: '#FFFFFF',
          fontWeight: 700
        }}
      >
        Return to Home
      </button>
    </div>
  );
};
