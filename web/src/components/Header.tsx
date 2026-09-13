import React, { useState, useRef, useEffect } from 'react';
import { Shield, Moon, Sun, Lock, Terminal, Sparkles, ChevronDown } from 'lucide-react';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';

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
  color: string;
}

const PRIMARY_NAV: NavItem[] = [
  { id: 'command_center', label: 'Command', color: 'var(--color-home)' },
  { id: 'learn', label: 'Learn', color: 'var(--color-learn)' },
  { id: 'practice', label: 'Operate', color: 'var(--color-practice)' },
  { id: 'intelligence', label: 'Intelligence', color: 'var(--color-intel)' },
  { id: 'passport', label: 'Proof', color: 'var(--color-proof)' }
];

const SECONDARY_NAV: NavItem[] = [
  { id: 'roadmap', label: 'Roadmap', color: 'var(--color-roadmap)' },
  { id: 'investigate', label: 'Investigate', color: 'var(--color-investigate)' },
  { id: 'cyber_reality', label: 'Cyber Reality', color: 'var(--color-investigate)' },
  { id: 'adversary', label: 'Adaptive Adversary', color: 'var(--color-error)' },
  { id: 'ai_analyst', label: 'AI Analyst', color: 'var(--color-ai)' },
  { id: 'verified_proof', label: 'Verified Proof', color: 'var(--color-proof)' },
  { id: 'tools', label: 'Tools', color: 'var(--color-tools)' },
  { id: 'settings', label: 'Settings', color: 'var(--text-secondary)' }
];

export const Header: React.FC<HeaderProps> = ({
  currentView,
  onNavigate,
  theme,
  onToggleTheme,
  authState,
  onOpenSecurityStatus
}) => {
  const [isMoreOpen, setIsMoreOpen] = useState(false);
  const moreRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (moreRef.current && !moreRef.current.contains(event.target as Node)) {
        setIsMoreOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const operatorLabel = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'operator';

  return (
    <header
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '12px 28px',
        borderBottom: '1px solid var(--border-subtle)',
        backgroundColor: 'var(--bg-secondary)',
        position: 'sticky',
        top: 0,
        zIndex: 50
      }}
    >
      {/* Brand & Primary Navigation */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '28px' }}>
        <button
          onClick={() => onNavigate('landing')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            textAlign: 'left'
          }}
        >
          <div
            style={{
              width: '32px',
              height: '32px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'rgba(77, 141, 255, 0.12)',
              border: '1px solid var(--color-home)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--color-home)'
            }}
          >
            <Shield size={18} />
          </div>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '8px' }}>
            <span
              style={{
                fontWeight: 800,
                fontSize: '17px',
                letterSpacing: '0.02em',
                color: 'var(--text-primary)'
              }}
            >
              AEGORA
            </span>
            <span
              style={{
                fontSize: '11px',
                fontWeight: 500,
                color: 'var(--text-muted)'
              }}
            >
              Cyber Reality
            </span>
          </div>
        </button>

        {/* Primary Clean Navigation */}
        <nav
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}
        >
          {PRIMARY_NAV.map(item => {
            const isActive = currentView === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onNavigate(item.id)}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: '6px 14px',
                  fontSize: '13px',
                  fontWeight: isActive ? 600 : 500,
                  borderRadius: 'var(--radius-full)',
                  color: isActive ? item.color : 'var(--text-secondary)',
                  backgroundColor: isActive
                    ? `color-mix(in srgb, ${item.color} 12%, transparent)`
                    : 'transparent',
                  border: isActive
                    ? `1px solid color-mix(in srgb, ${item.color} 30%, transparent)`
                    : '1px solid transparent',
                  cursor: 'pointer'
                }}
              >
                <span>{item.label}</span>
              </button>
            );
          })}

          {/* More Navigation Dropdown */}
          <div ref={moreRef} style={{ position: 'relative' }}>
            <button
              onClick={() => setIsMoreOpen(!isMoreOpen)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                padding: '6px 12px',
                fontSize: '13px',
                fontWeight: SECONDARY_NAV.some(s => s.id === currentView) ? 600 : 500,
                borderRadius: 'var(--radius-full)',
                color: SECONDARY_NAV.some(s => s.id === currentView)
                  ? 'var(--text-primary)'
                  : 'var(--text-secondary)',
                backgroundColor: SECONDARY_NAV.some(s => s.id === currentView)
                  ? 'var(--bg-tertiary)'
                  : 'transparent',
                border: '1px solid transparent'
              }}
            >
              <span>More</span>
              <ChevronDown size={13} />
            </button>

            {isMoreOpen && (
              <div
                style={{
                  position: 'absolute',
                  top: '110%',
                  left: 0,
                  minWidth: '160px',
                  backgroundColor: 'var(--bg-secondary)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-md)',
                  boxShadow: 'var(--shadow-lg)',
                  padding: '6px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '2px',
                  zIndex: 100
                }}
              >
                {SECONDARY_NAV.map(subItem => {
                  const isSubActive = currentView === subItem.id;
                  return (
                    <button
                      key={subItem.id}
                      onClick={() => {
                        onNavigate(subItem.id);
                        setIsMoreOpen(false);
                      }}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '8px 12px',
                        fontSize: '13px',
                        fontWeight: isSubActive ? 600 : 400,
                        color: isSubActive ? subItem.color : 'var(--text-secondary)',
                        backgroundColor: isSubActive
                          ? `color-mix(in srgb, ${subItem.color} 10%, transparent)`
                          : 'transparent',
                        borderRadius: 'var(--radius-sm)',
                        textAlign: 'left'
                      }}
                    >
                      <span>{subItem.label}</span>
                      {subItem.id === 'ai_mentor' && (
                        <Sparkles size={13} color="var(--color-ai)" />
                      )}
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        </nav>
      </div>

      {/* Operator Status & Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        {/* Auth Status Pill */}
        <button
          onClick={onOpenSecurityStatus}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 10px',
            borderRadius: 'var(--radius-full)',
            backgroundColor: 'rgba(255, 184, 77, 0.1)',
            border: '1px solid rgba(255, 184, 77, 0.3)',
            fontSize: '11px',
            color: 'var(--color-warning)',
            fontWeight: 500
          }}
          title="Security & System Details"
        >
          <Lock size={12} />
          <span>Local Authority</span>
        </button>

        {/* Operator Badge */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 12px',
            borderRadius: 'var(--radius-full)',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-subtle)',
            fontSize: '12px',
            color: 'var(--text-secondary)'
          }}
        >
          <Terminal size={12} color="var(--color-home)" />
          <span style={{ fontFamily: 'var(--font-mono)' }}>{operatorLabel}</span>
        </div>

        {/* Theme Toggle */}
        <button
          onClick={onToggleTheme}
          aria-label="Toggle dark/light theme"
          style={{
            width: '32px',
            height: '32px',
            borderRadius: 'var(--radius-full)',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-subtle)',
            color: 'var(--text-secondary)',
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
