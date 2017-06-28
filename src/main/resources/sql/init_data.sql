use test;
insert into t_privilege
(f_id,f_name ,f_description ,f_url ,f_icon ,f_leaf ,f_parent_id ,f_create_time ,f_update_time)
values 
(1,'system','系统管理','/manage','',false,null,now(),now()),
(2,'employee','操作员管理','/manage/user/list','',true,1,now(),now()),
(3,'role','角色管理','/manage/role/list','',true,1,now(),now()),
(4,'privilege','权限管理','/manage/privilege/list','',true,1,now(),now())

;