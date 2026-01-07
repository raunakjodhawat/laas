-- ============================================================
-- Extensions
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS citext;

-- ============================================================
-- Users table
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email CITEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    password_algo TEXT NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- updated_at trigger
-- ============================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

-- ============================================================
-- Email verification table
-- ============================================================

CREATE TABLE IF NOT EXISTS email_verifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT email_verifications_expires_check
    CHECK (expires_at > created_at)
);

-- ============================================================
-- Login audit table
-- ============================================================

CREATE TABLE IF NOT EXISTS login_audit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL,
    email CITEXT NOT NULL,
    ip_address INET NOT NULL,
    user_agent TEXT,
    success BOOLEAN NOT NULL,
    failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Indexes
-- ============================================================

-- Login audit indexes
CREATE INDEX IF NOT EXISTS login_audit_user_id_idx
    ON login_audit(user_id);

CREATE INDEX IF NOT EXISTS login_audit_email_idx
    ON login_audit(email);

CREATE INDEX IF NOT EXISTS login_audit_created_at_idx
    ON login_audit(created_at);

-- Fast lookup for failed login attempts (rate limiting / lockout)
CREATE INDEX IF NOT EXISTS login_audit_failed_idx
    ON login_audit(email, created_at)
    WHERE success = FALSE;

-- Email verification indexes
CREATE INDEX IF NOT EXISTS email_verifications_user_id_idx
    ON email_verifications(user_id);

CREATE INDEX IF NOT EXISTS email_verifications_expires_at_idx
    ON email_verifications(expires_at);

CREATE UNIQUE INDEX IF NOT EXISTS email_verifications_token_hash_idx
    ON email_verifications(token_hash);

-- Ensure only one active (unconsumed) verification token per user
CREATE UNIQUE INDEX IF NOT EXISTS email_verifications_active_user_idx
    ON email_verifications(user_id)
    WHERE consumed_at IS NULL;

-- ============================================================
-- Password expiry
-- ============================================================

ALTER TABLE users
    ADD COLUMN password_changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
ADD COLUMN password_expires_at TIMESTAMPTZ NOT NULL DEFAULT (now() + interval '90 days');

-- ============================================================
-- Password reset table
-- ============================================================

CREATE TABLE IF NOT EXISTS password_resets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    consumed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT password_resets_expires_check
    CHECK (expires_at > created_at)
);

CREATE INDEX IF NOT EXISTS password_resets_user_id_idx
    ON password_resets(user_id);

CREATE INDEX IF NOT EXISTS password_resets_expires_at_idx
    ON password_resets(expires_at);

CREATE UNIQUE INDEX IF NOT EXISTS password_resets_token_hash_idx
    ON password_resets(token_hash);

CREATE UNIQUE INDEX IF NOT EXISTS password_resets_active_user_idx
    ON password_resets(user_id)
    WHERE consumed_at IS NULL;

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT refresh_tokens_expires_check
        CHECK (expires_at > created_at)
);

CREATE UNIQUE INDEX refresh_tokens_token_hash_idx
    ON refresh_tokens(token_hash);

CREATE INDEX refresh_tokens_user_id_idx
    ON refresh_tokens(user_id);

CREATE INDEX refresh_tokens_expires_at_idx
    ON refresh_tokens(expires_at);

ALTER TABLE users
    ADD CONSTRAINT users_password_expiry_check
        CHECK (password_expires_at > password_changed_at);
