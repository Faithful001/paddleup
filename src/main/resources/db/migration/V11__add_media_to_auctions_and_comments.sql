-- Add unified media jsonb column to auctions and comments
ALTER TABLE auctions ADD COLUMN media jsonb NOT NULL DEFAULT '[]'::jsonb;

-- Migrate existing image_urls to media objects in auctions
UPDATE auctions
SET media = (
    SELECT coalesce(jsonb_agg(jsonb_build_object('url', u, 'type', 'IMAGE')), '[]'::jsonb)
    FROM unnest(image_urls) AS u
)
WHERE image_urls IS NOT NULL AND cardinality(image_urls) > 0;

-- Drop legacy image_urls column and its constraint
ALTER TABLE auctions DROP CONSTRAINT IF EXISTS chk_image_urls_max;
ALTER TABLE auctions DROP COLUMN IF EXISTS image_urls;

-- Add unified media jsonb column to comments
ALTER TABLE comments ADD COLUMN media jsonb NOT NULL DEFAULT '[]'::jsonb;
