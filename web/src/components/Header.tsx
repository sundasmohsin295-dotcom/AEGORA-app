import React from 'react';
import { Shield, Moon, Sun, Lock, Laptop, Terminal } from 'lucide-react';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';

export type AppView =
  | 'landing'
  | 'command_center'
  | 'learn'
  | 'practice'
  | 'mission'
  | 'bounty'
  | 'roadmap'
  | 'tools'
  | 'intelligence'
  | 'passport';

interface HeaderProps {
  currentView: AppView;
  onNavigate: (view: AppView) => void;
  theme: 'dark' | 'light';
  onToggleTheme: () => void;
  authState: AuthState;
  onOpenSecurityStatus: () => void;
}

interface NavItem {
  id: AppView;
  label: string;
  isFlagship?: boolean;
}

const NAV_ITEMS: NavItem[] = [
  { id: 'command_center', label: 'Command Center' },
  { id: 'learn', label: 'Learn' },
  { id: 'practice', label: 'Practice' },
  { id: 'mission', label: 'Investigate', isFlagship: true },
  { id: 'bounty', label: 'Bug Bounty' },
  { id: 'roadmap', label: 'Roadmap' },
  { id: 'tools', label: 'Tools' },
  { id: 'intelligence', label: 'Intelligence' },
  { id: 'passport', label: 'Proof' }
];

export const Header: React.FC<HeaderProps> = ({
  currentView,
  onNavigate,
  theme,
  onToggleTheme,
  authState,
  onOpenSecurityStatus
}) => {
  const operatorLabel = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'operator_guest_mode';

  return (
    <header
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '12px 24px',
        borderBottom: '1px solid var(--border-subtle)',
        backgroundColor: 'var(--bg-secondary)',
        position: 'sticky',
        top: 0,
        zIndex: 50
      }}
    >
      {/* Brand & Platform Identifier */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <button
          onClick={() => onNavigate('landing')}
          style={{ display: 'flex', alignItems: 'center', gap: '10px', textAlign: 'left' }}
        >
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--accent-cyan-subtle)',
              border: '1px solid var(--accent-cyan)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--accent-cyan)'
            }}
          >
            <Shield size={18} />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontWeight: 800, fontSize: '15px', letterSpacing: '0.05em' }}>
                AEGORA
              </span>
              <span
                style={{
                  fontSize: '10px',
                  fontFamily: 'var(--font-mono)',
                  padding: '2px 6px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--accent-indigo-subtle)',
                  color: 'var(--accent-indigo)',
                  fontWeight: 600
                }}
              >
                WEB CLIENT // v1.4
              </span>
            </div>
            <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
              Cybersecurity Capability OS
            </span>
          </div>
        </button>

        {/* Navigation Tabs */}
        <nav
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '4px',
            marginLeft: '12px',
            overflowX: 'auto',
            paddingBottom: '2px'
          }}
        >
          {NAV_ITEMS.map(item => {
            const isActive = currentView === item.id;
            const isFlagship = item.isFlagship;

            return (
              <button
                key={item.id}
                onClick={() => onNavigate(item.id)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: isFlagship ? '5px 12px' : '5px 10px',
                  fontSize: '12px',
                  fontWeight: isActive || isFlagship ? 700 : 500,
                  borderRadius: 'var(--radius-sm)',
                  color: isActive
                    ? 'var(--accent-cyan)'
                    : isFlagship
                    ? 'var(--text-primary)'
                    : 'var(--text-secondary)',
                  backgroundColor: isActive
                    ? 'var(--accent-cyan-subtle)'
                    : isFlagship
                    ? 'var(--bg-tertiary)'
                    : 'transparent',
                  border: isActive
                    ? '1px solid var(--accent-cyan)'
                    : isFlagship
                    ? '1px solid var(--border-strong)'
                    : '1px solid transparent',
                  cursor: 'pointer',
                  transition: 'all 0.15s ease',
                  whiteSpace: 'nowrap'
                }}
              >
                <span>{item.label}</span>
                {isFlagship && (
                  <span
                    style={{
                      fontSize: '9px',
                      fontFamily: 'var(--font-mono)',
                      fontWeight: 800,
                      padding: '1px 5px',
                      borderRadius: '2px',
                      backgroundColor: isActive ? 'var(--accent-cyan)' : 'var(--accent-cyan-subtle)',
                      color: isActive ? '#000' : 'var(--accent-cyan)',
                      letterSpacing: '0.04em'
                    }}
                  >
                    CORE
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Operator Status & Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        {/* Auth Honesty Status Badge */}
        <button
          onClick={onOpenSecurityStatus}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 10px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: 'var(--accent-amber-subtle)',
            border: '1px solid var(--accent-amber)',
            fontSize: '11px',
            fontFamily: 'var(--font-mono)',
            color: 'var(--accent-amber)',
            fontWeight: 600
          }}
          title="Click to view Security & Identity Architecture Details"
        >
          <Lock size={12} />
          <span>AUTH: BLOCKED (NO FIREBASE CONFIG)</span>
        </button>

        {/* Operator Badge */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 10px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-strong)',
            fontSize: '11px',
            fontFamily: 'var(--font-mono)',
            color: 'var(--text-primary)'
          }}
        >
          <Terminal size={12} color="var(--accent-cyan)" />
          <span>{operatorLabel}</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={onToggleTheme}
          aria-label="Toggle dark/light theme"
          style={{
            padding: '8px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-subtle)',
            color: 'var(--text-primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
        >
          {theme === 'dark' ? <Sun size={15} /> : <Moon size={15} />}
        </button>
      </div>
    </header>
  );
};
