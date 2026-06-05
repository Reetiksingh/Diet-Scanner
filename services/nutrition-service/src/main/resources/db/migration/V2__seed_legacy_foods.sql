insert into nutrition.foods (id, barcode, name, brand, source, serving_size, serving_unit, created_at)
values
('10000000-0000-0000-0000-000000000001','8901002020020','Regular Biscuits','Legacy Diet Scanner','LEGACY',100,'g',now()),
('10000000-0000-0000-0000-000000000002','8901234567890','Whole Wheat Bread','Legacy Diet Scanner','LEGACY',100,'g',now()),
('10000000-0000-0000-0000-000000000003','8901111222333','Refined Flour','Legacy Diet Scanner','LEGACY',100,'g',now()),
('10000000-0000-0000-0000-000000000004','8901444555666','Regular Cola','Legacy Diet Scanner','LEGACY',330,'ml',now()),
('10000000-0000-0000-0000-000000000005','8901777888999','Oats Cereal','Legacy Diet Scanner','LEGACY',40,'g',now())
on conflict (id) do nothing;

insert into nutrition.food_nutrition (food_id, calories, protein_g, carbs_g, fats_g, fiber_g, sugar_g, sodium_mg)
values
('10000000-0000-0000-0000-000000000001',480,6,65,20,2,12,200),
('10000000-0000-0000-0000-000000000002',265,9,49,3,7,3,400),
('10000000-0000-0000-0000-000000000003',364,10,76,1,2,0,2),
('10000000-0000-0000-0000-000000000004',140,0,39,0,0,39,40),
('10000000-0000-0000-0000-000000000005',150,5,27,3,4,1,100)
on conflict (food_id) do nothing;

