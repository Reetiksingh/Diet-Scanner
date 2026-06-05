create schema if not exists gamification;

create table if not exists gamification.challenges (
  id uuid primary key,
  code varchar(80) unique not null,
  name varchar(180) not null,
  description text,
  challenge_type varchar(80) not null,
  metric varchar(80) not null,
  starts_at timestamptz not null,
  ends_at timestamptz not null,
  status varchar(40) not null
);

create table if not exists gamification.challenge_participants (
  id uuid primary key,
  challenge_id uuid not null,
  user_id uuid not null,
  joined_at timestamptz not null,
  progress numeric(10,2) not null,
  completed_at timestamptz,
  constraint uk_challenge_participant unique(challenge_id, user_id)
);

insert into gamification.challenges (id, code, name, description, challenge_type, metric, starts_at, ends_at, status)
values
('20000000-0000-0000-0000-000000000001','PROTEIN_CHALLENGE','Protein Challenge','Reach protein target consistently for seven days.','WEEKLY','protein-score',now(),now() + interval '30 days','ACTIVE'),
('20000000-0000-0000-0000-000000000002','HYDRATION_CHALLENGE','Hydration Challenge','Hit hydration target and improve hydration score.','WEEKLY','hydration-score',now(),now() + interval '30 days','ACTIVE'),
('20000000-0000-0000-0000-000000000003','BREAKFAST_CHALLENGE','Healthy Breakfast Challenge','Log balanced breakfasts consistently.','WEEKLY','consistency-score',now(),now() + interval '30 days','ACTIVE')
on conflict (id) do nothing;

