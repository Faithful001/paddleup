create table users (
                       id          uuid primary key,
                       username    varchar(255) not null,
                       first_name  varchar(255) not null,
                       last_name   varchar(255) not null,
                       email       varchar(255) not null,
                       password    varchar(255) not null,
                       created_at  timestamptz  not null,
                       updated_at  timestamptz,

                       constraint uq_users_username unique (username),
                       constraint uq_users_email    unique (email)
);

create table auctions (
                          id              uuid primary key,
                          seller_id       uuid           not null,
                          title           varchar(255)   not null,
                          description     varchar(255),
                          starting_price  numeric(12, 2) not null,
                          reserve_price   numeric(12, 2),
                          min_increment   numeric(12, 2) not null default 1.00,
                          status          varchar(20)    not null default 'ACTIVE',
                          image_urls      varchar(500)[] not null default '{}',
                          ends_at         timestamptz    not null,
                          created_at      timestamptz    not null,
                          updated_at      timestamptz,

                          constraint fk_auctions_seller   foreign key (seller_id) references users (id),
                          constraint chk_starting_price   check (starting_price > 0),
                          constraint chk_min_increment    check (min_increment > 0),
                          constraint chk_image_urls_max   check (cardinality(image_urls) <= 10),
                          constraint chk_auctions_status  check (status in ('DRAFT', 'ACTIVE', 'CLOSED', 'CANCELLED'))
);

create table bids (
                      id          uuid primary key,
                      auction_id  uuid           not null,
                      bidder_id   uuid           not null,
                      amount      numeric(12, 2) not null,
                      created_at  timestamptz    not null,

                      constraint fk_bids_auction foreign key (auction_id) references auctions (id),
                      constraint fk_bids_bidder  foreign key (bidder_id)  references users (id),
                      constraint chk_bid_amount  check (amount > 0)
);

create index idx_auctions_seller         on auctions (seller_id);
create index idx_auctions_status_ends_at on auctions (status, ends_at);
create index idx_bids_auction_created    on bids (auction_id, created_at desc);
create index idx_bids_auction_amount     on bids (auction_id, amount desc);
create index idx_bids_bidder             on bids (bidder_id);