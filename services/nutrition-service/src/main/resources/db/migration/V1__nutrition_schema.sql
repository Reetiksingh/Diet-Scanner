create schema if not exists nutrition;

create table if not exists nutrition.foods (
  id uuid primary key,
  barcode varchar(80) unique,
  qr_code varchar(120),
  name varchar(180) not null,
  brand varchar(120),
  source varchar(40) not null,
  serving_size numeric(8,2),
  serving_unit varchar(40),
  created_by_user_id uuid,
  created_at timestamptz not null
);

create index if not exists idx_foods_name on nutrition.foods using gin (to_tsvector('english', name));
create index if not exists idx_foods_barcode on nutrition.foods(barcode);

create table if not exists nutrition.food_nutrition (
  food_id uuid primary key references nutrition.foods(id) on delete cascade,
  calories numeric(8,2),
  protein_g numeric(8,2),
  carbs_g numeric(8,2),
  fats_g numeric(8,2),
  fiber_g numeric(8,2),
  sugar_g numeric(8,2),
  sodium_mg numeric(8,2),
  potassium_mg numeric(8,2),
  saturated_fat_g numeric(8,2)
);

create table if not exists nutrition.meals (
  id uuid primary key,
  user_id uuid not null,
  meal_type varchar(40) not null,
  consumed_at timestamptz not null,
  timezone varchar(80) not null,
  notes text,
  created_at timestamptz not null,
  updated_at timestamptz not null,
  version bigint not null default 0
);

create index if not exists idx_meals_user_consumed on nutrition.meals(user_id, consumed_at desc);

create table if not exists nutrition.meal_entries (
  id uuid primary key,
  meal_id uuid not null references nutrition.meals(id) on delete cascade,
  food_id uuid not null references nutrition.foods(id),
  quantity numeric(8,2) not null,
  unit varchar(40) not null,
  calories numeric(8,2),
  protein_g numeric(8,2),
  carbs_g numeric(8,2),
  fats_g numeric(8,2),
  fiber_g numeric(8,2),
  sugar_g numeric(8,2),
  sodium_mg numeric(8,2)
);

create table if not exists nutrition.nutrition_records (
  id uuid primary key,
  user_id uuid not null,
  record_date date not null,
  calories numeric(8,2),
  protein_g numeric(8,2),
  carbs_g numeric(8,2),
  fats_g numeric(8,2),
  fiber_g numeric(8,2),
  sugar_g numeric(8,2),
  water_ml int,
  sodium_mg numeric(8,2),
  constraint uk_nutrition_record_user_date unique(user_id, record_date)
);

create table if not exists nutrition.scan_jobs (
  id uuid primary key,
  user_id uuid not null,
  scan_type varchar(40) not null,
  raw_text text,
  status varchar(40) not null,
  result_payload text,
  created_at timestamptz not null
);

create table if not exists nutrition.outbox_events (
  id uuid primary key,
  topic varchar(160) not null,
  event_type varchar(120) not null,
  payload text not null,
  created_at timestamptz not null,
  published_at timestamptz
);

