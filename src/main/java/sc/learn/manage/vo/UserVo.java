package sc.learn.manage.vo;

import java.util.List;

import sc.learn.manage.po.Privilege;
import sc.learn.manage.po.User;

public class UserVo extends User {
	
	private static final long serialVersionUID = 1L;
	private String volateCode;
	
	private List<Privilege> privileges;
	

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}

	public String getVolateCode() {
		return volateCode;
	}

	public void setVolateCode(String volateCode) {
		this.volateCode = volateCode;
	}
	
}
