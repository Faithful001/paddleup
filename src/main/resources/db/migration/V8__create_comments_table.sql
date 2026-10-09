create table comments (
    id          uuid primary key,
    auction_id  uuid not null,
    user_id     uuid not null,
    parent_id   uuid,
    content     varchar(1000) not null,
    is_deleted  boolean not null default false,
    created_at  timestamptz not null,
    updated_at  timestamptz not null,

    constraint fk_comments_auction foreign key (auction_id) references auctions (id) on delete cascade,
    constraint fk_comments_user    foreign key (user_id)    references users (id)    on delete cascade,
    constraint fk_comments_parent  foreign key (parent_id)  references comments (id) on delete cascade
);

create index idx_comments_auction_created on comments (auction_id, created_at desc) where parent_id is null;
create index idx_comments_parent_id       on comments (parent_id) where parent_id is not null;
create index idx_comments_user_id         on comments (user_id);
