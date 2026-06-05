create schema if not exists gamification;

create table if not exists gamification.achievements (
  id uuid primary key,
  code varchar(80) unique not null,
  name varchar(180) not null,
  description text not null,
  rule_type varchar(80) not null,
  rule_config text not null,
  active boolean not null
);

create table if not exists gamification.user_achievements (
  id uuid primary key,
  user_id uuid not null,
  achievement_id uuid not null,
  unlocked_at timestamptz not null,
  event_id uuid not null,
  constraint uk_user_achievement unique(user_id, achievement_id)
);

insert into gamification.achievements (id, code, name, description, rule_type, rule_config, active)
values
('30000000-0000-0000-0000-000000000001','HYDRATION_HERO','Hydration Hero','Hydration score reached 90 or higher.','DAILY_SCORE','{"metric":"hydrationScore","min":90}',true),
('30000000-0000-0000-0000-000000000002','PROTEIN_WARRIOR','Protein Warrior','Protein intake patterns support daily target adherence.','TREND','{"metric":"protein","targetDays":14}',true),
('30000000-0000-0000-0000-000000000003','CONSISTENCY_MASTER','Consistency Master','Diet consistency score reached 85 or higher.','DAILY_SCORE','{"metric":"consistencyScore","min":85}',true),
('30000000-0000-0000-0000-000000000004','NUTRITION_CHAMPION','Nutrition Champion','Nutrition score reached 90 or higher.','DAILY_SCORE','{"metric":"nutritionScore","min":90}',true),
('30000000-0000-0000-0000-000000000005','THIRTY_DAY_STREAK','30-Day Streak','Logging streak reached 30 days.','STREAK','{"days":30}',true),
('30000000-0000-0000-0000-000000000006','HUNDRED_DAY_STREAK','100-Day Streak','Logging streak reached 100 days.','STREAK','{"days":100}',true),
('30000000-0000-0000-0000-000000000007','MACRO_MASTER','Macro Master','Macro balance score reached 85 or higher.','DAILY_SCORE','{"metric":"macroBalanceScore","min":85}',true),
('30000000-0000-0000-0000-000000000008','DEEP_DISCIPLINE','Deep Discipline','Long-term consistency behavior detected.','STREAK','{"days":60}',true)
on conflict (id) do nothing;

