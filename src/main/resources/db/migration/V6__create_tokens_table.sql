create table tokens (
    id          uuid primary key,
    user_id     uuid          not null,
    token       varchar(1024) not null,
    token_type  varchar(50)   not null,
    revoked     boolean       not null default false,
    expired     boolean       not null default false,
    expires_at  timestamptz   not null,
    created_at  timestamptz   not null,
    updated_at  timestamptz,

    constraint fk_tokens_user foreign key (user_id) references users (id) on delete cascade,
    constraint uq_tokens_token unique (token)
);

create index idx_tokens_user on tokens (user_id);
create index idx_tokens_token on tokens (token);
create index idx_tokens_user_type on tokens (user_id, token_type);
