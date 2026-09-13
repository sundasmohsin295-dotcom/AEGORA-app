import React, { useState } from 'react';
import {
  Shield,
  CheckCircle2,
  Copy,
  Check,
  X,
  Fingerprint,
  ExternalLink,
  Award,
  Lock,
  FileCode,
  Download
} from 'lucide-react';

interface ProofDossierModalProps {
  isOpen: boolean;
  onClose: () => void;
  operatorName?: string;
  callsign?: string;
  role?: string;
}

export const ProofDossierModal: React.FC<ProofDossierModalProps> = ({
  isOpen,
  onClose,
  operatorName = 'SUNDAS MOHSIN',
  callsign = 'AEG-2026-9942X',
  role = 'Lvl 4 • Senior SOC Analyst'
}) => {
  const [copiedLink, setCopiedLink] = useState(false);
  const [copiedHash, setCopiedHash] = useState(false);
  const [isExporting, setIsExporting] = useState(false);
  const [exportProgress, setExportProgress] = useState(0);
  const [exportComplete, setExportComplete] = useState(false);

  if (!isOpen) return null;

  const mockSha256Digest = 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855';
  const verificationUrl = `https://aegora.net/verify/passport/${callsign}?sha256=${mockSha256Digest.slice(0, 16)}`;

  const verifiedSkills = [
    { title: 'Sysmon EDR Log Analysis', level: 'ADVANCED', status: 'VERIFIED' },
    { title: 'Cobalt Strike Stager Identification', level: 'EXPERT', status: 'VERIFIED' },
    { title: 'PowerShell Base64 Deobfuscation', level: 'INTERMEDIATE', status: 'VERIFIED' },
    { title: 'Active Directory ACL Recon', level: 'ADVANCED', status: 'VERIFIED' },
    { title: 'Memory Forensics Injection (Volatility)', level: 'EXPERT', status: 'VERIFIED' },
    { title: 'Sigma Rule Detection Engineering', level: 'SENIOR', status: 'VERIFIED' }
  ];

  const handleCopyLink = () => {
    if (navigator?.clipboard) {
      navigator.clipboard.writeText(verificationUrl);
    }
    setCopiedLink(true);
    setTimeout(() => setCopiedLink(false), 2400);
  };

  const handleCopyHash = () => {
    if (navigator?.clipboard) {
      navigator.clipboard.writeText(mockSha256Digest);
    }
    setCopiedHash(true);
    setTimeout(() => setCopiedHash(false), 2400);
  };

  const handleBiometricExport = () => {
    if (isExporting) return;
    setIsExporting(true);
    setExportProgress(0);
    setExportComplete(false);

    const interval = setInterval(() => {
      setExportProgress(prev => {
        if (prev >= 100) {
          clearInterval(interval);
          setIsExporting(false);
          setExportComplete(true);
          return 100;
        }
        return prev + 25;
      });
    }, 280);
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(5, 11, 20, 0.85)',
        backdropFilter: 'blur(10px)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px',
        zIndex: 1000
      }}
      onClick={onClose}
    >
      {/* High-Fidelity Matte-Dark Card */}
      <div
        style={{
          width: '100%',
          maxWidth: '680px',
          maxHeight: '90vh',
          overflowY: 'auto',
          borderRadius: '16px',
          backgroundColor: '#090A0C',
          backgroundImage: 'linear-gradient(180deg, #15171C 0%, #090A0C 100%)',
          border: '1px solid #2D313A',
          boxShadow: '0 0 50px -10px rgba(41, 98, 255, 0.25), 0 25px 50px -12px rgba(0, 0, 0, 0.9)',
          padding: '28px',
          position: 'relative',
          color: '#FFFFFF'
        }}
        onClick={e => e.stopPropagation()}
      >
        {/* Close Button */}
        <button
          onClick={onClose}
          style={{
            position: 'absolute',
            top: '20px',
            right: '20px',
            background: 'transparent',
            border: 'none',
            color: '#8A919E',
            cursor: 'pointer',
            padding: '6px',
            borderRadius: '6px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}
          aria-label="Close Modal"
        >
          <X size={20} />
        </button>

        {/* Modal Header */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' }}>
          <div
            style={{
              width: '42px',
              height: '42px',
              borderRadius: '10px',
              backgroundColor: 'rgba(41, 98, 255, 0.15)',
              border: '1px solid #2962FF',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#2962FF'
            }}
          >
            <Shield size={22} />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <h2 style={{ fontSize: '18px', fontWeight: 800, letterSpacing: '0.04em', margin: 0 }}>
                VERIFIED CAPABILITY PASSPORT
              </h2>
              <span
                style={{
                  fontSize: '10px',
                  fontWeight: 700,
                  fontFamily: 'monospace',
                  padding: '2px 8px',
                  borderRadius: '4px',
                  backgroundColor: 'rgba(0, 230, 118, 0.15)',
                  color: '#00E676',
                  border: '1px solid rgba(0, 230, 118, 0.4)'
                }}
              >
                LIVE AUDIT
              </span>
            </div>
            <p style={{ margin: '4px 0 0', fontSize: '11px', color: '#8A919E', fontFamily: 'monospace' }}>
              CRYPTOGRAPHIC PROOF DOSSIER // RECRUITER-VERIFIABLE ENCLAVE
            </p>
          </div>
        </div>

        {/* Operator Profile Header */}
        <div
          style={{
            padding: '16px',
            borderRadius: '10px',
            backgroundColor: '#15171C',
            border: '1px solid #2D313A',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '20px',
            flexWrap: 'wrap',
            gap: '12px'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
            <div
              style={{
                width: '44px',
                height: '44px',
                borderRadius: '50%',
                backgroundColor: '#2962FF',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontWeight: 900,
                fontSize: '16px'
              }}
            >
              SM
            </div>
            <div>
              <div style={{ fontSize: '15px', fontWeight: 700, letterSpacing: '0.02em' }}>
                {operatorName}
              </div>
              <div style={{ fontSize: '12px', color: '#8A919E', fontFamily: 'monospace' }}>
                {role}
              </div>
            </div>
          </div>

          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', color: '#8A919E', fontFamily: 'monospace' }}>
              PASSPORT IDENTIFIER
            </div>
            <div style={{ fontSize: '13px', fontWeight: 700, color: '#00E676', fontFamily: 'monospace' }}>
              {callsign}
            </div>
          </div>
        </div>

        {/* SHA-256 Evidence Digest Box */}
        <div style={{ marginBottom: '22px' }}>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              marginBottom: '8px'
            }}
          >
            <span style={{ fontSize: '11px', fontWeight: 700, color: '#8A919E', fontFamily: 'monospace' }}>
              IMMUTABLE EVIDENCE DIGEST (SHA-256)
            </span>
            <button
              onClick={handleCopyHash}
              style={{
                background: 'transparent',
                border: 'none',
                color: copiedHash ? '#00E676' : '#2962FF',
                fontSize: '11px',
                fontFamily: 'monospace',
                cursor: 'pointer',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '4px'
              }}
            >
              {copiedHash ? <Check size={12} /> : <Copy size={12} />}
              <span>{copiedHash ? 'DIGEST COPIED' : 'COPY HASH'}</span>
            </button>
          </div>

          <div
            style={{
              padding: '14px',
              borderRadius: '8px',
              backgroundColor: '#000000',
              border: '1px solid #2D313A',
              fontFamily: 'monospace',
              fontSize: '11px',
              lineHeight: '1.6',
              color: '#00E676',
              wordBreak: 'break-all'
            }}
          >
            <div style={{ color: '#8A919E', fontSize: '10px', marginBottom: '4px' }}>
              // ROOT MERKLE LEAF HASH:
            </div>
            {mockSha256Digest}
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                marginTop: '8px',
                paddingTop: '8px',
                borderTop: '1px dashed #2D313A',
                fontSize: '10px',
                color: '#8A919E'
              }}
            >
              <span>SIGNER: AEGORA-PROV-ED25519</span>
              <span>ATTESTED: 2026-09-13T08:19:00Z</span>
            </div>
          </div>
        </div>

        {/* Status Chips for Completed Skills */}
        <div style={{ marginBottom: '24px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: '#8A919E', fontFamily: 'monospace', marginBottom: '10px' }}>
            PROVEN COGNITIVE & DEFENSIVE CAPABILITIES
          </div>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
            {verifiedSkills.map((skill, index) => (
              <div
                key={index}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '6px',
                  padding: '6px 12px',
                  borderRadius: '6px',
                  backgroundColor: '#15171C',
                  border: '1px solid #2D313A',
                  fontSize: '11px'
                }}
              >
                <CheckCircle2 size={13} color="#00E676" />
                <span style={{ fontWeight: 600, color: '#E2E8F0' }}>{skill.title}</span>
                <span
                  style={{
                    fontSize: '9px',
                    fontFamily: 'monospace',
                    fontWeight: 700,
                    padding: '1px 4px',
                    borderRadius: '3px',
                    backgroundColor: 'rgba(0, 230, 118, 0.1)',
                    color: '#00E676'
                  }}
                >
                  [{skill.status}]
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Biometric Export Progress Simulation */}
        {isExporting && (
          <div
            style={{
              padding: '14px',
              borderRadius: '8px',
              backgroundColor: 'rgba(41, 98, 255, 0.1)',
              border: '1px solid #2962FF',
              marginBottom: '20px'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', fontFamily: 'monospace', marginBottom: '6px' }}>
              <span style={{ color: '#2962FF', fontWeight: 700 }}>
                ATTESTING BIOMETRIC CREDENTIAL ENCLAVE...
              </span>
              <span style={{ color: '#FFFFFF' }}>{exportProgress}%</span>
            </div>
            <div style={{ width: '100%', height: '4px', backgroundColor: '#15171C', borderRadius: '2px', overflow: 'hidden' }}>
              <div
                style={{
                  width: `${exportProgress}%`,
                  height: '100%',
                  backgroundColor: '#2962FF',
                  transition: 'width 0.25s ease'
                }}
              />
            </div>
          </div>
        )}

        {exportComplete && (
          <div
            style={{
              padding: '12px 14px',
              borderRadius: '8px',
              backgroundColor: 'rgba(0, 230, 118, 0.1)',
              border: '1px solid #00E676',
              marginBottom: '20px',
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              fontSize: '11px',
              fontFamily: 'monospace',
              color: '#00E676'
            }}
          >
            <Download size={16} />
            <span>EXPORT SUCCESSFUL: aegora-passport-verified.json signed with hardware token.</span>
          </div>
        )}

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap' }}>
          <button
            onClick={handleCopyLink}
            style={{
              flex: 1,
              minWidth: '200px',
              height: '46px',
              borderRadius: '8px',
              backgroundColor: '#15171C',
              border: '1px solid #2D313A',
              color: '#FFFFFF',
              fontWeight: 700,
              fontSize: '13px',
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              transition: 'background 0.2s ease'
            }}
          >
            {copiedLink ? <Check size={16} color="#00E676" /> : <Copy size={16} />}
            <span>{copiedLink ? 'VERIFICATION LINK COPIED' : 'COPY VERIFICATION LINK'}</span>
          </button>

          <button
            onClick={handleBiometricExport}
            disabled={isExporting}
            style={{
              flex: 1,
              minWidth: '200px',
              height: '46px',
              borderRadius: '8px',
              backgroundColor: isExporting ? '#15171C' : '#2962FF',
              border: isExporting ? '1px solid #2D313A' : 'none',
              color: '#FFFFFF',
              fontWeight: 700,
              fontSize: '13px',
              cursor: isExporting ? 'not-allowed' : 'pointer',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '8px',
              transition: 'background 0.2s ease'
            }}
          >
            <Fingerprint size={16} />
            <span>{isExporting ? 'SIMULATING EXPORT...' : 'BIOMETRIC EXPORT SIMULATION'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
