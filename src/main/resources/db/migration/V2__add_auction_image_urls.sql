ALTER TABLE auctions ADD COLUMN image_urls varchar(500)[] NOT NULL DEFAULT '{}';

ALTER TABLE auctions ADD CONSTRAINT chk_image_urls_max CHECK (cardinality(image_urls) <= 10);