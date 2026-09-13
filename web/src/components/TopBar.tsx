import React, { useState } from 'react';
import {
  Shield,
  Search,
  Bell,
  Sun,
  Moon,
  Lock,
  Menu,
  Sparkles,
  ChevronDown
} from 'lucide-react';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';
import { SubscriptionTier } from '../core/types/subscription';

interface TopBarProps {
  onOpenSearch: () => void;
  onOpenNotifications: () => void;
  onOpenPlanModal: () => void;
  onOpenSecurityStatus: () => void;
  theme: 'light' | 'dark';
  onToggleTheme: () => void;
  authState: AuthState;
  currentTier: SubscriptionTier;
  onToggleMobileMenu: () => void;
  onOpenProfile: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  onOpenSearch,
  onOpenNotifications,
  onOpenPlanModal,
  onOpenSecurityStatus,
  theme,
  onToggleTheme,
  authState,
  currentTier,
  onToggleMobileMenu,
  onOpenProfile
}) => {
  const learnerId = authState.identity?.canonicalLearnerId || 'operator_sundas';
  const displayName = learnerId.includes('@')
    ? learnerId.split('@')[0]
    : learnerId.startsWith('op_')
    ? 'Sundas'
    : learnerId;

  return (
    <header
      style={{
        height: '56px',
        backgroundColor: 'var(--bg-secondary)',
        borderBottom: '1px solid var(--border-subtle)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 20px',
        position: 'sticky',
        top: 0,
        zIndex: 40
      }}
    >
      {/* Left side: Mobile menu toggle + Quick Search Button */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <button
          onClick={onToggleMobileMenu}
          className="mobile-only-btn"
          style={{
            display: 'none',
            padding: '6px',
            color: 'var(--text-secondary)',
            borderRadius: 'var(--radius-sm)'
          }}
          title="Toggle Navigation"
        >
          <Menu size={20} />
        </button>

        {/* Global Search Bar Trigger */}
        <button
          onClick={onOpenSearch}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            padding: '6px 14px',
            backgroundColor: 'var(--bg-primary)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-md)',
            color: 'var(--text-secondary)',
            fontSize: '13px',
            minWidth: '220px',
            transition: 'all 0.15s ease'
          }}
        >
          <Search size={15} color="var(--text-muted)" />
          <span style={{ flex: 1, textAlign: 'left' }}>Search or command...</span>
          <kbd
            style={{
              fontSize: '10px',
              fontWeight: 700,
              padding: '2px 5px',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-sm)',
              color: 'var(--text-muted)'
            }}
          >
            ⌘K
          </kbd>
        </button>
      </div>

      {/* Right side: Plan Pill, Security Badge, Notifications, Theme, Profile */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        {/* Plan Clearance Pill */}
        <button
          onClick={onOpenPlanModal}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 10px',
            borderRadius: 'var(--radius-full)',
            backgroundColor:
              currentTier === 'CAREER'
                ? 'rgba(217, 154, 0, 0.12)'
                : currentTier === 'PRO'
                ? 'rgba(77, 141, 255, 0.12)'
                : 'var(--bg-tertiary)',
            border:
              currentTier === 'CAREER'
                ? '1px solid var(--color-proof)'
                : currentTier === 'PRO'
                ? '1px solid var(--color-home)'
                : '1px solid var(--border-subtle)',
            color:
              currentTier === 'CAREER'
                ? 'var(--color-proof)'
                : currentTier === 'PRO'
                ? 'var(--color-home)'
                : 'var(--text-secondary)',
            fontSize: '11px',
            fontWeight: 700,
            letterSpacing: '0.04em'
          }}
          title="Clearance Tier & Upgrade"
        >
          <Sparkles size={12} />
          <span>{currentTier} PLAN</span>
        </button>

        {/* Security / Verification Badge */}
        <button
          onClick={onOpenSecurityStatus}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 10px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-primary)',
            border: '1px solid var(--border-subtle)',
            fontSize: '12px',
            fontWeight: 600,
            color: 'var(--text-secondary)'
          }}
          title="Zero Trust Security Boundary"
        >
          <Lock size={12} color="var(--color-success)" />
          <span className="hide-on-mobile">VERIFIED</span>
        </button>

        {/* Notifications */}
        <button
          onClick={onOpenNotifications}
          style={{
            padding: '8px',
            borderRadius: 'var(--radius-md)',
            color: 'var(--text-secondary)',
            position: 'relative'
          }}
          title="Notifications"
        >
          <Bell size={17} />
          <span
            style={{
              position: 'absolute',
              top: '6px',
              right: '6px',
              width: '6px',
              height: '6px',
              backgroundColor: 'var(--color-investigate)',
              borderRadius: '50%'
            }}
          />
        </button>

        {/* Theme Toggle */}
        <button
          onClick={onToggleTheme}
          style={{
            padding: '8px',
            borderRadius: 'var(--radius-md)',
            color: 'var(--text-secondary)'
          }}
          title={theme === 'dark' ? 'Switch to Light SaaS Mode' : 'Switch to Dark Ops Mode'}
        >
          {theme === 'dark' ? <Sun size={17} /> : <Moon size={17} />}
        </button>

        {/* Operator Profile Chip */}
        <button
          onClick={onOpenProfile}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            padding: '4px 8px',
            borderRadius: 'var(--radius-md)',
            border: '1px solid var(--border-subtle)',
            backgroundColor: 'var(--bg-primary)'
          }}
        >
          <div
            style={{
              width: '24px',
              height: '24px',
              borderRadius: '50%',
              backgroundColor: 'rgba(77, 141, 255, 0.15)',
              color: 'var(--color-home)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '11px',
              fontWeight: 700
            }}
          >
            {displayName[0]?.toUpperCase() || 'S'}
          </div>
          <span style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
            {displayName}
          </span>
        </button>
      </div>
    </header>
  );
};
