create database if not exists test character set utf8;
use test;
create table if not exists t_user(
	f_id int primary key auto_increment,
	f_name varchar(20) unique not null comment '用户名',
	f_password varchar(50) not null comment '密码',
	f_email varchar(50) not null comment '邮箱',
	f_create_time datetime not null default now() comment '创建时间',
	f_update_time datetime not null default now() comment '最后修改时间'
)engine=InnoDB;

create table if not exists t_role(
	f_id int primary key auto_increment,
	f_name varchar(20) unique not null default '' comment '角色名',
	f_description varchar(255) not null default '' comment '角色描述',
	f_create_time datetime not null default now() comment '创建时间',
	f_update_time datetime not null default now() comment '最后修改时间'
)engine=InnoDB;

create table if not exists t_privilege(
	f_id int primary key auto_increment,
	f_name varchar(30) unique not null default '' comment '权限名',
	f_description varchar(255) not null default '' comment '权限描述',
	f_url varchar(255) unique not null default '' comment '请求URL，根据此URL进行权限拦截',
	f_icon varchar(255) not null default '' comment '打算菜单树图标',
	f_leaf boolean not null default false comment '是否为叶子节点',
	f_parent_id int not null default 0 comment '菜单父节点',
	f_create_time datetime not null default now() comment '创建时间',
	f_update_time datetime not null default now() comment '最后修改时间',
	foreign key(f_parent_id) references t_privilege(f_id)
)engine=InnoDB;

create table if not exists t_user_role(
	f_user_id int not null,
	f_role_id int not null,
	f_create_time datetime not null default now() comment '创建时间',
	f_update_time datetime not null default now() comment '最后修改时间',
	primary key (`f_user_id`,`f_role_id`),
	foreign key(f_user_id) references t_user(f_id),
	foreign key(f_role_id) references t_role(f_id)
)engine=InnoDB;

create table if not exists t_role_privilege(
	f_role_id int not null,
	f_privilege_id int not null,
	f_create_time datetime not null default now() comment '创建时间',
	f_update_time datetime not null default now() comment '最后修改时间',
	PRIMARY KEY (`f_role_id`,`f_privilege_id`),
	foreign key(f_role_id) REFERENCES t_role(f_id),
	foreign key(f_privilege_id) REFERENCES t_privilege(f_id)
)engine=InnoDB;