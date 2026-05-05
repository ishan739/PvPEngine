CREATE TABLE processed_events (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  consumer_group VARCHAR(100) NOT NULL,
                                  event_id VARCHAR(255) NOT NULL,
                                  topic VARCHAR(255) NOT NULL,
                                  processed_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Core idempotency constraint
    -- Same event cannot be processed twice by same consumer group
                                  CONSTRAINT uq_processed_events UNIQUE (consumer_group, event_id)
);

CREATE INDEX idx_processed_events_group_event ON processed_events(consumer_group, event_id);