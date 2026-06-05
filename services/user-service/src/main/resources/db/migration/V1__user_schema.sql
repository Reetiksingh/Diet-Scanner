create schema if not exists users;

create table if not exists users.users (
  id uuid primary key,
  email varchar(320) not null unique,
  display_name varchar(160) not null,
  avatar_url text,
  country_code char(2),
  timezone varchar(80) not null,
  status varchar(40) not null,
  created_at timestamptz not null,
  updated_at timestamptz not null,
  version bigint not null default 0
);

create table if not exists users.user_profiles (
  user_id uuid primary key,
  age int,
  height_cm numeric(5,2),
  weight_kg numeric(5,2),
  gender varchar(40),
  activity_level varchar(40),
  dietary_preference varchar(80),
  updated_at timestamptz
);

create table if not exists users.user_goals (
  id uuid primary key,
  user_id uuid not null,
  goal_type varchar(40) not null,
  target_weight_kg numeric(5,2),
  target_calories int,
  protein_target_g numeric(6,2),
  carbs_target_g numeric(6,2),
  fats_target_g numeric(6,2),
  water_target_ml int,
  active boolean not null,
  created_at timestamptz not null
);

create index if not exists idx_user_goals_user_active on users.user_goals(user_id, active, created_at desc);

create table if not exists users.body_measurements (
  id uuid primary key,
  user_id uuid not null,
  measured_on date not null,
  weight_kg numeric(5,2) not null,
  body_fat_percent numeric(5,2),
  created_at timestamptz not null
);

create index if not exists idx_body_measurements_user_date on users.body_measurements(user_id, measured_on desc);

