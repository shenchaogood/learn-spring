/**定义包名 */
namespace java sc.learn.manage.service


/**定义请求的传输对象*/
struct TUserRequest{
1:required string name,
2:required i32 age,
3:required i64 birthday,
4:required string remark
}

/**定义返回的传输对象*/
struct TUserResponse{
1:required string name,
2:required i32 age,
3:required i64 birthday,
4:required string remark
}

/**定义两个接口方法*/
service TUserService{
	void save(1:TUserRequest user)
	TUserResponse findByName(1:string name);
}