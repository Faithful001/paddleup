create table notifications (
    id            uuid primary key,
    recipient_id  uuid not null,
    type          varchar(30) not null,
    title         varchar(255) not null,
    body          varchar(500) not null,
    entity_id     uuid,
    is_read       boolean not null default false,
    created_at    timestamptz not null,

    constraint fk_notifications_recipient foreign key (recipient_id) references users (id) on delete cascade,
    constraint chk_notification_type check (type in (
        'BID_PLACED', 'OUTBID', 'COMMENT_POSTED', 'COMMENT_REPLIED', 'AUCTION_LIKED', 'NEW_FOLLOWER'
    ))
);

create index idx_notifications_recipient_created on notifications (recipient_id, created_at desc);
create index idx_notifications_recipient_unread  on notifications (recipient_id) where is_read = false;
