CREATE TABLE match_results (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               match_id UUID NOT NULL UNIQUE REFERENCES matches(id) ON DELETE CASCADE,
                               winner_player_id UUID NOT NULL REFERENCES players(id),
                               player1_score INTEGER NOT NULL DEFAULT 0,
                               player2_score INTEGER NOT NULL DEFAULT 0,
                               raw_payload TEXT,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_match_results_match_id ON match_results(match_id);
CREATE INDEX idx_match_results_winner ON match_results(winner_player_id);