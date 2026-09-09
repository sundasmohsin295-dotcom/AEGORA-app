import { describe, it, expect, beforeEach } from 'vitest';
import { WebSubscriptionRepository } from '../src/core/subscription/WebSubscriptionRepository';
import { SubscriptionTier } from '../src/core/types/subscription';

describe('WebSubscriptionRepository & Feature Access Control', () => {
  let repo: WebSubscriptionRepository;

  beforeEach(() => {
    repo = WebSubscriptionRepository.getInstance();
    repo.resetIdentity();
  });

  it('provides available subscription packages', () => {
    const packages = repo.getAvailablePackages();
    expect(packages.length).toBeGreaterThanOrEqual(2);
    expect(packages.some(p => p.tier === 'PRO')).toBe(true);
    expect(packages.some(p => p.tier === 'CAREER')).toBe(true);
  });

  it('correctly gates features based on subscription tier', () => {
    // FREE tier checks
    const freeBase = repo.checkFeatureAccess('FOUNDATIONAL_MISSIONS', 'FREE');
    expect(freeBase.granted).toBe(true);

    const freeProCheck = repo.checkFeatureAccess('LIVE_SOC_SHIFT', 'FREE');
    expect(freeProCheck.granted).toBe(false);
    expect(freeProCheck.requiredTier).toBe('PRO');

    const freeCareerCheck = repo.checkFeatureAccess('PURPLE_TEAM_ARENA', 'FREE');
    expect(freeCareerCheck.granted).toBe(false);
    expect(freeCareerCheck.requiredTier).toBe('CAREER');

    // PRO tier checks
    const proSocCheck = repo.checkFeatureAccess('LIVE_SOC_SHIFT', 'PRO');
    expect(proSocCheck.granted).toBe(true);

    const proCareerCheck = repo.checkFeatureAccess('PURPLE_TEAM_ARENA', 'PRO');
    expect(proCareerCheck.granted).toBe(false);

    // CAREER tier checks
    const careerSocCheck = repo.checkFeatureAccess('LIVE_SOC_SHIFT', 'CAREER');
    expect(careerSocCheck.granted).toBe(true);

    const careerArenaCheck = repo.checkFeatureAccess('PURPLE_TEAM_ARENA', 'CAREER');
    expect(careerArenaCheck.granted).toBe(true);
  });

  it('clears state on resetIdentity when switching accounts', () => {
    let notified = false;
    const unsub = repo.listenToSubscriptionState('user_1', () => {
      notified = true;
    });

    repo.resetIdentity();
    unsub();
    expect(notified).toBe(false);
  });
});
