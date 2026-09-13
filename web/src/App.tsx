import React, { useState, useEffect } from 'react';
import { Zap } from 'lucide-react';
import { NavigationSidebar, AppView } from './components/NavigationSidebar';
import { TopBar } from './components/TopBar';
import { GlobalSearchModal } from './components/GlobalSearchModal';
import { PlanUpgradeModal } from './components/PlanUpgradeModal';
import { NotificationsModal } from './components/NotificationsModal';
import { SecurityStatusModal } from './components/SecurityStatusModal';

import { LandingView } from './components/LandingView';
import { CommandCenterView } from './components/CommandCenterView';
import { MissionInvestigationView } from './components/MissionInvestigationView';
import { CareerPassportView } from './components/CareerPassportView';
import {
  LearnDashboard,
  RoadmapDashboard,
  PracticeDashboard,
  InvestigateDashboard,
  CyberRealityDashboard,
  AdaptiveAdversaryDashboard,
  IntelligenceDashboard,
  AiAnalystDashboard,
  ProofDashboard,
  ToolsDashboard,
  SettingsDashboard
} from './components/ModuleDashboards';

import { authClient, AuthState } from './core/auth/CrossPlatformAuthClient';
import { WebSubscriptionRepository } from './core/subscription/WebSubscriptionRepository';
import { SubscriptionTier } from './core/types/subscription';

export function App() {
  const [currentView, setCurrentView] = useState<AppView>('command_center');
  const [theme, setTheme] = useState<'light' | 'dark'>('light');
  const [authState, setAuthState] = useState<AuthState>(authClient.getState());
  const [currentTier, setCurrentTier] = useState<SubscriptionTier>('FREE');

  // Modals state
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [isPlanModalOpen, setIsPlanModalOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [isSecurityModalOpen, setIsSecurityModalOpen] = useState(false);
  const [isMobileDrawerOpen, setIsMobileDrawerOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [shortcutFeedback, setShortcutFeedback] = useState<string | null>(null);

  useEffect(() => {
    const unsubscribe = authClient.subscribe(state => setAuthState(state));
    return () => unsubscribe();
  }, []);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  // Auto-dismiss shortcut feedback after 2.2 seconds
  useEffect(() => {
    if (!shortcutFeedback) return;
    const timer = setTimeout(() => {
      setShortcutFeedback(null);
    }, 2200);
    return () => clearTimeout(timer);
  }, [shortcutFeedback]);

  // Global Keyboard Shortcuts Listener
  // - Alt+L: Navigate to 'Learn' dashboard
  // - Alt+P: Navigate to 'Practice' dashboard
  // - Alt+C: Navigate to 'Command Center'
  // - Alt+I: Navigate to 'Investigate' workspace
  // - Alt+A: Navigate to 'Adaptive Adversary'
  // - Alt+R: Navigate to 'Roadmap'
  // - Alt+T: Navigate to 'Tools'
  // - Alt+S: Navigate to 'Settings'
  // - Alt+M: Navigate to 'Active Mission'
  // - Alt+D: Navigate to 'Skill Passport / Dossier'
  // - Ctrl/Cmd + K: Toggle Command / Search Modal
  // - Escape: Dismiss open dialogs/drawers
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // 1. Search Modal (Ctrl/Cmd + K)
      if ((e.ctrlKey || e.metaKey) && (e.key.toLowerCase() === 'k' || e.code === 'KeyK')) {
        e.preventDefault();
        setIsSearchOpen(prev => !prev);
        return;
      }

      // 2. Escape to close open overlays
      if (e.key === 'Escape') {
        if (isSearchOpen || isPlanModalOpen || isNotificationsOpen || isSecurityModalOpen || isMobileDrawerOpen) {
          e.preventDefault();
          setIsSearchOpen(false);
          setIsPlanModalOpen(false);
          setIsNotificationsOpen(false);
          setIsSecurityModalOpen(false);
          setIsMobileDrawerOpen(false);
          return;
        }
      }

      // 3. Global Alt / Option navigation shortcuts
      if (e.altKey && !e.ctrlKey && !e.metaKey) {
        const key = e.key.toLowerCase();
        const code = e.code;

        let targetView: AppView | null = null;
        let label = '';

        if (key === 'l' || code === 'KeyL') {
          targetView = 'learn';
          label = 'Learn Dashboard [Alt+L]';
        } else if (key === 'p' || code === 'KeyP') {
          targetView = 'practice';
          label = 'Practice Dashboard [Alt+P]';
        } else if (key === 'c' || code === 'KeyC') {
          targetView = 'command_center';
          label = 'Command Center [Alt+C]';
        } else if (key === 'i' || code === 'KeyI') {
          targetView = 'investigate';
          label = 'Investigate Workspace [Alt+I]';
        } else if (key === 'a' || code === 'KeyA') {
          targetView = 'adversary';
          label = 'Adaptive Adversary [Alt+A]';
        } else if (key === 'r' || code === 'KeyR') {
          targetView = 'roadmap';
          label = 'Roadmap [Alt+R]';
        } else if (key === 't' || code === 'KeyT') {
          targetView = 'tools';
          label = 'Tools Dashboard [Alt+T]';
        } else if (key === 's' || code === 'KeyS') {
          targetView = 'settings';
          label = 'Settings [Alt+S]';
        } else if (key === 'm' || code === 'KeyM') {
          targetView = 'mission';
          label = 'Active Mission [Alt+M]';
        } else if (key === 'd' || code === 'KeyD') {
          targetView = 'passport';
          label = 'Skill Passport [Alt+D]';
        }

        if (targetView) {
          e.preventDefault();
          setCurrentView(targetView);
          setIsSearchOpen(false);
          setIsMobileDrawerOpen(false);
          setIsPlanModalOpen(false);
          setIsNotificationsOpen(false);
          setIsSecurityModalOpen(false);
          setShortcutFeedback(label);
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isSearchOpen, isPlanModalOpen, isNotificationsOpen, isSecurityModalOpen, isMobileDrawerOpen]);

  const toggleTheme = () => {
    setTheme(prev => (prev === 'light' ? 'dark' : 'light'));
  };

  const handleTierUpdated = (newTier: SubscriptionTier) => {
    setCurrentTier(newTier);
  };

  // If in landing view, render full width
  if (currentView === 'landing') {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <LandingView
          onLaunchCommandCenter={() => setCurrentView('command_center')}
          onLaunchMission={() => setCurrentView('mission')}
        />
      </div>
    );
  }

  return (
    <div
      style={{
        display: 'flex',
        minHeight: '100vh',
        backgroundColor: 'var(--bg-primary)',
        color: 'var(--text-primary)',
        fontFamily: 'var(--font-sans)'
      }}
    >
      {/* Desktop / Tablet Persistent Sidebar */}
      <aside className="desktop-sidebar" style={{ position: 'sticky', top: 0, height: '100vh', zIndex: 30 }}>
        <NavigationSidebar
          currentView={currentView}
          onNavigate={view => setCurrentView(view)}
          collapsed={sidebarCollapsed}
          onToggleCollapse={() => setSidebarCollapsed(prev => !prev)}
        />
      </aside>

      {/* Mobile Drawer (When Open) */}
      {isMobileDrawerOpen && (
        <NavigationSidebar
          currentView={currentView}
          onNavigate={view => setCurrentView(view)}
          isMobileDrawer={true}
          onCloseMobileDrawer={() => setIsMobileDrawerOpen(false)}
        />
      )}

      {/* Main Column */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        {/* TopBar */}
        <TopBar
          onOpenSearch={() => setIsSearchOpen(true)}
          onOpenNotifications={() => setIsNotificationsOpen(true)}
          onOpenPlanModal={() => setIsPlanModalOpen(true)}
          onOpenSecurityStatus={() => setIsSecurityModalOpen(true)}
          theme={theme}
          onToggleTheme={toggleTheme}
          authState={authState}
          currentTier={currentTier}
          onToggleMobileMenu={() => setIsMobileDrawerOpen(true)}
          onOpenProfile={() => setCurrentView('settings')}
        />

        {/* View Router */}
        <main style={{ flex: 1, overflowY: 'auto' }}>
          {currentView === 'command_center' && (
            <CommandCenterView
              authState={authState}
              onLaunchMission={mId => setCurrentView('mission')}
              onViewPassport={() => setCurrentView('passport')}
              onOpenSecurityModal={() => setIsSecurityModalOpen(true)}
              onNavigateSection={v => setCurrentView(v)}
            />
          )}

          {currentView === 'mission' && (
            <MissionInvestigationView
              onReturnToCommandCenter={() => setCurrentView('command_center')}
            />
          )}

          {currentView === 'learn' && (
            <LearnDashboard
              onNavigate={v => setCurrentView(v)}
              onLaunchMission={mId => setCurrentView('mission')}
            />
          )}

          {currentView === 'roadmap' && (
            <RoadmapDashboard onNavigate={v => setCurrentView(v)} />
          )}

          {currentView === 'practice' && (
            <PracticeDashboard
              onNavigate={v => setCurrentView(v)}
              onLaunchMission={mId => setCurrentView('mission')}
            />
          )}

          {currentView === 'investigate' && (
            <InvestigateDashboard
              onNavigate={v => setCurrentView(v)}
              onLaunchMission={mId => setCurrentView('mission')}
            />
          )}

          {currentView === 'cyber_reality' && (
            <CyberRealityDashboard
              onNavigate={v => setCurrentView(v)}
              onLaunchMission={mId => setCurrentView('mission')}
            />
          )}

          {currentView === 'adversary' && (
            <AdaptiveAdversaryDashboard
              onNavigate={v => setCurrentView(v)}
              onLaunchMission={mId => setCurrentView('mission')}
            />
          )}

          {currentView === 'intelligence' && (
            <IntelligenceDashboard onNavigate={v => setCurrentView(v)} />
          )}

          {(currentView === 'ai_analyst' || currentView === 'ai_mentor') && (
            <AiAnalystDashboard onNavigate={v => setCurrentView(v)} />
          )}

          {currentView === 'passport' && (
            <CareerPassportView
              authState={authState}
              onReturnToCommandCenter={() => setCurrentView('command_center')}
            />
          )}

          {currentView === 'verified_proof' && (
            <ProofDashboard onNavigate={v => setCurrentView(v)} />
          )}

          {currentView === 'tools' && (
            <ToolsDashboard onNavigate={v => setCurrentView(v)} />
          )}

          {currentView === 'settings' && (
            <SettingsDashboard
              authState={authState}
              currentTier={currentTier}
              onOpenPlanModal={() => setIsPlanModalOpen(true)}
            />
          )}
        </main>

        {/* Footer */}
        <footer
          style={{
            borderTop: '1px solid var(--border-subtle)',
            padding: '14px 24px',
            fontSize: '12px',
            color: 'var(--text-muted)',
            backgroundColor: 'var(--bg-secondary)',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: '8px'
          }}
        >
          <div>
            AEGORA Cyber Capability OS // Multi-Platform v1.4
          </div>
          <div style={{ display: 'flex', gap: '14px' }}>
            <span>Zero Client Trust</span>
            <span>•</span>
            <span style={{ color: 'var(--color-success)' }}>RevenueCat Verified</span>
            <span>•</span>
            <button
              onClick={() => setCurrentView('landing')}
              style={{ color: 'var(--text-muted)', textDecoration: 'underline' }}
            >
              Landing Page
            </button>
          </div>
        </footer>
      </div>

      {/* Modals */}
      <GlobalSearchModal
        isOpen={isSearchOpen}
        onClose={() => setIsSearchOpen(false)}
        onNavigate={view => setCurrentView(view)}
      />

      <PlanUpgradeModal
        isOpen={isPlanModalOpen}
        currentTier={currentTier}
        onClose={() => setIsPlanModalOpen(false)}
        onTierUpdated={handleTierUpdated}
      />

      <NotificationsModal
        isOpen={isNotificationsOpen}
        onClose={() => setIsNotificationsOpen(false)}
        onNavigate={view => setCurrentView(view)}
        onLaunchMission={() => setCurrentView('mission')}
      />

      <SecurityStatusModal
        isOpen={isSecurityModalOpen}
        onClose={() => setIsSecurityModalOpen(false)}
        authState={authState}
      />

      {/* Global Keyboard Shortcut Feedback Toast */}
      {shortcutFeedback && (
        <div
          role="status"
          aria-live="polite"
          style={{
            position: 'fixed',
            bottom: '24px',
            right: '24px',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-accent)',
            borderRadius: 'var(--radius-md)',
            padding: '10px 16px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            color: 'var(--text-primary)',
            fontSize: '13px',
            fontWeight: 600,
            boxShadow: '0 10px 30px rgba(0, 0, 0, 0.45)',
            zIndex: 100,
            pointerEvents: 'none'
          }}
        >
          <div
            style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: 'var(--color-home)'
            }}
          />
          <Zap size={14} color="var(--color-home)" />
          <span>{shortcutFeedback}</span>
        </div>
      )}
    </div>
  );
}

export default App;
