create schema if not exists recommendations;

create table if not exists recommendations.recommendations (
  id uuid primary key,
  user_id uuid not null,
  recommendation_type varchar(80) not null,
  title varchar(180) not null,
  body text not null,
  evidence text not null,
  priority int not null,
  status varchar(40) not null,
  valid_until timestamptz,
  created_at timestamptz not null
);

create index if not exists idx_recommendations_user_status_priority on recommendations.recommendations(user_id, status, priority desc);

create table if not exists recommendations.inbox_events (
  event_id uuid primary key,
  topic varchar(160) not null,
  processed_at timestamptz not null
);

