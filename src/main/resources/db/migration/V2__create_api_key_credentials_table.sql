CREATE TABLE api_key_credentials (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                                     key_prefix VARCHAR(30) NOT NULL UNIQUE,
                                     key_hash VARCHAR(255) NOT NULL,
                                     active BOOLEAN NOT NULL DEFAULT true,
                                     created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                     expires_at TIMESTAMPTZ,
                                     last_used_at TIMESTAMPTZ
);

CREATE INDEX idx_api_keys_game_id ON api_key_credentials(game_id);
CREATE INDEX idx_api_keys_key_prefix ON api_key_credentials(key_prefix);
CREATE INDEX idx_api_keys_active ON api_key_credentials(active);