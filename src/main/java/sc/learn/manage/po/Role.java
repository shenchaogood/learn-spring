package sc.learn.manage.po;

import java.util.Date;

public class Role {
    private Integer fId;

    private String fName;

    private String fDescription;

    private Date fCreateTime;

    private Date fUpdateTime;

    public Integer getfId() {
        return fId;
    }

    public void setfId(Integer fId) {
        this.fId = fId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName == null ? null : fName.trim();
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription == null ? null : fDescription.trim();
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