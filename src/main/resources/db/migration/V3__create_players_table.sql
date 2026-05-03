CREATE TABLE players (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                         external_player_id VARCHAR(255),
                         username VARCHAR(50) NOT NULL,
                         email VARCHAR(255),
                         rating INTEGER NOT NULL DEFAULT 500,
                         is_banned BOOLEAN NOT NULL DEFAULT false,
                         ban_reason VARCHAR(500),
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         last_active_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- uniqueness is per-game, not global
                         CONSTRAINT uq_players_game_username UNIQUE (game_id, username),
                         CONSTRAINT uq_players_game_email UNIQUE (game_id, email),
                         CONSTRAINT uq_players_game_external_id UNIQUE (game_id, external_player_id)
);

CREATE INDEX idx_players_game_id ON players(game_id);
CREATE INDEX idx_players_game_username ON players(game_id, username);
CREATE INDEX idx_players_external_id ON players(game_id, external_player_id);