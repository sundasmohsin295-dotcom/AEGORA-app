import { initializeApp, getApps, getApp, FirebaseApp } from 'firebase/app';
import {
  getAuth,
  signInWithEmailAndPassword,
  signOut as firebaseSignOut,
  onAuthStateChanged,
  User,
  Auth
} from 'firebase/auth';

export interface FirebaseConfig {
  apiKey: string;
  authDomain: string;
  projectId: string;
  storageBucket?: string;
  messagingSenderId?: string;
  appId?: string;
  measurementId?: string;
}

/**
 * Retrieves Firebase configuration from environment variables.
 * Returns null if required configuration parameters (API Key or Project ID) are absent.
 */
export function getFirebaseConfig(): FirebaseConfig | null {
  const apiKey = (import.meta.env.VITE_FIREBASE_API_KEY as string | undefined)?.trim();
  const projectId = (import.meta.env.VITE_FIREBASE_PROJECT_ID as string | undefined)?.trim();

  if (!apiKey || !projectId) {
    return null;
  }

  return {
    apiKey,
    authDomain: (import.meta.env.VITE_FIREBASE_AUTH_DOMAIN as string | undefined)?.trim() || `${projectId}.firebaseapp.com`,
    projectId,
    storageBucket: (import.meta.env.VITE_FIREBASE_STORAGE_BUCKET as string | undefined)?.trim() || `${projectId}.appspot.com`,
    messagingSenderId: (import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID as string | undefined)?.trim() || '',
    appId: (import.meta.env.VITE_FIREBASE_APP_ID as string | undefined)?.trim() || '',
    measurementId: (import.meta.env.VITE_FIREBASE_MEASUREMENT_ID as string | undefined)?.trim() || ''
  };
}

let firebaseAppInstance: FirebaseApp | null = null;
let firebaseAuthInstance: Auth | null = null;

/**
 * Initializes Firebase App exactly once if valid configuration is present.
 */
export function initFirebase(): { app: FirebaseApp | null; auth: Auth | null; isConfigured: boolean } {
  const config = getFirebaseConfig();
  if (!config) {
    return { app: null, auth: null, isConfigured: false };
  }

  try {
    if (getApps().length === 0) {
      firebaseAppInstance = initializeApp(config);
    } else {
      firebaseAppInstance = getApp();
    }
    firebaseAuthInstance = getAuth(firebaseAppInstance);
    return {
      app: firebaseAppInstance,
      auth: firebaseAuthInstance,
      isConfigured: true
    };
  } catch (err) {
    console.error('Failed to initialize Firebase Web SDK:', err);
    return { app: null, auth: null, isConfigured: false };
  }
}

export function getFirebaseAuth(): Auth | null {
  if (!firebaseAuthInstance) {
    const init = initFirebase();
    return init.auth;
  }
  return firebaseAuthInstance;
}

export {
  signInWithEmailAndPassword,
  firebaseSignOut,
  onAuthStateChanged,
  type User,
  type Auth
};
