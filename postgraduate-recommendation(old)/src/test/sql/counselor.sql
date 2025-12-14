/*辅导员查看自己管理的专业*/
explain
select m.name, mc.name as category_name
from major m
         join major_category mc on m.major_category_id = mc.id
         join counselor_info ci on mc.id = ci.major_category_id
where ci.user_id = 002;

/*统计某学生所有“学术专长”相关的申报项目
关联指标点表，筛选属于“学术专长”一级指标的申报记录*/

