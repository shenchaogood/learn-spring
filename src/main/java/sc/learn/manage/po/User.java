package sc.learn.manage.po;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sc.learn.common.util.web.ServletContextUtil;


public class User implements Serializable{
	private static final long serialVersionUID = 1L;

	public User(){}

	public User(String name,String password,String email){
		this.name=name;
		this.password=password;
		this.email=email;
	}
	//用户id
	private Integer id;

	//用户名
	private String name;

	//用户密码
	private String password;
	
	//email
	private String email;
	
	//创建时间
	private Date createTime;
	
	//更新时间
	private Date updateTime;

	//用户拥有的角色
	private Set<Role> roles=new HashSet<Role>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 判断本用户是否有指定名称的权限
	 * 
	 * @param privilegeName
	 * @return
	 */
	public boolean hasPrivilegeByName(String privilegeName) {
		// 其他用户要是有权限才返回true
		for (Role role : roles) {
			for (Privilege privilege : role.getPrivileges()) {
				if (privilege.getName().equals(privilegeName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断本用户是否有指定URL的权限
	 * 
	 * @param privilegeUrl
	 * @return
	 */
	public boolean hasPrivilegeByUrl(String privilegeUrl) {
		
		if("admin".equals(name)) return true;
		// 如果以UI后缀结尾，就去掉UI后缀，以得到对应的权限（例如：addUI与add是同一个权限）
		if (privilegeUrl!=null&&privilegeUrl.endsWith("UI")) {
			privilegeUrl = privilegeUrl.substring(0, privilegeUrl.length() - 2);
		}
		
		@SuppressWarnings("unchecked")
		List<String> allPrivileges=(List<String>)ServletContextUtil.getServletContext().getAttribute("allPrivileges");
		
		if(!allPrivileges.contains(privilegeUrl)){
			return true;
		}
		// 如果是要控制的功能，则有权限才能使用
		for (Role role : roles) {
			for (Privilege privilege : role.getPrivileges()) {
				if (privilegeUrl!=null&&privilegeUrl.equals(privilege.getUrl())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", roles=" + roles + "]";
	}
}
