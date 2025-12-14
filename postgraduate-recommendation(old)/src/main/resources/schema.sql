/* 学院表 */
create table if not exists `college`
(
    id          bigint unsigned primary key,
    name        varchar(25) not null unique,
    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

/* 专业类别表 */
create table if not exists `major_category`
(
    id               bigint unsigned primary key,
    name             varchar(25)     not null unique,
    college_id       bigint unsigned not null,
    calculation_rule json            not null,
    create_time      datetime        not null default current_timestamp,
    update_time      datetime        not null default current_timestamp on update current_timestamp,

    index (college_id)/*查询某个学院的所有类别*/
);

/* 专业表 */
create table if not exists `major`
(
    id                bigint unsigned primary key,
    name              varchar(25)     not null,
    college_id        bigint unsigned not null,
    major_category_id bigint unsigned not null,
    create_time       datetime        not null default current_timestamp,
    update_time       datetime        not null default current_timestamp on update current_timestamp,

    index (major_category_id)/*查询某个专业类别下的所有专业*/
);

/* 用户表，仅保留基础信息 */
create table if not exists `user`
(
    id          bigint unsigned primary key,
    name        varchar(45), /*用户真实姓名*/
    college_id  bigint unsigned,
    account     varchar(11) not null unique,
    password    varchar(65) not null,/*BCrypt*/
    role        char(4)     not null, /*给每个角色自定义一串长度为4的字符串*/
    tel         char(11),
    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

/*学生中间表*/
create table if not exists `student_info`
(
    id          bigint unsigned primary key,
    user_id     bigint unsigned not null,
    major_id    bigint unsigned not null,
    create_time datetime        not null default current_timestamp,
    update_time datetime        not null default current_timestamp on update current_timestamp,

    index (user_id),
    index (major_id)
);

/*导员中间表*/
create table if not exists `counselor_info`
(
    id                bigint unsigned primary key,
    user_id           bigint unsigned not null,
    major_category_id bigint unsigned not null,
    create_time       datetime        not null default current_timestamp,
    update_time       datetime        not null default current_timestamp on update current_timestamp,

    index (user_id),
    index (major_category_id)
);

/*学生成绩表*/
create table if not exists `stu_score`
(
    id             bigint unsigned primary key,
    user_id        bigint unsigned  not null,
    major_rank     tinyint unsigned check (major_rank >= 1 and major_rank <= 200),
    weighted_score decimal(5, 2)    not null check (weighted_score > 0),
    status         tinyint unsigned not null default 0,/*只有两种状态，0未审核，1已认定*/
    create_time    datetime         not null default current_timestamp,
    update_time    datetime         not null default current_timestamp on update current_timestamp,

    index (user_id)
);

/* 指标点表 */
create table if not exists `indicator_points`
(
    id                bigint unsigned  not null primary key,
    major_category_id bigint unsigned  not null,
    name              varchar(255)     not null,
    level             tinyint unsigned not null,
    description       text,
    max_score         decimal(4, 2),
    item_upper_limit  int unsigned              default 999,
    parent_id         bigint unsigned,
    is_leaf           tinyint          not null,
    create_time       datetime         not null default current_timestamp,
    update_time       datetime         not null default current_timestamp on update current_timestamp,
    index (parent_id, is_leaf),
    index (major_category_id, level)
);

/* 申报记录表 */
create table if not exists `application`
(
    id              bigint unsigned not null primary key,
    user_id         bigint unsigned not null,/* 学生用户的id */
    first_indicator bigint unsigned not null,
    /* 学生仅申报最底层具体项（叶子节点） */
    indicator_id    bigint unsigned not null,/*指标点id，必须是叶子节点*/
    status          varchar(25)     not null default 'pending',
    item_name       varchar(255)    not null,/* 指标点项目名称 */
    description     text,
    create_time     datetime        not null default current_timestamp,
    update_time     datetime        not null default current_timestamp on update current_timestamp,

    index (user_id),
    index (indicator_id)/*某一指标下的所有申报记录*/,
    index (user_id, status)
);

/* 证明材料表 */
create table if not exists `application_evidences`
(
    id             bigint unsigned primary key,
    application_id bigint unsigned not null,/*申报记录表的id*/
    file_name      varchar(255)    not null,
    file_url       varchar(255)    not null,
    create_time    datetime        not null default current_timestamp,
    update_time    datetime        not null default current_timestamp on update current_timestamp,

    index (application_id)
);

/*申报记录审核日志表*/
create table if not exists `application_logging`
(
    id             bigint unsigned primary key,
    application_id bigint unsigned        not null,/*申报记录表的id*/
    user_id        bigint unsigned        not null,/*审核人员id*/
    score          decimal(4, 2) unsigned not null,
    status         varchar(25)            not null,
    description    text,/*审核意见*/
    create_time    datetime               not null default current_timestamp,
    update_time    datetime               not null default current_timestamp on update current_timestamp,

    index (application_id)
);

/*成绩审核日志表*/
create table if not exists `stu_score_logging`
(
    id             bigint unsigned primary key,
    stu_score_id   bigint unsigned  not null,/*学生成绩表id*/
    user_id        bigint unsigned  not null,/*审核人员id*/
    major_rank     tinyint unsigned check (major_rank >= 1 and major_rank <= 200),
    weighted_score decimal(5, 2)    not null check (weighted_score > 0),
    status         tinyint unsigned not null,/*只有两种状态，0未审核，1已认定*/
    create_time    datetime         not null default current_timestamp,
    update_time    datetime         not null default current_timestamp on update current_timestamp,

    index (stu_score_id)
);
