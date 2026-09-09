import * as admin from 'firebase-admin';
import { HttpsError, CallableRequest } from 'firebase-functions/v2/https';
import * as crypto from 'crypto';

/**
 * Trusted Identity & Security Helpers for AEGORA Server Authority
 */
export class AuthVerificationService {
  /**
   * Verifies that the caller has a valid, authenticated Firebase UID.
   * If client supplies a targetAuthUid or learnerId, strictly verifies it matches the authenticated UID.
   * Cross-user writes or identity spoofing are unconditionally rejected.
   */
  public static verifyCaller(request: CallableRequest<any>, requestedUid?: string): string {
    if (!request.auth || !request.auth.uid) {
      throw new HttpsError('unauthenticated', 'Unauthenticated request: Valid Firebase authentication credentials required.');
    }

    const authenticatedUid = request.auth.uid;

    if (requestedUid && requestedUid !== authenticatedUid) {
      throw new HttpsError('permission-denied', `Cross-user violation: Authenticated UID '${authenticatedUid}' cannot act on behalf of '${requestedUid}'.`);
    }

    return authenticatedUid;
  }

  /**
   * Verifies an ID token passed via standard HTTP headers (e.g. Bearer token)
   */
  public static async verifyHttpBearerToken(authHeader?: string): Promise<string> {
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new HttpsError('unauthenticated', 'Missing or malformed Authorization header with Bearer token.');
    }

    const idToken = authHeader.split('Bearer ')[1];
    try {
      const decodedToken = await admin.auth().verifyIdToken(idToken);
      if (!decodedToken.uid) {
        throw new HttpsError('unauthenticated', 'Invalid token: UID missing from payload.');
      }
      return decodedToken.uid;
    } catch (err: any) {
      throw new HttpsError('unauthenticated', `ID Token verification failed: ${err.message}`);
    }
  }

  public static generateDigest(rawString: string): string {
    return 'sha256:' + crypto.createHash('sha256').update(rawString, 'utf8').digest('hex');
  }

  /**
   * Evaluates SHA-256 integrity digest for evidence payload.
   * NOTE: SHA-256 IS STRICTLY INTEGRITY/TAMPER-DETECTION.
   * SHA-256 IS NOT AUTHENTICATION, NOT AUTHORIZATION, NOT DIGITAL SIGNATURE, AND NOT NON-REPUDIATION.
   */
  public static verifyIntegrityDigest(
    learnerId: string,
    missionId: string,
    attemptId: string,
    evidenceType: string,
    payloadRaw: string,
    providedDigest: string
  ): boolean {
    const rawString = `${learnerId}:${missionId}:${attemptId}:${evidenceType}:${payloadRaw}`;
    const calculatedDigest = 'sha256:' + crypto.createHash('sha256').update(rawString, 'utf8').digest('hex');
    return calculatedDigest === providedDigest;
  }
}
