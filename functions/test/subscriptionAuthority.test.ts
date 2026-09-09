import { describe, it, expect, beforeEach, vi } from 'vitest';
import { SubscriptionAuthority } from '../src/authority/subscriptionAuthority';

describe('SubscriptionAuthority (Server Authority & Webhooks)', () => {
  let authority: SubscriptionAuthority;
  let mockStore: Map<string, any>;
  let mockDb: any;

  beforeEach(() => {
    mockStore = new Map<string, any>();

    mockDb = {
      doc: (path: string) => ({
        get: async () => ({
          exists: mockStore.has(path),
          data: () => mockStore.get(path)
        }),
        set: async (data: any, options?: any) => {
          if (options?.merge && mockStore.has(path)) {
            mockStore.set(path, { ...mockStore.get(path), ...data });
          } else {
            mockStore.set(path, data);
          }
        }
      })
    };

    authority = new SubscriptionAuthority(mockDb);
  });

  it('correctly derives tiers from entitlements', () => {
    expect(authority.deriveTierFromEntitlements([])).toBe('FREE');
    expect(authority.deriveTierFromEntitlements(['pro'])).toBe('PRO');
    expect(authority.deriveTierFromEntitlements(['pro_access'])).toBe('PRO');
    expect(authority.deriveTierFromEntitlements(['career'])).toBe('CAREER');
    expect(authority.deriveTierFromEntitlements(['career_pass'])).toBe('CAREER');
    expect(authority.deriveTierFromEntitlements(['pro', 'career'])).toBe('CAREER');
  });

  it('defaults to FREE tier with server authority metadata when no subscription exists', async () => {
    const sub = await authority.getAuthoritativeSubscription('user_alpha');
    expect(sub.ownerAuthUid).toBe('user_alpha');
    expect(sub.tier).toBe('FREE');
    expect(sub.active).toBe(true);
    expect(sub.provider).toBe('SYSTEM_DEFAULT');
    expect(sub.authorityMetadata.authoritySource).toBe('SERVER');
    expect(sub.authorityMetadata.verifiedBy).toBe('AegoraSubscriptionAuthority');
  });

  it('records verified PRO entitlement from RevenueCat server authority', async () => {
    const futureDate = new Date(Date.now() + 86400000 * 30).toISOString();
    const recorded = await authority.recordAuthoritativeSubscription(
      'user_bravo',
      ['pro'],
      'aegora_pro_monthly',
      futureDate,
      'evt_12345'
    );

    expect(recorded.ownerAuthUid).toBe('user_bravo');
    expect(recorded.tier).toBe('PRO');
    expect(recorded.active).toBe(true);
    expect(recorded.productIdentifier).toBe('aegora_pro_monthly');
    expect(recorded.provider).toBe('REVENUECAT');
    expect(recorded.authorityMetadata.authoritySource).toBe('SERVER');

    // Retrieve again to ensure persistence
    const retrieved = await authority.getAuthoritativeSubscription('user_bravo');
    expect(retrieved.tier).toBe('PRO');
    expect(retrieved.active).toBe(true);
  });

  it('downgrades expired subscription to FREE on lookup', async () => {
    const pastDate = new Date(Date.now() - 86400000).toISOString();
    await authority.recordAuthoritativeSubscription(
      'user_charlie',
      ['career'],
      'aegora_career_annual',
      pastDate,
      'evt_past'
    );

    const retrieved = await authority.getAuthoritativeSubscription('user_charlie');
    expect(retrieved.tier).toBe('FREE');
    expect(retrieved.active).toBe(false);
  });

  it('processes webhook and records authoritative state idempotently', async () => {
    const event = {
      event: {
        id: 'webhook_evt_999',
        type: 'INITIAL_PURCHASE',
        app_user_id: 'user_delta',
        entitlement_ids: ['career_pass'],
        product_id: 'career_sub_yearly',
        expiration_at_ms: Date.now() + 86400000 * 365,
        event_timestamp_ms: Date.now()
      }
    };

    const firstRun = await authority.processRevenueCatWebhook(event);
    expect(firstRun.success).toBe(true);
    expect(firstRun.processed).toBe(true);

    const sub = await authority.getAuthoritativeSubscription('user_delta');
    expect(sub.tier).toBe('CAREER');
    expect(sub.active).toBe(true);

    // Replay attack / duplicate webhook
    const duplicateRun = await authority.processRevenueCatWebhook(event);
    expect(duplicateRun.success).toBe(true);
    expect(duplicateRun.processed).toBe(false);
    expect(duplicateRun.reason).toBe('IDEMPOTENT_DUPLICATE_IGNORED');
  });

  it('handles cancellation / expiration webhooks cleanly', async () => {
    // First establish subscription
    await authority.recordAuthoritativeSubscription(
      'user_echo',
      ['pro'],
      'pro_monthly',
      new Date(Date.now() + 86400000).toISOString()
    );

    // Expiration webhook arrives
    const expEvent = {
      event: {
        id: 'webhook_evt_exp_1',
        type: 'EXPIRATION',
        app_user_id: 'user_echo',
        entitlement_ids: ['pro'],
        event_timestamp_ms: Date.now()
      }
    };

    const expRun = await authority.processRevenueCatWebhook(expEvent);
    expect(expRun.success).toBe(true);
    expect(expRun.processed).toBe(true);

    const sub = await authority.getAuthoritativeSubscription('user_echo');
    expect(sub.tier).toBe('FREE');
  });
});
