package sc.learn.manage.po;

import java.util.Date;

public class Privilege {
    private Integer fId;

    private String fName;

    private String fDescription;

    private String fUrl;

    private String fIcon;

    private Boolean fLeaf;

    private Integer fParentId;

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

    public String getfUrl() {
        return fUrl;
    }

    public void setfUrl(String fUrl) {
        this.fUrl = fUrl == null ? null : fUrl.trim();
    }

    public String getfIcon() {
        return fIcon;
    }

    public void setfIcon(String fIcon) {
        this.fIcon = fIcon == null ? null : fIcon.trim();
    }

    public Boolean getfLeaf() {
        return fLeaf;
    }

    public void setfLeaf(Boolean fLeaf) {
        this.fLeaf = fLeaf;
    }

    public Integer getfParentId() {
        return fParentId;
    }

    public void setfParentId(Integer fParentId) {
        this.fParentId = fParentId;
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