create schema if not exists notifications;

create table if not exists notifications.notifications (
  id uuid primary key,
  user_id uuid not null,
  type varchar(80) not null,
  title varchar(180) not null,
  body text not null,
  payload text,
  read_at timestamptz,
  delivered_at timestamptz,
  created_at timestamptz not null
);

create index if not exists idx_notifications_user_created on notifications.notifications(user_id, created_at desc);

create table if not exists notifications.notification_preferences (
  user_id uuid primary key,
  achievement_notifications boolean not null,
  streak_reminders boolean not null,
  weekly_reports boolean not null,
  challenge_updates boolean not null,
  quiet_hours_start time,
  quiet_hours_end time
);

create table if not exists notifications.inbox_events (
  event_id uuid primary key,
  topic varchar(160) not null,
  processed_at timestamptz not null
);

