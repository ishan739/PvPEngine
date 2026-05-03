CREATE TABLE games (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(100) NOT NULL,
                       code VARCHAR(50) NOT NULL UNIQUE,
                       contact_email VARCHAR(255) NOT NULL,
                       webhook_url VARCHAR(500),
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_games_code ON games(code);
CREATE INDEX idx_games_status ON games(status);