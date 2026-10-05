ALTER TABLE auctions DROP CONSTRAINT chk_auctions_status;
ALTER TABLE auctions ADD CONSTRAINT chk_auctions_status
CHECK (status IN ('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED', 'SUSPENDED'));

ALTER TABLE auctions ADD CONSTRAINT chk_min_increment CHECK (min_increment > 0);