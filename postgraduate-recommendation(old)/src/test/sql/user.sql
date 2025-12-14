/*用户*/
insert into `user` (id, name, college_id, account, password, role, tel)
values (001,'小王',101,2023221544,'iampassword666','stude',17720667521),
       (002,'小李',101,2020998877,'iampassword999','fudao',17890667521);

/*学生用户信息*/
insert into `student_info` (id, user_id, major_id)
values (66601,001,1010101);

/*辅导员*/
insert into `counselor_info` (id, user_id, major_category_id)
values (66602,002,10101);

# /*根据学生的专业查出对应的类别*/
# explain
# select
#     mc.name as category_name,
#     m.name as major_name,
#     u.name as student_name
# from
#     `user` u
#         join
#     /*关联学生中间表获取学生所属专业id*/
#         student_info si on u.id = si.user_id
#         join
#     /*关联专业表获取专业对应的类别id*/
#         major m on si.major_id = m.id
#         join
#     /*关联专业类别表*/
#         major_category mc on m.major_category_id = mc.id
# where
#     u.id = 001;

SELECT status, COUNT(*)
FROM application
WHERE user_id = 001
GROUP BY status;

