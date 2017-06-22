package sc.learn.manage.po;

import java.util.Date;

public class RolePrivilege {
    private Integer fRoleId;

    private Integer fPrivilegeId;

    private Date fCreateTime;

    private Date fUpdateTime;

    public Integer getfRoleId() {
        return fRoleId;
    }

    public void setfRoleId(Integer fRoleId) {
        this.fRoleId = fRoleId;
    }

    public Integer getfPrivilegeId() {
        return fPrivilegeId;
    }

    public void setfPrivilegeId(Integer fPrivilegeId) {
        this.fPrivilegeId = fPrivilegeId;
    }

    public Date getfCreateTime() {
        return fCreateTime;
    }

    public void setfCreateTime(Date fCreateTime) {
        this.fCreateTime = fCreateTime;
    }

    public Date getfUpdateTime() {
        return fUpdateTime;
    }

    public void setfUpdateTime(Date fUpdateTime) {
        this.fUpdateTime = fUpdateTime;
    }
}