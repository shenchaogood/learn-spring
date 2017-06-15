/**定义包名 */
namespace java sc.learn.manage.service

struct TUser{
1:required string name,
2:required string email,
3:required string password
}

/**定义两个接口方法*/
service TUserService{
	void save(1:TUser user)
}