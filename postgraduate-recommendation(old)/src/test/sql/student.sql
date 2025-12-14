/*查询学生所属专业类别对应的一级指标点*/
<<<<<<< HEAD
=======
explain
select
    ip.name as first_level_name,
    mc.name as category_name
from
    `user` u
        join
    student_info si on u.id = si.user_id
        join
    /*学生->专业*/
        major m on si.major_id = m.id
        join
    /*专业->专业类别*/
        major_category mc on m.major_category_id = mc.id
        join
    /*类别->对应的指标点表*/
        indicator_points ip on mc.id = ip.major_category_id
where
    u.id = 001
  and ip.level = 1
  and ip.parent_id is null
order by
    ip.id;

>>>>>>> b55aa732c7c51a72477901feb869cbd43dcd4756


/*查询学生的加权分数和专业排名*/
select weighted_score,major_rank
from stu_score where user_id = 001;

