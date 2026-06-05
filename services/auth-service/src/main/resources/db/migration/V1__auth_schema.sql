create schema if not exists auth;

create table if not exists auth.oauth_identities (
  id uuid primary key,
  user_id uuid not null,
  provider varchar(40) not null,
  provider_subject varchar(255) not null,
  email varchar(320) not null,
  email_verified boolean not null,
  created_at timestamptz not null,
  constraint uk_oauth_provider_subject unique (provider, provider_subject)
);

create table if not exists auth.refresh_token_families (
  id uuid primary key,
  user_id uuid not null,
  family_id uuid not null unique,
  current_token_hash varchar(255) not null,
  expires_at timestamptz not null,
  revoked_at timestamptz,
  reuse_detected_at timestamptz,
  created_at timestamptz not null,
  version bigint not null default 0
);

create index if not exists idx_refresh_user on auth.refresh_token_families(user_id);

create table if not exists auth.revoked_access_tokens (
  jti varchar(255) primary key,
  user_id uuid not null,
  expires_at timestamptz not null,
  revoked_at timestamptz not null
);

create index if not exists idx_revoked_access_expires on auth.revoked_access_tokens(expires_at);

create table if not exists auth.login_audit (
  id uuid primary key,
  user_id uuid,
  provider varchar(40),
  ip_address varchar(80),
  user_agent text,
  success boolean not null,
  created_at timestamptz not null
);

create table if not exists auth.outbox_events (
  id uuid primary key,
  topic varchar(160) not null,
  event_type varchar(120) not null,
  payload text not null,
  created_at timestamptz not null,
  published_at timestamptz
);

