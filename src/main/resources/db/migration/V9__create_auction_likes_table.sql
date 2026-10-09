create table auction_likes (
    id          uuid primary key,
    user_id     uuid not null,
    auction_id  uuid not null,
    created_at  timestamptz not null,

    constraint fk_auction_likes_user    foreign key (user_id)    references users (id)    on delete cascade,
    constraint fk_auction_likes_auction foreign key (auction_id) references auctions (id) on delete cascade,
    constraint uq_auction_like          unique (user_id, auction_id)
);

create index idx_auction_likes_auction_id on auction_likes (auction_id);
create index idx_auction_likes_user_id    on auction_likes (user_id);
