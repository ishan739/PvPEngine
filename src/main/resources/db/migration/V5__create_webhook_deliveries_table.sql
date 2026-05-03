CREATE TABLE webhook_deliveries (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    game_id UUID NOT NULL REFERENCES games(id) ON DELETE CASCADE,
                                    match_id UUID NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
                                    event_type VARCHAR(50) NOT NULL,
                                    target_url VARCHAR(500) NOT NULL,
                                    request_body TEXT NOT NULL,
                                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                    attempt_count INTEGER NOT NULL DEFAULT 0,
                                    last_attempt_at TIMESTAMPTZ,
                                    response_code INTEGER,
                                    response_body TEXT,
                                    next_retry_at TIMESTAMPTZ,
                                    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_webhook_deliveries_game_id ON webhook_deliveries(game_id);
CREATE INDEX idx_webhook_deliveries_match_id ON webhook_deliveries(match_id);
CREATE INDEX idx_webhook_deliveries_status ON webhook_deliveries(status);
CREATE INDEX idx_webhook_deliveries_next_retry ON webhook_deliveries(next_retry_at) WHERE status = 'PENDING';