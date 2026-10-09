create table followers (
    id           uuid primary key,
    follower_id  uuid not null,
    following_id uuid not null,
    created_at   timestamptz not null,

    constraint fk_followers_follower  foreign key (follower_id)  references users (id) on delete cascade,
    constraint fk_followers_following foreign key (following_id) references users (id) on delete cascade,
    constraint uq_follower_following  unique (follower_id, following_id),
    constraint chk_not_self_follow    check (follower_id <> following_id)
);

create index idx_followers_follower_id  on followers (follower_id);
create index idx_followers_following_id on followers (following_id);
