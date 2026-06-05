create schema if not exists analytics;

create table if not exists analytics.daily_analytics (
  id uuid primary key,
  user_id uuid not null,
  analytics_date date not null,
  calories numeric(8,2),
  protein_g numeric(8,2),
  carbs_g numeric(8,2),
  fats_g numeric(8,2),
  fiber_g numeric(8,2),
  sugar_g numeric(8,2),
  water_ml int,
  nutrition_score int,
  macro_balance_score int,
  consistency_score int,
  hydration_score int,
  diet_quality_score int,
  late_night_calorie_ratio numeric(5,2),
  skipped_breakfast boolean,
  meal_timing_variance_minutes int,
  computed_at timestamptz,
  constraint uk_daily_analytics_user_date unique(user_id, analytics_date)
);

create index if not exists idx_daily_analytics_user_date on analytics.daily_analytics(user_id, analytics_date desc);

create table if not exists analytics.weekly_analytics (
  id uuid primary key,
  user_id uuid not null,
  week_start date not null,
  week_end date not null,
  avg_nutrition_score int,
  avg_consistency_score int,
  avg_hydration_score int,
  streak_days int,
  calorie_spike_count int,
  late_night_eating_count int,
  generated_at timestamptz
);

create table if not exists analytics.monthly_analytics (
  id uuid primary key,
  user_id uuid not null,
  month_start date not null,
  month_end date not null,
  avg_nutrition_score int,
  avg_consistency_score int,
  adherence_days int,
  best_metric varchar(80),
  weakest_metric varchar(80),
  generated_at timestamptz
);

create table if not exists analytics.nutrition_insights (
  id uuid primary key,
  user_id uuid not null,
  insight_type varchar(80) not null,
  title varchar(180) not null,
  message text not null,
  evidence text not null,
  severity varchar(40) not null,
  period_start date not null,
  period_end date not null,
  created_at timestamptz not null
);

create table if not exists analytics.inbox_events (
  event_id uuid primary key,
  topic varchar(160) not null,
  processed_at timestamptz not null
);

