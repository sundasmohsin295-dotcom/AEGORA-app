import { describe, it, expect } from 'vitest';
import { CrossPlatformAuthClient, authClient } from '../src/core/auth/CrossPlatformAuthClient';
import { WebCapabilityEngine } from '../src/core/intelligence/DemonstratedCapabilityEngine';
import { validateWebMission } from '../src/core/missions/SuspiciousLoginMission';

describe('AEGORA Web Client Platform Contracts', () => {
  it('01: Default web auth state is strictly unauthenticated/blocked with honest reason', () => {
    const state = authClient.getState();
    expect(state.status).toBe('BLOCKED');
    expect(state.identity).toBeNull();
    expect(state.isBackendConnected).toBe(false);
    expect(state.blockedReason).toContain('AUTH BACKEND BLOCKED');
  });

  it('02: Deterministic canonical identity resolution matches Android exactly', () => {
    const providerUid = 'google_sec_99182';
    const provider = 'google.com';
    const email = 'analyst@cyberops.internal';

    const identity = CrossPlatformAuthClient.resolveCanonicalIdentity(
      providerUid,
      provider,
      email
    );

    expect(identity.canonicalUserId).toBe('usr_google_sec_99182');
    expect(identity.canonicalLearnerId).toBe('operator_google_sec_99182');
    expect(identity.clientType).toBe('WEB');
    expect(identity.email).toBe(email);
  });

  it('03: Zero-trust boundary rejects spoofed learner IDs', () => {
    const identity = CrossPlatformAuthClient.resolveCanonicalIdentity(
      'legit_operator_01',
      'google.com'
    );

    const client = new CrossPlatformAuthClient();
    client.attachAuthenticatedSession(identity);

    // Attempting to access legitimate learner ID passes
    const validCheck = client.validateLearnerOwnership('operator_legit_operator_0');
    expect(validCheck.allowed).toBe(true);

    // Attempting to access spoofed target fails
    const invalidCheck = client.validateLearnerOwnership('operator_victim_target');
    expect(invalidCheck.allowed).toBe(false);
    expect(invalidCheck.reason).toContain('Authorization Violation');
  });

  it('04: Cyber Twin strictly preserves all 4 authoritative cognitive clusters', () => {
    const clusters = WebCapabilityEngine.getAuthoritativeClusters();
    expect(clusters.length).toBe(4);

    const names = clusters.map(c => c.name);
    expect(names).toContain('Foundation');
    expect(names).toContain('Active Defense');
    expect(names).toContain('Generalization & Stress');
    expect(names).toContain('Metacognitive & Strategic');

    // Total weight must equal 1.0
    const totalWeight = clusters.reduce((acc, c) => acc + c.weight, 0);
    expect(Math.abs(totalWeight - 1.0)).toBeLessThan(0.001);
  });

  it('05: Mission validation requires minimum 15 characters reasoning', () => {
    const answers = {
      q_login_1: 0,
      q_login_2: 0,
      q_login_3: 0
    };

    // Insufficient reasoning
    const shortOutcome = validateWebMission(answers, 'too short');
    expect(shortOutcome.isPassed).toBe(false);
    expect(shortOutcome.reasoningFeedback).toContain('insufficient');
    expect(shortOutcome.evidenceHash).toBeUndefined();

    // Sufficient reasoning
    const validOutcome = validateWebMission(
      answers,
      'Detected impossible travel between Moscow and Austin within 7 minutes and contained user session.'
    );
    expect(validOutcome.isPassed).toBe(true);
    expect(validOutcome.scorePercent).toBe(100);
    expect(validOutcome.evidenceHash).toBeDefined();
    expect(validOutcome.evidenceHash).toContain('sha256:aegora_web_lab_suspicious_login');
  });

  it('06: Firebase UID to canonical learner mapping is collision-resistant and deterministic', () => {
    const firebaseUid1 = 'd7K3mP9xYz42AbCd';
    const firebaseUid2 = 'd7K3mP9xYz42AbCe';

    const id1 = CrossPlatformAuthClient.resolveCanonicalIdentity(firebaseUid1, 'FIREBASE');
    const id2 = CrossPlatformAuthClient.resolveCanonicalIdentity(firebaseUid2, 'FIREBASE');

    expect(id1.canonicalLearnerId).toBe('operator_d7k3mp9xyz42abcd');
    expect(id2.canonicalLearnerId).toBe('operator_d7k3mp9xyz42abce');
    expect(id1.canonicalLearnerId).not.toBe(id2.canonicalLearnerId);
  });

  it('07: Cross-platform identity contract: Firebase UID on Android matches Web canonical identity', () => {
    // Known test vector matching Android AuthenticatedIdentity.fromProvider("firebase_usr_7721", "FIREBASE")
    const testUid = 'firebase_usr_7721';
    const webIdentity = CrossPlatformAuthClient.resolveCanonicalIdentity(testUid, 'FIREBASE');

    expect(webIdentity.providerUid).toBe(testUid);
    expect(webIdentity.canonicalLearnerId).toBe('operator_firebase_usr_772');
    expect(webIdentity.canonicalUserId).toBe('usr_firebase_usr_772');
  });

  it('08: Sign-out invalidates active identity and resets to unauthenticated', async () => {
    const client = new CrossPlatformAuthClient();
    const identity = CrossPlatformAuthClient.resolveCanonicalIdentity('test_user_signout', 'FIREBASE');
    client.attachAuthenticatedSession(identity);

    expect(client.getState().status).toBe('AUTHENTICATED');
    expect(client.getState().identity).not.toBeNull();

    await client.signOut();

    expect(client.getState().status).not.toBe('AUTHENTICATED');
    expect(client.getState().identity).toBeNull();
  });
});
