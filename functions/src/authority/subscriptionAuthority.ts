import * as admin from 'firebase-admin';
import { AuthoritativeSubscriptionState, SubscriptionTier } from '../models/types';

export class SubscriptionAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Derives SubscriptionTier from active entitlement identifiers.
   * Entitlements:
   *  - 'career' or 'career_pass' -> CAREER
   *  - 'pro' or 'pro_access'     -> PRO
   *  - default                   -> FREE
   */
  public deriveTierFromEntitlements(entitlements: string[]): SubscriptionTier {
    const normalized = entitlements.map(e => e.toLowerCase().trim());
    if (normalized.some(e => e.includes('career'))) {
      return 'CAREER';
    }
    if (normalized.some(e => e.includes('pro'))) {
      return 'PRO';
    }
    return 'FREE';
  }

  /**
   * Retrieves server-authoritative subscription state from Firestore.
   * If not yet set, returns a default FREE tier state without granting unearned access.
   */
  public async getAuthoritativeSubscription(ownerAuthUid: string): Promise<AuthoritativeSubscriptionState> {
    const docRef = this.db.doc(`learners/${ownerAuthUid}/subscription/current`);
    const snapshot = await docRef.get();

    if (snapshot.exists) {
      const data = snapshot.data() as AuthoritativeSubscriptionState;
      // Check expiration if present
      if (data.expiresAt) {
        const expirationTime = new Date(data.expiresAt).getTime();
        if (!isNaN(expirationTime) && Date.now() > expirationTime) {
          // Subscription expired - downgrade to FREE
          const expiredState: AuthoritativeSubscriptionState = {
            ...data,
            tier: 'FREE',
            active: false,
            checkedAt: new Date().toISOString(),
            authorityMetadata: {
              authoritySource: 'SERVER',
              verifiedAt: new Date().toISOString(),
              verifiedBy: 'AegoraSubscriptionAuthority',
              algorithmVersion: '2.0.0-revenuecat',
              sourceEvidenceIds: ['EXPIRED_CHECK']
            }
          };
          await docRef.set(expiredState, { merge: true });
          return expiredState;
        }
      }
      return data;
    }

    const defaultState: AuthoritativeSubscriptionState = {
      ownerAuthUid,
      tier: 'FREE',
      active: true,
      entitlementIdentifiers: [],
      productIdentifier: null,
      expiresAt: null,
      provider: 'SYSTEM_DEFAULT',
      customerId: ownerAuthUid,
      checkedAt: new Date().toISOString(),
      authorityMetadata: {
        authoritySource: 'SERVER',
        verifiedAt: new Date().toISOString(),
        verifiedBy: 'AegoraSubscriptionAuthority',
        algorithmVersion: '2.0.0-revenuecat',
        sourceEvidenceIds: ['INITIAL_DEFAULT']
      }
    };

    await docRef.set(defaultState);
    return defaultState;
  }

  /**
   * Updates server-authoritative subscription state from verified RevenueCat customer data.
   * Guaranteed to be written only by trusted server execution (Admin SDK).
   */
  public async recordAuthoritativeSubscription(
    ownerAuthUid: string,
    entitlementIdentifiers: string[],
    productIdentifier: string | null,
    expiresAt: string | null,
    sourceEventId?: string
  ): Promise<AuthoritativeSubscriptionState> {
    const tier = this.deriveTierFromEntitlements(entitlementIdentifiers);
    const active = tier !== 'FREE' ? (expiresAt ? new Date(expiresAt).getTime() > Date.now() : true) : true;
    const now = new Date().toISOString();

    const subscriptionState: AuthoritativeSubscriptionState = {
      ownerAuthUid,
      tier,
      active,
      entitlementIdentifiers,
      productIdentifier,
      expiresAt,
      provider: 'REVENUECAT',
      customerId: ownerAuthUid,
      checkedAt: now,
      sourceEventId,
      authorityMetadata: {
        authoritySource: 'SERVER',
        verifiedAt: now,
        verifiedBy: 'AegoraSubscriptionAuthority',
        algorithmVersion: '2.0.0-revenuecat',
        sourceEvidenceIds: sourceEventId ? [sourceEventId] : ['DIRECT_SYNC']
      }
    };

    const docRef = this.db.doc(`learners/${ownerAuthUid}/subscription/current`);
    await docRef.set(subscriptionState);
    return subscriptionState;
  }

  /**
   * Processes a verified RevenueCat Webhook event idempotently.
   * Checks event_id against processed_webhook_events collection to prevent replay attacks.
   */
  public async processRevenueCatWebhook(
    event: any,
    authHeader?: string
  ): Promise<{ success: boolean; processed: boolean; reason?: string }> {
    // 1. Webhook Secret Validation
    const expectedSecret = process.env.REVENUECAT_WEBHOOK_AUTH_TOKEN;
    if (expectedSecret && authHeader !== expectedSecret && authHeader !== `Bearer ${expectedSecret}`) {
      return { success: false, processed: false, reason: 'UNAUTHORIZED_WEBHOOK' };
    }

    if (!event || !event.event) {
      return { success: false, processed: false, reason: 'INVALID_PAYLOAD' };
    }

    const webhookEvent = event.event;
    const eventId: string = webhookEvent.id || webhookEvent.event_timestamp_ms?.toString();
    const appUserId: string = webhookEvent.app_user_id;

    if (!eventId || !appUserId) {
      return { success: false, processed: false, reason: 'MISSING_REQUIRED_FIELDS' };
    }

    // 2. Replay / Idempotency check
    const eventRef = this.db.doc(`system_events/revenuecat_webhooks/${eventId}`);
    const existing = await eventRef.get();
    if (existing.exists) {
      return { success: true, processed: false, reason: 'IDEMPOTENT_DUPLICATE_IGNORED' };
    }

    // 3. Resolve customer to Firebase UID
    // In AEGORA, app_user_id is the canonical Firebase UID.
    const ownerAuthUid = appUserId.trim();

    // 4. Extract active entitlements
    const entitlementIds: string[] = webhookEvent.entitlement_ids || [];
    const productId: string = webhookEvent.product_id || null;
    const expirationMs: number | undefined = webhookEvent.expiration_at_ms;
    const expiresAt = expirationMs ? new Date(expirationMs).toISOString() : null;

    // Handle cancellation or expiration event types
    const eventType = webhookEvent.type;
    let finalEntitlements = entitlementIds;
    if (eventType === 'EXPIRATION' || eventType === 'CANCELLATION_REVOCATION') {
      finalEntitlements = [];
    }

    // 5. Update Authoritative Subscription
    await this.recordAuthoritativeSubscription(
      ownerAuthUid,
      finalEntitlements,
      productId,
      expiresAt,
      eventId
    );

    // 6. Record processed event for replay protection
    await eventRef.set({
      eventId,
      eventType,
      appUserId,
      processedAt: new Date().toISOString(),
      timestampMs: webhookEvent.event_timestamp_ms || Date.now()
    });

    return { success: true, processed: true };
  }
}
