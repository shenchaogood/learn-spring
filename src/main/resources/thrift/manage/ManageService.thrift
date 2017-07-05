/**定义包名 */
namespace java sc.learn.manage.service
include "manage/ManageModel.thrift"
service TManageService{
	void saveUser(1:ManageModel.TUser user);
	ManageModel.TUser getUserById(1:i32 id);
	
}