package sc.learn.manage.vo;

import sc.learn.manage.po.User;

public class UserVo extends User {
	private static final long serialVersionUID = 1L;
	
	private String volateCode;

	public String getVolateCode() {
		return volateCode;
	}

	public void setVolateCode(String volateCode) {
		this.volateCode = volateCode;
	}
	
}
