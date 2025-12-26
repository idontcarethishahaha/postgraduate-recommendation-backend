/* 学院表 */
create table if not exists `college`
(
    id          bigint unsigned primary key,
    name        varchar(25) not null unique,
    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp
);

/* 专业表 */
create table if not exists `major`
(
    id          bigint unsigned primary key,
    name        varchar(25) not null,
    college_id        bigint unsigned not null,
    major_category_id      bigint  unsigned not null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    index (major_category_id)/*查询某个专业类别下的所有专业*/
);

/* 用户表 */
create table if not exists `user`
(
    id          bigint primary key,
    name        varchar(15) not null,
    account     varchar(15) not null,
    password    varchar(65) not null,
    tel      varchar(11) null,
    role        char(4)     not null,
    college_id     bigint      null,
    major_id    bigint      null,
    major_category_id      bigint      null,

    create_time datetime    not null default current_timestamp,
    update_time datetime    not null default current_timestamp on update current_timestamp,

    unique (account),
    index (major_id)
);

/* 专业类别表 */
create table if not exists `major_category`
(
    id               bigint unsigned primary key,
    name             varchar(25)     not null unique,
    college_id       bigint unsigned not null,
    weighting   json        not null,
    deadline_time    datetime    null,/*截止时间*/

    create_time      datetime        not null default current_timestamp,
    update_time      datetime        not null default current_timestamp on update current_timestamp,

    index (college_id)/*查询某个学院的所有类别*/
);

create table if not exists `user_category`/*用户-类别关联表*/
(
    id          bigint primary key,
    user_id     bigint   not null,
    major_category_id      bigint   not null,

    create_time datetime not null default current_timestamp,
    update_time datetime not null default current_timestamp on update current_timestamp,

    /*双向查询（按用户查类别、按类别查用户）*/
    index (user_id),
    index (major_category_id)
);

create table if not exists `weighted_score`/*加权得分*/
(
    id          bigint primary key,
    score       decimal(6, 3)    not null check ( score > 0 and score <= 100.00),
    ranking     tinyint unsigned not null,/*排名*/
    verified    tinyint unsigned not null default 0,/*是否已审核认定*/
    logs varchar(500) null,

    create_time datetime         not null default current_timestamp,
    update_time datetime         not null default current_timestamp on update current_timestamp
);

create table if not exists `weighted_score_log`/*评分日志*/
(
    id          bigint primary key,
    student_id  bigint      not null,/*被评分学生*/
    user_id     bigint      not null,/*评分操作人*/

    create_time datetime    not null default current_timestamp,

    index (student_id)/*按学生查评分日志*/
);

create table if not exists `item`
(
    id          bigint primary key,
    name        varchar(200)           not null,
    major_category_id      bigint  unsigned   not null,/*不同类别可配置不同指标*/
    max_points  decimal(5, 2) unsigned not null comment '上限点数',
    max_items   tinyint unsigned       null comment '限项数',
    parent_id   bigint                 null comment '上级指标',
    comment     text                   null,

    create_time datetime               not null default current_timestamp,
    update_time datetime               not null default current_timestamp on update current_timestamp,

    index (parent_id, major_category_id)/*按类别 + 父指标查子指标*/
);


create table if not exists `student_item`/*学生指标提交表*/
(
    id           bigint primary key,
    user_id      bigint                 not null,/*绑定提交学生*/
    root_item_id bigint                 not null,/*绑定顶级指标（便于统计大类得分）*/
    item_id      bigint                 not null,/*绑定指标模板*/
    point        decimal(5, 2) unsigned null check ( point >= 0 and point <= 100 ),/*申请分数*/
    name         varchar(200)           not null,
    comment      text                   null,
    status       char(4)                not null comment '已提交；已驳回，已认定；待修改',

    create_time  datetime               not null default current_timestamp,
    update_time  datetime               not null default current_timestamp on update current_timestamp,

    index (user_id, root_item_id, item_id, status)/*按学生 + 指标 + 状态查询*/
);

create table if not exists `student_item_file`/*指标附件表*/
(
    id              bigint primary key,
    student_item_id bigint       not null,
    path            varchar(100) not null,
    filename        varchar(100) not null,
    create_time     datetime     not null default current_timestamp,
    update_time     datetime     not null default current_timestamp on update current_timestamp,

    index (student_item_id)
);

create table if not exists `student_item_log`/*指标审核日志表*/
(
    id              bigint primary key,
    student_item_id bigint      not null,/*绑定被审核的学生指标提交记录*/
    user_id         bigint      not null,/*审核操作人*/
    comment         text        null,/*审核备注(如：备注驳回理由)*/
    create_time     datetime    not null default current_timestamp,

    index (student_item_id)
);