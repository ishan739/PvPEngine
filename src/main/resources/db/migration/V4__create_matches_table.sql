CREATE TABLE matches (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                         player1_id UUID NOT NULL REFERENCES players(id),
                         player2_id UUID NOT NULL REFERENCES players(id),
                         status VARCHAR(20) NOT NULL DEFAULT 'MATCH_FOUND',
                         game_session_id VARCHAR(255),
                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                         started_at TIMESTAMPTZ,
                         completed_at TIMESTAMPTZ
);

CREATE INDEX idx_matches_game_id ON matches(game_id);
CREATE INDEX idx_matches_player1 ON matches(player1_id);
CREATE INDEX idx_matches_player2 ON matches(player2_id);
CREATE INDEX idx_matches_status ON matches(status);