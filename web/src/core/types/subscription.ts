export type SubscriptionTier = 'FREE' | 'PRO' | 'CAREER' | 'UNKNOWN';

export interface AuthoritativeSubscriptionState {
  ownerAuthUid: string;
  tier: SubscriptionTier;
  active: boolean;
  entitlementIdentifiers: string[];
  productIdentifier: string | null;
  expiresAt: string | null;
  provider: 'REVENUECAT' | 'SYSTEM_DEFAULT';
  customerId: string;
  checkedAt: string;
  sourceEventId?: string;
  authorityMetadata: {
    authoritySource: 'SERVER';
    verifiedAt: string;
    verifiedBy: string;
    algorithmVersion: string;
    sourceEvidenceIds: string[];
  };
}

export interface SubscriptionFeatureCheck {
  featureKey: string;
  requiredTier: SubscriptionTier;
  granted: boolean;
  reason?: string;
}

export interface SubscriptionPackage {
  identifier: string;
  packageType: 'MONTHLY' | 'ANNUAL' | 'LIFETIME' | 'CUSTOM';
  tier: SubscriptionTier;
  title: string;
  description: string;
  priceString: string;
}
