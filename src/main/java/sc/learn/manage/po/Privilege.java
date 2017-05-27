package sc.learn.manage.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Privilege implements Serializable{
	private static final long serialVersionUID = 1L;

	//权限id
	private Integer id;
	
	//权限名称
	private String name;
	
	//权限url
	private String url;
	
	//权限描述
	private String description;
	
	//权限图标
	private String icon;
	
	//当前节点是否是叶节点
	private Boolean leaf;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Privilege parent;
	
	//当前权限的子权限
	private List<Privilege> children = new ArrayList<Privilege>();
	//当前权限属于那个角色
	private Set<Role> roles=new HashSet<Role>();

	public Privilege(){}
	
	public Privilege(String name, String url, String description, String icon,
			Boolean leaf, Privilege parent) {
		super();
		this.name = name;
		this.url = url;
		this.description = description;
		this.icon = icon;
		this.leaf = leaf;
		this.parent = parent;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public Privilege getParent() {
		return parent;
	}

	public void setParent(Privilege parent) {
		this.parent = parent;
	}

	public List<Privilege> getChildren() {
		return children;
	}

	public void setChildren(List<Privilege> children) {
		this.children = children;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Privilege other = (Privilege) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Privilege [id=" + id + ", name=" + name + ", url=" + url + ", description=" + description + ", icon=" + icon + ", leaf=" + leaf
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + ", parent=" + parent + ", children=" + children + ", roles=" + roles + "]";
	}
	
}
