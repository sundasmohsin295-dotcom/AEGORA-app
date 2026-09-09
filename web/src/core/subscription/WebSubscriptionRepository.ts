import { SubscriptionTier, AuthoritativeSubscriptionState, SubscriptionFeatureCheck, SubscriptionPackage } from '../types/subscription';
import { getFunctions, httpsCallable } from 'firebase/functions';
import { getFirestore, doc, getDoc, onSnapshot, Unsubscribe } from 'firebase/firestore';

export class WebSubscriptionRepository {
  private static instance: WebSubscriptionRepository;
  private currentSubscription: AuthoritativeSubscriptionState | null = null;
  private subscribers: Set<(sub: AuthoritativeSubscriptionState) => void> = new Set();
  private activeFirestoreUnsub: Unsubscribe | null = null;

  private constructor() {}

  public static getInstance(): WebSubscriptionRepository {
    if (!WebSubscriptionRepository.instance) {
      WebSubscriptionRepository.instance = new WebSubscriptionRepository();
    }
    return WebSubscriptionRepository.instance;
  }

  public getAvailablePackages(): SubscriptionPackage[] {
    return [
      {
        identifier: 'aegora_pro_monthly',
        packageType: 'MONTHLY',
        tier: 'PRO',
        title: 'AEGORA Pro Monthly',
        description: 'Full access to Live SOC Shifts, Deep Binary Disassembly, and Multi-Gate Radar.',
        priceString: '$19.99 / month'
      },
      {
        identifier: 'aegora_career_annual',
        packageType: 'ANNUAL',
        tier: 'CAREER',
        title: 'AEGORA Career Pass Annual',
        description: 'Complete Purple Team Arena, 1-on-1 AI Mentorship, Employer Verified Dossiers, and Priority Radar.',
        priceString: '$149.99 / year'
      }
    ];
  }

  public async syncWithServerAuthority(authUid: string): Promise<AuthoritativeSubscriptionState> {
    try {
      const functions = getFunctions();
      const callable = httpsCallable<{ targetAuthUid: string }, AuthoritativeSubscriptionState>(
        functions,
        'getOrSyncSubscriptionState'
      );
      const res = await callable({ targetAuthUid: authUid });
      this.currentSubscription = res.data;
      this.notifySubscribers(this.currentSubscription);
      return res.data;
    } catch (e) {
      // Fallback to local default if offline or blocked
      const fallback: AuthoritativeSubscriptionState = {
        ownerAuthUid: authUid,
        tier: 'FREE',
        active: true,
        entitlementIdentifiers: [],
        productIdentifier: null,
        expiresAt: null,
        provider: 'SYSTEM_DEFAULT',
        customerId: authUid,
        checkedAt: new Date().toISOString(),
        authorityMetadata: {
          authoritySource: 'SERVER',
          verifiedAt: new Date().toISOString(),
          verifiedBy: 'WebSubscriptionRepositoryFallback',
          algorithmVersion: '2.0.0-revenuecat',
          sourceEvidenceIds: ['OFFLINE_OR_UNAVAILABLE']
        }
      };
      this.currentSubscription = fallback;
      this.notifySubscribers(fallback);
      return fallback;
    }
  }

  public listenToSubscriptionState(
    authUid: string,
    callback: (sub: AuthoritativeSubscriptionState) => void
  ): () => void {
    this.subscribers.add(callback);

    if (this.currentSubscription && this.currentSubscription.ownerAuthUid === authUid) {
      callback(this.currentSubscription);
    }

    // Attach Firestore listener if Firestore is available
    if (!this.activeFirestoreUnsub) {
      try {
        const db = getFirestore();
        const subDoc = doc(db, `learners/${authUid}/subscription/current`);
        this.activeFirestoreUnsub = onSnapshot(subDoc, (snapshot) => {
          if (snapshot.exists()) {
            const data = snapshot.data() as AuthoritativeSubscriptionState;
            this.currentSubscription = data;
            this.notifySubscribers(data);
          }
        });
      } catch (err) {
        // Safe degrade if firestore not initialized
      }
    }

    return () => {
      this.subscribers.delete(callback);
      if (this.subscribers.size === 0 && this.activeFirestoreUnsub) {
        this.activeFirestoreUnsub();
        this.activeFirestoreUnsub = null;
      }
    };
  }

  public checkFeatureAccess(featureKey: string, tierOverride?: SubscriptionTier): SubscriptionFeatureCheck {
    const tier = tierOverride || this.currentSubscription?.tier || 'FREE';

    // Feature permission hierarchy:
    // FREE: foundational labs, basic triage, skill radar intro
    // PRO: live soc shifts, binary disassembly, advanced labs
    // CAREER: purple team war room, employer dossier export, unlimited AI mentor synthesis
    switch (featureKey) {
      case 'LIVE_SOC_SHIFT':
      case 'BINARY_DISASSEMBLER':
      case 'FULL_SKILL_RADAR':
        return {
          featureKey,
          requiredTier: 'PRO',
          granted: tier === 'PRO' || tier === 'CAREER',
          reason: tier === 'FREE' ? 'Requires AEGORA Pro or Career Pass.' : undefined
        };

      case 'PURPLE_TEAM_ARENA':
      case 'CAREER_DOSSIER_EXPORT':
      case 'UNLIMITED_MENTOR_SYNTHESIS':
        return {
          featureKey,
          requiredTier: 'CAREER',
          granted: tier === 'CAREER',
          reason: tier !== 'CAREER' ? 'Requires AEGORA Career Pass.' : undefined
        };

      case 'FOUNDATIONAL_MISSIONS':
      default:
        return {
          featureKey,
          requiredTier: 'FREE',
          granted: true
        };
    }
  }

  public resetIdentity(): void {
    if (this.activeFirestoreUnsub) {
      this.activeFirestoreUnsub();
      this.activeFirestoreUnsub = null;
    }
    this.currentSubscription = null;
    this.subscribers.clear();
  }

  private notifySubscribers(sub: AuthoritativeSubscriptionState) {
    for (const subFn of this.subscribers) {
      try {
        subFn(sub);
      } catch (e) {
        console.error('Subscription listener error', e);
      }
    }
  }
}
