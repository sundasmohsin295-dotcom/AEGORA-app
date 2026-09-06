import { CanonicalAegoraIdentity } from '../types/platform';
import {
  initFirebase,
  getFirebaseAuth,
  signInWithEmailAndPassword,
  firebaseSignOut,
  onAuthStateChanged,
  User,
  Auth
} from '../firebase/firebaseClient';

export type AuthStatus =
  | 'UNAUTHENTICATED'
  | 'AUTHENTICATING'
  | 'AUTHENTICATED'
  | 'BLOCKED';

export interface AuthState {
  status: AuthStatus;
  identity: CanonicalAegoraIdentity | null;
  blockedReason: string | null;
  isBackendConnected: boolean;
}

/**
 * Platform Authentication Client for AEGORA Web.
 * Mirrors the Android AuthProvider and AegoraAuthRepository abstraction.
 *
 * Defaults strictly to UNAUTHENTICATED or BLOCKED depending on configuration presence.
 * Honestly marks status as BLOCKED when external Firebase/IdP credentials are absent.
 */
export class CrossPlatformAuthClient {
  private state: AuthState;
  private listeners: Array<(state: AuthState) => void> = [];
  private authInstance: Auth | null = null;
  private isConfigured = false;

  constructor() {
    const init = initFirebase();
    this.authInstance = init.auth;
    this.isConfigured = init.isConfigured;

    if (this.isConfigured && this.authInstance) {
      // Configured Firebase starts strictly UNAUTHENTICATED until onAuthStateChanged fires
      this.state = {
        status: 'UNAUTHENTICATED',
        identity: null,
        blockedReason: null,
        isBackendConnected: true
      };

      // Listen to real Firebase Auth state changes
      onAuthStateChanged(this.authInstance, (user: User | null) => {
        if (user) {
          const canonical = CrossPlatformAuthClient.resolveCanonicalIdentity(
            user.uid,
            'FIREBASE',
            user.email,
            user.displayName
          );
          this.state = {
            status: 'AUTHENTICATED',
            identity: canonical,
            blockedReason: null,
            isBackendConnected: true
          };
        } else {
          this.state = {
            status: 'UNAUTHENTICATED',
            identity: null,
            blockedReason: null,
            isBackendConnected: true
          };
        }
        this.notify();
      });
    } else {
      // Configuration absent: honestly mark status as BLOCKED
      this.state = {
        status: 'BLOCKED',
        identity: null,
        blockedReason: 'AUTH BACKEND BLOCKED: External Identity Provider configuration missing (google-services.json / Web Firebase config not present). Operating in architectural transparency mode.',
        isBackendConnected: false
      };
    }
  }

  public getState(): AuthState {
    return { ...this.state };
  }

  public subscribe(listener: (state: AuthState) => void): () => void {
    this.listeners.push(listener);
    listener(this.getState());
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }

  private notify() {
    const currentState = this.getState();
    this.listeners.forEach(l => l(currentState));
  }

  /**
   * Deterministic canonical identity resolution.
   * Matches Android CanonicalAegoraIdentity.resolve() and AuthenticatedIdentity.fromProvider() exactly.
   */
  public static resolveCanonicalIdentity(
    providerUid: string,
    provider: string,
    email?: string | null,
    displayName?: string | null
  ): CanonicalAegoraIdentity {
    if (!providerUid || !providerUid.trim()) {
      throw new Error('Provider UID cannot be blank');
    }
    if (!provider || !provider.trim()) {
      throw new Error('Provider cannot be blank');
    }

    const sanitizedUid = providerUid
      .replace(/[^a-zA-Z0-9_]/g, '')
      .slice(0, 16)
      .toLowerCase();

    return {
      canonicalUserId: `usr_${sanitizedUid}`,
      canonicalLearnerId: `operator_${sanitizedUid}`,
      providerUid,
      provider,
      email: email || null,
      displayName: displayName || `Operator ${sanitizedUid}`,
      clientType: 'WEB',
      authenticatedAt: Date.now()
    };
  }

  /**
   * Protected Domain Guard:
   * Rejects client UI requests attempting to query or submit as another learner.
   */
  public validateLearnerOwnership(targetLearnerId: string): { allowed: boolean; reason?: string } {
    if (this.state.status !== 'AUTHENTICATED' || !this.state.identity) {
      return {
        allowed: false,
        reason: 'Unauthenticated caller cannot execute protected operations'
      };
    }

    if (this.state.identity.canonicalLearnerId !== targetLearnerId) {
      return {
        allowed: false,
        reason: `Authorization Violation: Caller ${this.state.identity.canonicalLearnerId} cannot access learner ${targetLearnerId}`
      };
    }

    return { allowed: true };
  }

  /**
   * Real Firebase Email/Password Sign-In
   */
  public async signInWithEmailPassword(email: string, pass: string): Promise<{ success: boolean; error?: string }> {
    if (!this.isConfigured || !this.authInstance) {
      const errorMsg = 'Firebase configuration required: VITE_FIREBASE_API_KEY and VITE_FIREBASE_PROJECT_ID are missing from environment.';
      this.state = {
        status: 'BLOCKED',
        identity: null,
        blockedReason: errorMsg,
        isBackendConnected: false
      };
      this.notify();
      return { success: false, error: errorMsg };
    }

    this.state = {
      ...this.state,
      status: 'AUTHENTICATING'
    };
    this.notify();

    try {
      const userCredential = await signInWithEmailAndPassword(this.authInstance, email, pass);
      const canonical = CrossPlatformAuthClient.resolveCanonicalIdentity(
        userCredential.user.uid,
        'FIREBASE',
        userCredential.user.email,
        userCredential.user.displayName
      );
      this.state = {
        status: 'AUTHENTICATED',
        identity: canonical,
        blockedReason: null,
        isBackendConnected: true
      };
      this.notify();
      return { success: true };
    } catch (err: unknown) {
      const errorMsg = err instanceof Error ? err.message : String(err);
      this.state = {
        status: 'UNAUTHENTICATED',
        identity: null,
        blockedReason: errorMsg,
        isBackendConnected: true
      };
      this.notify();
      return { success: false, error: errorMsg };
    }
  }

  /**
   * Architectural Session Ingress:
   * Used for authenticated session handover (e.g. verified cross-platform identity transfer).
   */
  public attachAuthenticatedSession(identity: CanonicalAegoraIdentity) {
    this.state = {
      status: 'AUTHENTICATED',
      identity,
      blockedReason: null,
      isBackendConnected: this.isConfigured
    };
    this.notify();
  }

  public async signOut(): Promise<void> {
    if (this.isConfigured && this.authInstance) {
      try {
        await firebaseSignOut(this.authInstance);
      } catch (err) {
        console.error('Error during Firebase sign out:', err);
      }
      this.state = {
        status: 'UNAUTHENTICATED',
        identity: null,
        blockedReason: null,
        isBackendConnected: true
      };
    } else {
      this.state = {
        status: 'BLOCKED',
        identity: null,
        blockedReason: 'AUTH BACKEND BLOCKED: External Identity Provider configuration missing.',
        isBackendConnected: false
      };
    }
    this.notify();
  }
}

export const authClient = new CrossPlatformAuthClient();
