import React, { useState, useEffect, useRef } from 'react';
import {
  Search,
  ArrowRight,
  Terminal,
  Shield,
  BookOpen,
  Cpu,
  Award,
  Settings,
  Flame,
  X,
  Compass,
  Zap,
  HelpCircle
} from 'lucide-react';
import { AppView } from './NavigationSidebar';

export interface SearchItem {
  id: string;
  title: string;
  category: 'Action' | 'Mission' | 'Skill' | 'Tool' | 'Module' | 'Proof';
  description: string;
  view: AppView;
  actionId?: string;
  shortcut?: string;
}

const SEARCH_CATALOG: SearchItem[] = [
  // Quick Actions
  {
    id: 'act_open_learn',
    title: 'Learn Dashboard: Interactive Cybersecurity Curriculum',
    category: 'Action',
    description: 'Security fundamentals, defensive engineering, and attack methodology modules',
    view: 'learn',
    shortcut: 'Alt+L'
  },
  {
    id: 'act_open_practice',
    title: 'Practice Dashboard: Live Cyber Ranges & Terminals',
    category: 'Action',
    description: 'Adversary emulation, attack-defense simulations, and threat ladders',
    view: 'practice',
    shortcut: 'Alt+P'
  },
  {
    id: 'act_start_mission',
    title: 'Start Mission: Suspicious Login Investigation',
    category: 'Action',
    description: 'Execute flagship AI-augmented cyber reality incident triage',
    view: 'mission',
    shortcut: 'Alt+M'
  },
  {
    id: 'act_open_investigation',
    title: 'Open Investigate Workspace',
    category: 'Action',
    description: 'View active and recommended incident triage cases',
    view: 'investigate',
    shortcut: 'Alt+I'
  },
  {
    id: 'act_open_adversary',
    title: 'Adaptive Adversary: Catch Flawed AI',
    category: 'Action',
    description: 'Challenge red team AI reasoning failures and catch premature conclusions',
    view: 'adversary',
    shortcut: 'Alt+A'
  },
  {
    id: 'act_view_intel',
    title: 'View Cognitive Intelligence & Cyber Twin',
    category: 'Action',
    description: 'Inspect cognitive capability clusters and failure autopsy patterns',
    view: 'intelligence'
  },
  {
    id: 'act_open_proof',
    title: 'Open Skill Passport & Verified Proof',
    category: 'Action',
    description: 'Inspect cryptographic proof artifacts and verified missions',
    view: 'passport'
  },
  {
    id: 'act_open_settings',
    title: 'Open Settings & Account',
    category: 'Action',
    description: 'Manage operator profile, appearance, and clearance tiers',
    view: 'settings'
  },

  // Missions & Investigations
  {
    id: 'mis_suspicious_login',
    title: 'Flagship: Suspicious Login Investigation',
    category: 'Mission',
    description: 'Sysmon Event ID 3 network telemetry vs AI analyst external compromise claim',
    view: 'mission'
  },
  {
    id: 'mis_powershell_c2',
    title: 'Investigation: Base64 Encoded PowerShell C2',
    category: 'Mission',
    description: 'Deobfuscate encoded stager and trace parent-child process tree',
    view: 'investigate'
  },
  {
    id: 'mis_impossible_travel',
    title: 'Investigation: Multi-Geo Impossible Travel Anomaly',
    category: 'Mission',
    description: 'Evaluate concurrent sessions from distinct autonomous systems',
    view: 'investigate'
  },

  // Skills
  {
    id: 'sk_sysmon',
    title: 'Skill: Sysmon Process & Network Telemetry',
    category: 'Skill',
    description: 'Event ID 1 process creation and Event ID 3 network socket tracking',
    view: 'learn'
  },
  {
    id: 'sk_ai_judgment',
    title: 'Skill: AI Hallucination & Evidence Grounding',
    category: 'Skill',
    description: 'Detect unsupported assertions and cognitive anchoring in AI claims',
    view: 'adversary'
  },
  {
    id: 'sk_active_directory',
    title: 'Skill: Windows Kerberos & NTLM Triage',
    category: 'Skill',
    description: 'Event ID 4624 logon types and anomalous ticket granting service requests',
    view: 'learn'
  },

  // Tools
  {
    id: 'tool_nmap',
    title: 'Tool: Nmap Network Mapper',
    category: 'Tool',
    description: 'Raw port scanner, service detection, and NSE script auditing',
    view: 'tools'
  },
  {
    id: 'tool_wireshark',
    title: 'Tool: Wireshark / TShark Packet Analysis',
    category: 'Tool',
    description: 'Deep PCAP protocol inspection and TCP stream reassembly',
    view: 'tools'
  },
  {
    id: 'tool_sigma',
    title: 'Tool: Sigma Detection Rule Engine',
    category: 'Tool',
    description: 'Generic log signature format convertable to Splunk, QRadar, Sentinel',
    view: 'tools'
  },
  {
    id: 'tool_volatility',
    title: 'Tool: Volatility 3 Memory Forensics',
    category: 'Tool',
    description: 'Kernel memory dump analysis, pslist, malfind, and netscan triage',
    view: 'tools'
  },

  // Modules
  {
    id: 'mod_networking',
    title: 'Module: TCP/IP & Network Protocols',
    category: 'Module',
    description: 'Foundations of packet framing, routing, and handshake states',
    view: 'learn'
  },
  {
    id: 'mod_soc_triage',
    title: 'Module: SOC Tier 1 Incident Triage',
    category: 'Module',
    description: 'Alert disposition, false positive reduction, and containment gates',
    view: 'learn'
  },

  // Proof
  {
    id: 'prf_sysmon_c2',
    title: 'Proof: Cryptographic Digest sha256:7f4ae91b',
    category: 'Proof',
    description: 'Authoritative signature verifying AI failure detection and independent triage',
    view: 'verified_proof'
  }
];

interface GlobalSearchModalProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (view: AppView) => void;
}

const RECENT_ACTIONS: SearchItem[] = [
  {
    id: 'rec_mission_login',
    title: 'Execute: Suspicious Login Investigation',
    category: 'Recent',
    description: 'Autonomous multi-stage triage: Sysmon Event ID 3 & 4624 telemetry',
    view: 'mission'
  },
  {
    id: 'rec_proof_dossier',
    title: 'Verify: Cryptographic Proof Dossier',
    category: 'Recent',
    description: 'Inspect SHA-256 digest sha256:7f4ae91b & immutable career evidence',
    view: 'verified_proof'
  },
  {
    id: 'rec_adversary_autopsy',
    title: 'Adversary: Premature Conclusion Autopsy',
    category: 'Recent',
    description: 'Analyze AI cognitive failure mode and review telemetry cross-check',
    view: 'adversary'
  }
];

export const GlobalSearchModal: React.FC<GlobalSearchModalProps> = ({
  isOpen,
  onClose,
  onNavigate
}) => {
  const [query, setQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(0);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 50);
      setSelectedIndex(0);
    } else {
      setQuery('');
    }
  }, [isOpen]);

  const displayedItems: SearchItem[] = React.useMemo(() => {
    if (!query.trim()) {
      // Innovation B: Display RECENT actions at the top, followed by PRIMARY QUICK ACTIONS
      const primaryQuickActions = SEARCH_CATALOG.filter(item => item.category === 'Action').slice(0, 5);
      return [...RECENT_ACTIONS, ...primaryQuickActions];
    }
    const q = query.toLowerCase();
    return SEARCH_CATALOG.filter(item => {
      return (
        item.title.toLowerCase().includes(q) ||
        item.description.toLowerCase().includes(q) ||
        item.category.toLowerCase().includes(q)
      );
    }).slice(0, 8);
  }, [query]);

  const filtered = displayedItems;

  useEffect(() => {
    setSelectedIndex(0);
  }, [query]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex(prev => (prev + 1) % Math.max(1, filtered.length));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex(prev => (prev - 1 + filtered.length) % Math.max(1, filtered.length));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (filtered[selectedIndex]) {
        onNavigate(filtered[selectedIndex].view);
        onClose();
      }
    } else if (e.key === 'Escape') {
      e.preventDefault();
      onClose();
    }
  };

  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(17, 24, 39, 0.45)',
        backdropFilter: 'blur(4px)',
        zIndex: 100,
        display: 'flex',
        alignItems: 'flex-start',
        justifyContent: 'center',
        paddingTop: '10vh',
        paddingLeft: '16px',
        paddingRight: '16px'
      }}
      onClick={onClose}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '580px',
          backgroundColor: 'var(--bg-secondary)',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-subtle)',
          boxShadow: 'var(--shadow-lg)',
          overflow: 'hidden'
        }}
        onClick={e => e.stopPropagation()}
      >
        {/* Search Input Bar */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '12px',
            padding: '16px 20px',
            borderBottom: '1px solid var(--border-subtle)'
          }}
        >
          <Search size={18} color="var(--text-muted)" />
          <input
            ref={inputRef}
            type="text"
            placeholder="Search missions, skills, tools, proof, actions... (↑↓ to select)"
            value={query}
            onChange={e => setQuery(e.target.value)}
            onKeyDown={handleKeyDown}
            style={{
              flex: 1,
              border: 'none',
              outline: 'none',
              backgroundColor: 'transparent',
              fontSize: '15px',
              color: 'var(--text-primary)',
              fontWeight: 500
            }}
          />
          <kbd
            style={{
              padding: '2px 6px',
              fontSize: '11px',
              fontWeight: 600,
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-sm)',
              color: 'var(--text-muted)'
            }}
          >
            ESC
          </kbd>
        </div>

        {/* Results List */}
        <div style={{ maxHeight: '380px', overflowY: 'auto', padding: '8px' }}>
          {filtered.length === 0 ? (
            <div
              style={{
                padding: '32px 16px',
                textAlign: 'center',
                color: 'var(--text-muted)',
                fontSize: '14px'
              }}
            >
              No matching commands or operations found for "{query}"
            </div>
          ) : (
            filtered.map((item, idx) => {
              const isSelected = idx === selectedIndex;
              return (
                <div
                  key={item.id}
                  onClick={() => {
                    onNavigate(item.view);
                    onClose();
                  }}
                  onMouseEnter={() => setSelectedIndex(idx)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '10px 14px',
                    borderRadius: 'var(--radius-md)',
                    backgroundColor: isSelected ? 'var(--bg-tertiary)' : 'transparent',
                    cursor: 'pointer',
                    transition: 'background-color 0.15s ease'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px', minWidth: 0 }}>
                    <span
                      style={{
                        fontSize: '10px',
                        fontWeight: 700,
                        textTransform: 'uppercase',
                        padding: '2px 6px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor:
                          item.category === 'Recent'
                            ? 'rgba(139, 92, 246, 0.14)'
                            : item.category === 'Action'
                            ? 'rgba(77, 141, 255, 0.12)'
                            : item.category === 'Mission'
                            ? 'rgba(240, 68, 85, 0.12)'
                            : item.category === 'Tool'
                            ? 'rgba(14, 165, 233, 0.12)'
                            : item.category === 'Proof'
                            ? 'rgba(217, 154, 0, 0.12)'
                            : 'var(--bg-primary)',
                        color:
                          item.category === 'Recent'
                            ? 'var(--color-learn)'
                            : item.category === 'Action'
                            ? 'var(--color-home)'
                            : item.category === 'Mission'
                            ? 'var(--color-investigate)'
                            : item.category === 'Tool'
                            ? 'var(--color-tools)'
                            : item.category === 'Proof'
                            ? 'var(--color-proof)'
                            : 'var(--text-secondary)'
                      }}
                    >
                      {item.category}
                    </span>
                    <div style={{ overflow: 'hidden' }}>
                      <div
                        style={{
                          fontSize: '14px',
                          fontWeight: 600,
                          color: 'var(--text-primary)',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}
                      >
                        {item.title}
                      </div>
                      <div
                        style={{
                          fontSize: '12px',
                          color: 'var(--text-muted)',
                          whiteSpace: 'nowrap',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis'
                        }}
                      >
                        {item.description}
                      </div>
                    </div>
                  </div>
                  {isSelected && (
                    <ArrowRight size={14} color="var(--color-home)" style={{ flexShrink: 0 }} />
                  )}
                </div>
              );
            })
          )}
        </div>

        {/* Footer shortcuts */}
        <div
          style={{
            padding: '8px 16px',
            backgroundColor: 'var(--bg-tertiary)',
            borderTop: '1px solid var(--border-subtle)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            fontSize: '11px',
            color: 'var(--text-muted)'
          }}
        >
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
            <span>↑↓ Navigate</span>
            <span>↵ Select</span>
            <span style={{ color: 'var(--color-learn)' }}>Alt+L Learn</span>
            <span style={{ color: 'var(--color-practice)' }}>Alt+P Practice</span>
            <span>ESC Close</span>
          </div>
          <span style={{ fontWeight: 600 }}>AEGORA Command Engine</span>
        </div>
      </div>
    </div>
  );
};
