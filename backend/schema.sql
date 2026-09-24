-- ============================================================================
-- AEGORA: ENTERPRISE SUPABASE / POSTGRESQL PRODUCTION SCHEMA
-- ============================================================================
-- Standard: PostgreSQL 15+ / Supabase Serverless with Row Level Security (RLS)
-- Includes: Role-Based Access Control, Merkle Audit Blocks, SOAR Firewall Rules,
--           and Incident Automation Queues.
-- ============================================================================

-- Enable required cryptographic extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- 1. OPERATORS & ROLE-BASED ACCESS CONTROL
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.aegora_operators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    username VARCHAR(64) UNIQUE NOT NULL,
    callsign VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'OPERATOR' CHECK (role IN ('OPERATOR', 'ANALYST', 'ADMIN', 'SUPER_ADMIN')),
    clearance_level VARCHAR(32) NOT NULL DEFAULT 'STANDARD_LEVEL_1',
    device_fingerprint VARCHAR(128) NOT NULL,
    public_key_pem TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

-- Index for rapid lookups on auth and device verification
CREATE INDEX IF NOT EXISTS idx_operators_auth_user ON public.aegora_operators(auth_user_id);
CREATE INDEX IF NOT EXISTS idx_operators_device_fp ON public.aegora_operators(device_fingerprint);

-- ============================================================================
-- 2. IMMUTABLE MERKLE AUDIT CHAIN BLOCKS
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.aegora_merkle_blocks (
    block_index BIGSERIAL PRIMARY KEY,
    block_uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    timestamp TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    event_id VARCHAR(64) NOT NULL,
    component_tag VARCHAR(64) NOT NULL,
    severity VARCHAR(16) NOT NULL CHECK (severity IN ('INFO', 'WARN', 'CRITICAL')),
    raw_payload TEXT NOT NULL,
    event_payload_hash CHAR(64) NOT NULL,
    previous_block_hash CHAR(64) NOT NULL,
    block_hash CHAR(64) NOT NULL,
    is_tampered BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

CREATE INDEX IF NOT EXISTS idx_merkle_event_id ON public.aegora_merkle_blocks(event_id);
CREATE INDEX IF NOT EXISTS idx_merkle_block_hash ON public.aegora_merkle_blocks(block_hash);

-- Function to guarantee append-only immutability
CREATE OR REPLACE FUNCTION prevent_merkle_block_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'AEGORA SECURITY VIOLATION: Merkle Audit blocks are append-only and cryptographically immutable.';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_prevent_merkle_block_mutation ON public.aegora_merkle_blocks;
CREATE TRIGGER trg_prevent_merkle_block_mutation
BEFORE UPDATE OR DELETE ON public.aegora_merkle_blocks
FOR EACH ROW EXECUTE FUNCTION prevent_merkle_block_mutation();

-- ============================================================================
-- 3. TELEMETRY & SECURITY AUDIT EVENTS
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.aegora_telemetry_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id VARCHAR(64) UNIQUE NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    severity VARCHAR(16) NOT NULL CHECK (severity IN ('INFO', 'WARN', 'CRITICAL')),
    component_tag VARCHAR(64) NOT NULL,
    message TEXT NOT NULL,
    metadata JSONB DEFAULT '{}'::jsonb,
    client_ip VARCHAR(45) NOT NULL,
    device_fingerprint VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

CREATE INDEX IF NOT EXISTS idx_telemetry_severity ON public.aegora_telemetry_events(severity);
CREATE INDEX IF NOT EXISTS idx_telemetry_component ON public.aegora_telemetry_events(component_tag);

-- ============================================================================
-- 4. AUTONOMOUS SOAR FIREWALL RULES
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.aegora_soar_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    threat_id VARCHAR(64) NOT NULL,
    target_ip VARCHAR(45) NOT NULL,
    firewall_rule TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'AUTONOMOUSLY_ENFORCED' CHECK (status IN ('AUTONOMOUSLY_ENFORCED', 'ADMIN_FORCED_ENFORCEMENT', 'REVOKED')),
    enforced_by VARCHAR(64) NOT NULL DEFAULT 'SYSTEM_SOAR_ENGINE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    revoked_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_soar_target_ip ON public.aegora_soar_rules(target_ip);
CREATE INDEX IF NOT EXISTS idx_soar_status ON public.aegora_soar_rules(status);

-- ============================================================================
-- 5. N8N INCIDENT RESPONSE AUTOMATION QUEUE
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.aegora_n8n_incidents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incident_id VARCHAR(64) UNIQUE NOT NULL,
    threat_title VARCHAR(256) NOT NULL,
    cvss_score NUMERIC(3, 1) NOT NULL,
    severity VARCHAR(16) NOT NULL CHECK (severity IN ('INFO', 'WARN', 'CRITICAL')),
    source_ip VARCHAR(45) NOT NULL,
    merkle_block_hash CHAR(64),
    payload JSONB NOT NULL,
    webhook_url TEXT NOT NULL,
    dispatch_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' CHECK (dispatch_status IN ('PENDING', 'DELIVERED', 'FAILED', 'RETRYING')),
    http_status_code INT,
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    delivered_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_n8n_dispatch_status ON public.aegora_n8n_incidents(dispatch_status);

-- ============================================================================
-- ROW-LEVEL SECURITY (RLS) POLICIES
-- ============================================================================
ALTER TABLE public.aegora_operators ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.aegora_merkle_blocks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.aegora_telemetry_events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.aegora_soar_rules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.aegora_n8n_incidents ENABLE ROW LEVEL SECURITY;

-- Operator Profiles: Users can view their own profile; Admins view all
CREATE POLICY "Operators can view own profile"
    ON public.aegora_operators FOR SELECT
    USING (auth.uid() = auth_user_id);

CREATE POLICY "Admins have full operator access"
    ON public.aegora_operators FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.aegora_operators
            WHERE auth_user_id = auth.uid() AND role IN ('ADMIN', 'SUPER_ADMIN')
        )
    );

-- Merkle Blocks: Authenticated operators can read blocks (transparent verification); only system role can insert
CREATE POLICY "Authenticated users can verify Merkle chain"
    ON public.aegora_merkle_blocks FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "System service role can insert Merkle blocks"
    ON public.aegora_merkle_blocks FOR INSERT
    WITH CHECK (auth.role() = 'service_role');

-- Telemetry Events: Operators can insert telemetry; Admins view all
CREATE POLICY "Authenticated operators can record telemetry"
    ON public.aegora_telemetry_events FOR INSERT
    TO authenticated
    WITH CHECK (true);

CREATE POLICY "Operators view authorized telemetry"
    ON public.aegora_telemetry_events FOR SELECT
    TO authenticated
    USING (true);

-- SOAR Rules: All authenticated users can view enforced blocks; Admins can manage
CREATE POLICY "Operators can view active SOAR rules"
    ON public.aegora_soar_rules FOR SELECT
    TO authenticated
    USING (true);

CREATE POLICY "Admins can manage SOAR rules"
    ON public.aegora_soar_rules FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM public.aegora_operators
            WHERE auth_user_id = auth.uid() AND role IN ('ADMIN', 'SUPER_ADMIN')
        )
    );

-- n8n Incidents: Service role and Admins access
CREATE POLICY "Admins can view n8n incident queue"
    ON public.aegora_n8n_incidents FOR SELECT
    TO authenticated
    USING (
        EXISTS (
            SELECT 1 FROM public.aegora_operators
            WHERE auth_user_id = auth.uid() AND role IN ('ADMIN', 'SUPER_ADMIN')
        )
    );
