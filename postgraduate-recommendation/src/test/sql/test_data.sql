/*实际的数据插入应该由业务来实现，这里的数据仅临时用于模拟*/

/*插入学院数据*/
insert into `college` (id, name)
values (101,'计算机与控制工程学院'),
       (102,'机电工程学院');

/*插入类别数据*/
insert into `major_category` (id, name, college_id,calculation_rule)
values (10101,'计算机类',101,'{"weighted_score_ratio": 0.85, "comprehensive_score_ratio": 0.15}'),
       (10102,'电子类',101,'{"weighted_score_ratio": 0.80, "comprehensive_score_ratio": 0.20}');

/*插入专业数据*/
insert into `major` (id, name, college_id, major_category_id)
values (1010101,'软件工程',101,10101),
       (1010102,'计算机科学与技术',101,10101);


/*模拟学生申报记录*/
insert into `application` (id, user_id,first_indicator, indicator_id, status, item_name, description) values
     (134693, 001, 13257,13261, 'pending', '软件杯一等奖', '获得国家级竞赛一等奖'),
     (134694, 001, 13257,13261, 'pending', '数学建模一等奖', '获得国家级竞赛一等奖'),
     (134695, 001, 13257,13262, 'pending', 'XX竞赛二等奖', null),
     (134697, 001, 13257,13261, 'pass', '机器人竞赛一等奖', '获得国家级竞赛一等奖'),
     (134698, 001, 13257,13261, 'rejected', 'QQ杯一等奖', '获得国家级竞赛一等奖'),
     (134699, 001, 13257,13261, 'pass', '建模一等奖', '获得国家级竞赛一等奖'),
     (134700, 001, 13257,13262, 'revision', 'XX竞赛二等奖', null),
     (134701, 001, 13257,13261, 'rejected', 'QWQ竞赛一等奖', '获得国家级竞赛一等奖');

/*模拟学生成绩表*/
insert into `stu_score` (id, user_id, major_rank, weighted_score)
values (12345,001,1,96.5);