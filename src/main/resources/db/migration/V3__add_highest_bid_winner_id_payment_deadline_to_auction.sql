ALTER TABLE auctions
    ADD COLUMN highest_bid numeric(12, 2),
    ADD COLUMN winner_id UUID,
    ADD COLUMN payment_deadline TIMESTAMPTZ;

ALTER TABLE auctions ADD CONSTRAINT fk_auctions_winner FOREIGN KEY (winner_id) REFERENCES users(id);

ALTER TABLE auctions DROP CONSTRAINT IF EXISTS chk_auctions_status;

ALTER TABLE auctions ADD CONSTRAINT chk_auction_status CHECK (status IN (
                                                                         'ACTIVE',
                                                                         'DRAFT',
                                                                         'CANCELLED',
                                                                         'ENDED',
                                                                         'AWAITING_PAYMENT',
                                                                         'COMPLETED'
                                                                        ));