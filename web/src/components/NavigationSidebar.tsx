import React, { useState } from 'react';
import {
  Shield,
  LayoutDashboard,
  BookOpen,
  Map,
  Terminal,
  Search,
  Zap,
  Activity,
  Sparkles,
  Award,
  FileCheck2,
  Wrench,
  Settings,
  ChevronDown,
  ChevronRight,
  Flame,
  X
} from 'lucide-react';

export type AppView =
  | 'landing'
  | 'command_center'
  | 'learn'
  | 'roadmap'
  | 'practice'
  | 'investigate'
  | 'cyber_reality'
  | 'intelligence'
  | 'ai_analyst'
  | 'adversary'
  | 'passport'
  | 'verified_proof'
  | 'tools'
  | 'settings'
  | 'mission'
  | 'ai_mentor'
  | 'bounty';

interface NavItemDef {
  id: AppView;
  aliases?: AppView[];
  label: string;
  icon: React.ReactNode;
  color: string;
  badge?: string;
  shortcut?: string;
}

interface NavSectionDef {
  id: string;
  title: string;
  items: NavItemDef[];
}

const NAV_SECTIONS: NavSectionDef[] = [
  {
    id: 'command',
    title: 'COMMAND',
    items: [
      {
        id: 'command_center',
        label: 'Command Center',
        icon: <LayoutDashboard size={18} />,
        color: 'var(--color-home)',
        shortcut: 'Alt+C'
      }
    ]
  },
  {
    id: 'learn',
    title: 'LEARN',
    items: [
      {
        id: 'learn',
        label: 'Learn',
        icon: <BookOpen size={18} />,
        color: 'var(--color-learn)',
        shortcut: 'Alt+L'
      },
      {
        id: 'roadmap',
        label: 'Roadmap',
        icon: <Map size={18} />,
        color: 'var(--color-roadmap)',
        shortcut: 'Alt+R'
      }
    ]
  },
  {
    id: 'operate',
    title: 'OPERATE',
    items: [
      {
        id: 'practice',
        label: 'Practice',
        icon: <Terminal size={18} />,
        color: 'var(--color-practice)',
        shortcut: 'Alt+P'
      },
      {
        id: 'investigate',
        aliases: ['mission'],
        label: 'Investigate',
        icon: <Search size={18} />,
        color: 'var(--color-investigate)',
        badge: 'ACTIVE',
        shortcut: 'Alt+I'
      },
      {
        id: 'cyber_reality',
        label: 'Cyber Reality',
        icon: <Zap size={18} />,
        color: 'var(--color-investigate)'
      }
    ]
  },
  {
    id: 'intelligence',
    title: 'INTELLIGENCE',
    items: [
      {
        id: 'intelligence',
        label: 'Intelligence',
        icon: <Activity size={18} />,
        color: 'var(--color-intel)'
      },
      {
        id: 'ai_analyst',
        aliases: ['ai_mentor'],
        label: 'AI Analyst',
        icon: <Sparkles size={18} />,
        color: 'var(--color-ai)'
      },
      {
        id: 'adversary',
        label: 'Adaptive Adversary',
        icon: <Flame size={18} />,
        color: 'var(--color-error)',
        badge: 'RED TEAM',
        shortcut: 'Alt+A'
      }
    ]
  },
  {
    id: 'proof',
    title: 'PROOF',
    items: [
      {
        id: 'passport',
        label: 'Skill Passport',
        icon: <Award size={18} />,
        color: 'var(--color-proof)',
        shortcut: 'Alt+D'
      },
      {
        id: 'verified_proof',
        label: 'Verified Proof',
        icon: <FileCheck2 size={18} />,
        color: 'var(--color-proof)'
      }
    ]
  },
  {
    id: 'tools',
    title: 'TOOLS',
    items: [
      {
        id: 'tools',
        label: 'Tools',
        icon: <Wrench size={18} />,
        color: 'var(--color-tools)',
        shortcut: 'Alt+T'
      }
    ]
  },
  {
    id: 'account',
    title: 'ACCOUNT',
    items: [
      {
        id: 'settings',
        label: 'Settings',
        icon: <Settings size={18} />,
        color: 'var(--text-secondary)',
        shortcut: 'Alt+S'
      }
    ]
  }
];

interface NavigationSidebarProps {
  currentView: AppView;
  onNavigate: (view: AppView) => void;
  collapsed?: boolean;
  onToggleCollapse?: () => void;
  isMobileDrawer?: boolean;
  onCloseMobileDrawer?: () => void;
}

export const NavigationSidebar: React.FC<NavigationSidebarProps> = ({
  currentView,
  onNavigate,
  collapsed = false,
  isMobileDrawer = false,
  onCloseMobileDrawer
}) => {
  // Collapsed sections state
  const [collapsedSections, setCollapsedSections] = useState<Record<string, boolean>>({});

  const toggleSection = (sectionId: string) => {
    setCollapsedSections(prev => ({
      ...prev,
      [sectionId]: !prev[sectionId]
    }));
  };

  const isItemActive = (item: NavItemDef) => {
    if (currentView === item.id) return true;
    if (item.aliases && item.aliases.includes(currentView)) return true;
    return false;
  };

  const content = (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        backgroundColor: 'var(--bg-secondary)',
        borderRight: isMobileDrawer ? 'none' : '1px solid var(--border-subtle)',
        width: collapsed ? '68px' : '240px',
        transition: 'width 0.2s ease',
        userSelect: 'none'
      }}
    >
      {/* Brand Header */}
      <div
        style={{
          padding: collapsed ? '16px 14px' : '18px 20px',
          borderBottom: '1px solid var(--border-subtle)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between'
        }}
      >
        <div
          onClick={() => {
            onNavigate('command_center');
            onCloseMobileDrawer?.();
          }}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            cursor: 'pointer'
          }}
        >
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'rgba(77, 141, 255, 0.12)',
              border: '1px solid var(--color-home)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--color-home)',
              flexShrink: 0
            }}
          >
            <Shield size={18} />
          </div>
          {!collapsed && (
            <div>
              <div
                style={{
                  fontSize: '15px',
                  fontWeight: 800,
                  letterSpacing: '0.06em',
                  color: 'var(--text-primary)'
                }}
              >
                AEGORA
              </div>
              <div
                style={{
                  fontSize: '10px',
                  fontWeight: 600,
                  letterSpacing: '0.04em',
                  color: 'var(--text-muted)'
                }}
              >
                CYBERSECURITY OS
              </div>
            </div>
          )}
        </div>

        {isMobileDrawer && (
          <button
            onClick={onCloseMobileDrawer}
            style={{
              padding: '6px',
              color: 'var(--text-muted)',
              borderRadius: 'var(--radius-sm)'
            }}
          >
            <X size={18} />
          </button>
        )}
      </div>

      {/* Nav List */}
      <div
        style={{
          flex: 1,
          overflowY: 'auto',
          padding: collapsed ? '12px 6px' : '12px 10px'
        }}
      >
        {NAV_SECTIONS.map(section => {
          const isCollapsed = collapsedSections[section.id];
          return (
            <div key={section.id} style={{ marginBottom: '16px' }}>
              {!collapsed && (
                <div
                  onClick={() => toggleSection(section.id)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '4px 10px',
                    fontSize: '11px',
                    fontWeight: 700,
                    letterSpacing: '0.05em',
                    color: 'var(--text-muted)',
                    cursor: 'pointer',
                    borderRadius: 'var(--radius-sm)'
                  }}
                >
                  <span>{section.title}</span>
                  {isCollapsed ? <ChevronRight size={12} /> : <ChevronDown size={12} />}
                </div>
              )}

              {(!isCollapsed || collapsed) && (
                <div style={{ marginTop: '2px', display: 'flex', flexDirection: 'column', gap: '2px' }}>
                  {section.items.map(item => {
                    const active = isItemActive(item);
                    return (
                      <button
                        key={item.id}
                        title={collapsed ? `${item.label}${item.shortcut ? ` (${item.shortcut})` : ''}` : undefined}
                        onClick={() => {
                          onNavigate(item.id);
                          onCloseMobileDrawer?.();
                        }}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: collapsed ? 'center' : 'flex-start',
                          gap: '10px',
                          padding: collapsed ? '10px' : '8px 12px',
                          borderRadius: 'var(--radius-md)',
                          backgroundColor: active
                            ? 'var(--bg-tertiary)'
                            : 'transparent',
                          color: active ? item.color : 'var(--text-primary)',
                          fontWeight: active ? 600 : 500,
                          fontSize: '13px',
                          width: '100%',
                          textAlign: 'left',
                          transition: 'all 0.15s ease',
                          border: active ? '1px solid var(--border-subtle)' : '1px solid transparent'
                        }}
                      >
                        <span style={{ color: active ? item.color : 'var(--text-secondary)', display: 'flex' }}>
                          {item.icon}
                        </span>

                        {!collapsed && (
                          <div
                            style={{
                              flex: 1,
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'space-between',
                              gap: '6px'
                            }}
                          >
                            <span>{item.label}</span>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                              {item.badge && (
                                <span
                                  style={{
                                    fontSize: '9px',
                                    fontWeight: 700,
                                    padding: '1px 5px',
                                    borderRadius: 'var(--radius-full)',
                                    backgroundColor:
                                      item.badge === 'RED TEAM'
                                        ? 'rgba(240, 68, 85, 0.12)'
                                        : 'rgba(77, 141, 255, 0.12)',
                                    color:
                                      item.badge === 'RED TEAM'
                                        ? 'var(--color-error)'
                                        : 'var(--color-home)'
                                  }}
                                >
                                  {item.badge}
                                </span>
                              )}
                              {item.shortcut && (
                                <kbd
                                  style={{
                                    fontSize: '9px',
                                    fontFamily: 'monospace',
                                    fontWeight: 600,
                                    padding: '1px 4px',
                                    borderRadius: 'var(--radius-sm)',
                                    backgroundColor: 'rgba(255, 255, 255, 0.04)',
                                    border: '1px solid var(--border-subtle)',
                                    color: 'var(--text-muted)'
                                  }}
                                >
                                  {item.shortcut}
                                </kbd>
                              )}
                            </div>
                          </div>
                        )}
                      </button>
                    );
                  })}
                </div>
              )}
            </div>
          );
        })}
      </div>

      {/* Footer Operator Tag */}
      {!collapsed && (
        <div
          style={{
            padding: '12px 16px',
            borderTop: '1px solid var(--border-subtle)',
            fontSize: '11px',
            color: 'var(--text-muted)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          <span>CLEARANCE // LEVEL 2</span>
          <span
            style={{
              width: '6px',
              height: '6px',
              borderRadius: '50%',
              backgroundColor: 'var(--color-success)'
            }}
          />
        </div>
      )}
    </div>
  );

  if (isMobileDrawer) {
    return (
      <div
        style={{
          position: 'fixed',
          inset: 0,
          backgroundColor: 'rgba(17, 24, 39, 0.5)',
          backdropFilter: 'blur(3px)',
          zIndex: 90,
          display: 'flex'
        }}
        onClick={onCloseMobileDrawer}
      >
        <div
          style={{
            width: '280px',
            height: '100%',
            backgroundColor: 'var(--bg-secondary)',
            boxShadow: 'var(--shadow-lg)'
          }}
          onClick={e => e.stopPropagation()}
        >
          {content}
        </div>
      </div>
    );
  }

  return content;
};
