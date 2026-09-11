"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.SubscriptionAuthority = void 0;
const admin = __importStar(require("firebase-admin"));
class SubscriptionAuthority {
    db;
    constructor(db) {
        this.db = db || admin.firestore();
    }
    /**
     * Derives SubscriptionTier from active entitlement identifiers.
     * Entitlements:
     *  - 'career' or 'career_pass' -> CAREER
     *  - 'pro' or 'pro_access'     -> PRO
     *  - default                   -> FREE
     */
    deriveTierFromEntitlements(entitlements) {
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
    async getAuthoritativeSubscription(ownerAuthUid) {
        const docRef = this.db.doc(`learners/${ownerAuthUid}/subscription/current`);
        const snapshot = await docRef.get();
        if (snapshot.exists) {
            const data = snapshot.data();
            // Check expiration if present
            if (data.expiresAt) {
                const expirationTime = new Date(data.expiresAt).getTime();
                if (!isNaN(expirationTime) && Date.now() > expirationTime) {
                    // Subscription expired - downgrade to FREE
                    const expiredState = {
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
        const defaultState = {
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
    async recordAuthoritativeSubscription(ownerAuthUid, entitlementIdentifiers, productIdentifier, expiresAt, sourceEventId) {
        const tier = this.deriveTierFromEntitlements(entitlementIdentifiers);
        const active = tier !== 'FREE' ? (expiresAt ? new Date(expiresAt).getTime() > Date.now() : true) : true;
        const now = new Date().toISOString();
        const subscriptionState = {
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
    async processRevenueCatWebhook(event, authHeader) {
        // 1. Webhook Secret Validation
        const expectedSecret = process.env.REVENUECAT_WEBHOOK_AUTH_TOKEN;
        if (expectedSecret && authHeader !== expectedSecret && authHeader !== `Bearer ${expectedSecret}`) {
            return { success: false, processed: false, reason: 'UNAUTHORIZED_WEBHOOK' };
        }
        if (!event || !event.event) {
            return { success: false, processed: false, reason: 'INVALID_PAYLOAD' };
        }
        const webhookEvent = event.event;
        const eventId = webhookEvent.id || webhookEvent.event_timestamp_ms?.toString();
        const appUserId = webhookEvent.app_user_id;
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
        const entitlementIds = webhookEvent.entitlement_ids || [];
        const productId = webhookEvent.product_id || null;
        const expirationMs = webhookEvent.expiration_at_ms;
        const expiresAt = expirationMs ? new Date(expirationMs).toISOString() : null;
        // Handle cancellation or expiration event types
        const eventType = webhookEvent.type;
        let finalEntitlements = entitlementIds;
        if (eventType === 'EXPIRATION' || eventType === 'CANCELLATION_REVOCATION') {
            finalEntitlements = [];
        }
        // 5. Update Authoritative Subscription
        await this.recordAuthoritativeSubscription(ownerAuthUid, finalEntitlements, productId, expiresAt, eventId);
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
exports.SubscriptionAuthority = SubscriptionAuthority;
//# sourceMappingURL=subscriptionAuthority.js.map