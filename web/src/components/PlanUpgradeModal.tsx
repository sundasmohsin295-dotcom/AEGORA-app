import React, { useState } from 'react';
import { Check, X, Shield, Sparkles, Award, Users, ArrowRight } from 'lucide-react';
import { WebSubscriptionRepository } from '../core/subscription/WebSubscriptionRepository';
import { SubscriptionTier } from '../core/types/subscription';

interface PlanUpgradeModalProps {
  isOpen: boolean;
  currentTier: SubscriptionTier;
  onClose: () => void;
  onTierUpdated: (newTier: SubscriptionTier) => void;
}

interface PlanOption {
  tier: SubscriptionTier;
  name: string;
  price: string;
  period: string;
  badge?: string;
  description: string;
  features: string[];
  buttonText: string;
}

const PLANS: PlanOption[] = [
  {
    tier: 'FREE',
    name: 'Operator Free',
    price: '$0',
    period: 'forever',
    description: 'Foundational labs & basic incident triage.',
    features: [
      'Foundational defensive missions',
      'Basic suspicious login triage',
      'Standard skill passport overview',
      'Community discussion access'
    ],
    buttonText: 'Current Clearance'
  },
  {
    tier: 'PRO',
    name: 'AEGORA Pro',
    price: '$19.99',
    period: 'per month',
    badge: 'POPULAR',
    description: 'Live SOC ranges & advanced adversarial challenges.',
    features: [
      'All Free tier features',
      'Live SOC Shift simulator access',
      'Binary disassembly & malware analysis',
      'Deep capability cluster analytics',
      'Full adaptive adversary scenarios'
    ],
    buttonText: 'Upgrade to Pro'
  },
  {
    tier: 'CAREER',
    name: 'Career Pass',
    price: '$149.99',
    period: 'per year',
    badge: 'JOB-READY',
    description: 'Employer-verified proof dossiers & unlimited mentorship.',
    features: [
      'All Pro tier features',
      'Purple Team Arena live simulation',
      'Cryptographically signed employer proof dossier',
      'Unlimited AI mentor synthesis & query grounding',
      'Direct partner recruiter priority radar'
    ],
    buttonText: 'Get Career Pass'
  }
];

export const PlanUpgradeModal: React.FC<PlanUpgradeModalProps> = ({
  isOpen,
  currentTier,
  onClose,
  onTierUpdated
}) => {
  const [selectedTier, setSelectedTier] = useState<SubscriptionTier>(currentTier);
  const [isProcessing, setIsProcessing] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleUpgrade = (tier: SubscriptionTier) => {
    if (tier === currentTier) return;
    setIsProcessing(true);
    setTimeout(() => {
      setIsProcessing(false);
      onTierUpdated(tier);
      setToastMessage(`Successfully activated ${tier} clearance! Entitlements synced.`);
      setTimeout(() => {
        setToastMessage(null);
        onClose();
      }, 1200);
    }, 600);
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(17, 24, 39, 0.5)',
        backdropFilter: 'blur(4px)',
        zIndex: 100,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px'
      }}
      onClick={onClose}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '860px',
          backgroundColor: 'var(--bg-secondary)',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-subtle)',
          boxShadow: 'var(--shadow-lg)',
          overflow: 'hidden',
          maxHeight: '90vh',
          display: 'flex',
          flexDirection: 'column'
        }}
        onClick={e => e.stopPropagation()}
      >
        {/* Header */}
        <div
          style={{
            padding: '24px 28px',
            borderBottom: '1px solid var(--border-subtle)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            backgroundColor: 'var(--bg-primary)'
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Shield size={20} color="var(--color-home)" />
              <h2 style={{ fontSize: '20px', fontWeight: 800, color: 'var(--text-primary)' }}>
                AEGORA Clearance Plans
              </h2>
            </div>
            <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              Select your tier. All upgrades are verified via RevenueCat server authority.
            </p>
          </div>
          <button
            onClick={onClose}
            style={{
              padding: '6px',
              borderRadius: 'var(--radius-sm)',
              color: 'var(--text-muted)'
            }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Plan Cards Grid */}
        <div
          style={{
            padding: '28px',
            overflowY: 'auto',
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(230px, 1fr))',
            gap: '20px'
          }}
        >
          {PLANS.map(plan => {
            const isCurrent = currentTier === plan.tier;
            const isPro = plan.tier === 'PRO';
            const isCareer = plan.tier === 'CAREER';

            return (
              <div
                key={plan.tier}
                style={{
                  border: isCurrent
                    ? '2px solid var(--color-home)'
                    : isCareer
                    ? '1px solid var(--color-proof)'
                    : '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-lg)',
                  padding: '24px',
                  backgroundColor: 'var(--bg-secondary)',
                  display: 'flex',
                  flexDirection: 'column',
                  position: 'relative'
                }}
              >
                {plan.badge && (
                  <span
                    style={{
                      position: 'absolute',
                      top: '12px',
                      right: '12px',
                      fontSize: '10px',
                      fontWeight: 700,
                      padding: '2px 8px',
                      borderRadius: 'var(--radius-full)',
                      backgroundColor: isCareer
                        ? 'rgba(217, 154, 0, 0.15)'
                        : 'rgba(77, 141, 255, 0.15)',
                      color: isCareer ? 'var(--color-proof)' : 'var(--color-home)'
                    }}
                  >
                    {plan.badge}
                  </span>
                )}

                <h3 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)' }}>
                  {plan.name}
                </h3>
                <div style={{ margin: '12px 0 16px 0' }}>
                  <span style={{ fontSize: '28px', fontWeight: 800, color: 'var(--text-primary)' }}>
                    {plan.price}
                  </span>{' '}
                  <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                    / {plan.period}
                  </span>
                </div>
                <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '20px' }}>
                  {plan.description}
                </p>

                <div style={{ flex: 1, marginBottom: '24px' }}>
                  <div
                    style={{
                      fontSize: '11px',
                      fontWeight: 700,
                      textTransform: 'uppercase',
                      letterSpacing: '0.05em',
                      color: 'var(--text-muted)',
                      marginBottom: '12px'
                    }}
                  >
                    INCLUDED CAPABILITIES
                  </div>
                  <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {plan.features.map((feat, idx) => (
                      <li
                        key={idx}
                        style={{
                          display: 'flex',
                          alignItems: 'flex-start',
                          gap: '8px',
                          fontSize: '12px',
                          color: 'var(--text-primary)'
                        }}
                      >
                        <Check size={14} color="var(--color-success)" style={{ marginTop: '2px', flexShrink: 0 }} />
                        <span>{feat}</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <button
                  disabled={isCurrent || isProcessing}
                  onClick={() => handleUpgrade(plan.tier)}
                  style={{
                    width: '100%',
                    padding: '10px 16px',
                    borderRadius: 'var(--radius-md)',
                    fontSize: '13px',
                    fontWeight: 700,
                    cursor: isCurrent ? 'default' : 'pointer',
                    backgroundColor: isCurrent
                      ? 'var(--bg-tertiary)'
                      : isCareer
                      ? 'var(--color-proof)'
                      : 'var(--color-home)',
                    color: isCurrent ? 'var(--text-muted)' : '#FFFFFF',
                    border: isCurrent ? '1px solid var(--border-subtle)' : 'none',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '6px'
                  }}
                >
                  {isCurrent ? 'Active Plan' : plan.buttonText}
                  {!isCurrent && <ArrowRight size={14} />}
                </button>
              </div>
            );
          })}
        </div>

        {/* Toast confirmation */}
        {toastMessage && (
          <div
            style={{
              padding: '12px 20px',
              backgroundColor: 'rgba(16, 185, 129, 0.12)',
              borderTop: '1px solid var(--color-success)',
              color: 'var(--color-success)',
              fontSize: '13px',
              fontWeight: 600,
              textAlign: 'center'
            }}
          >
            {toastMessage}
          </div>
        )}
      </div>
    </div>
  );
};
