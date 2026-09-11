import React, { useState, useEffect } from 'react';
import { Header, AppView } from './components/Header';
import { LandingView } from './components/LandingView';
import { CommandCenterView } from './components/CommandCenterView';
import { MissionInvestigationView } from './components/MissionInvestigationView';
import { CareerPassportView } from './components/CareerPassportView';
import { SectionPlaceholderView } from './components/SectionPlaceholderView';
import { SecurityStatusModal } from './components/SecurityStatusModal';
import { authClient, AuthState } from './core/auth/CrossPlatformAuthClient';

export function App() {
  const [currentView, setCurrentView] = useState<AppView>('landing');
  const [theme, setTheme] = useState<'dark' | 'light'>('dark');
  const [authState, setAuthState] = useState<AuthState>(authClient.getState());
  const [isSecurityModalOpen, setIsSecurityModalOpen] = useState<boolean>(false);

  useEffect(() => {
    const unsubscribe = authClient.subscribe(state => setAuthState(state));
    return () => unsubscribe();
  }, []);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(prev => (prev === 'dark' ? 'light' : 'dark'));
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header
        currentView={currentView}
        onNavigate={view => setCurrentView(view)}
        theme={theme}
        onToggleTheme={toggleTheme}
        authState={authState}
        onOpenSecurityStatus={() => setIsSecurityModalOpen(true)}
      />

      <main style={{ flex: 1 }}>
        {currentView === 'landing' && (
          <LandingView
            onLaunchCommandCenter={() => setCurrentView('command_center')}
            onLaunchMission={() => setCurrentView('mission')}
          />
        )}

        {currentView === 'command_center' && (
          <CommandCenterView
            authState={authState}
            onLaunchMission={missionId => setCurrentView('mission')}
            onViewPassport={() => setCurrentView('passport')}
            onOpenSecurityModal={() => setIsSecurityModalOpen(true)}
          />
        )}

        {currentView === 'mission' && (
          <MissionInvestigationView
            onReturnToCommandCenter={() => setCurrentView('command_center')}
          />
        )}

        {currentView === 'passport' && (
          <CareerPassportView
            authState={authState}
            onReturnToCommandCenter={() => setCurrentView('command_center')}
          />
        )}

        {(currentView === 'learn' ||
          currentView === 'practice' ||
          currentView === 'bounty' ||
          currentView === 'roadmap' ||
          currentView === 'tools' ||
          currentView === 'intelligence') && (
          <SectionPlaceholderView
            section={currentView}
            onLaunchMission={() => setCurrentView('mission')}
            onReturnToCommandCenter={() => setCurrentView('command_center')}
          />
        )}
      </main>

      <footer
        style={{
          borderTop: '1px solid var(--border-subtle)',
          padding: '16px 24px',
          textAlign: 'center',
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
          AEGORA Cyber Capability OS // Multi-Platform Core Architecture v1.4
        </div>
        <div style={{ display: 'flex', gap: '16px' }}>
          <span>Android + Web Synchronized</span>
          <span>•</span>
          <span>Zero Client Trust</span>
          <span>•</span>
          <span style={{ color: 'var(--accent-amber)' }}>AUTH: BLOCKED (NO FIREBASE CONFIG)</span>
        </div>
      </footer>

      <SecurityStatusModal
        isOpen={isSecurityModalOpen}
        onClose={() => setIsSecurityModalOpen(false)}
        authState={authState}
      />
    </div>
  );
}

export default App;
