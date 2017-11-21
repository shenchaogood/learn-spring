package sc.learn.thrift.swift;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.Arrays;
import java.util.Objects;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.codec.ThriftStruct;

@ThriftStruct("TUser")
public final class TUser
{
    public TUser() {
    }

    private String name;

    @ThriftField(value=1, name="name", requiredness=Requiredness.REQUIRED)
    public String getName() { return name; }

    @ThriftField
    public void setName(final String name) { this.name = name; }

    private String email;

    @ThriftField(value=2, name="email", requiredness=Requiredness.REQUIRED)
    public String getEmail() { return email; }

    @ThriftField
    public void setEmail(final String email) { this.email = email; }

    private String password;

    @ThriftField(value=3, name="password", requiredness=Requiredness.REQUIRED)
    public String getPassword() { return password; }

    @ThriftField
    public void setPassword(final String password) { this.password = password; }

    private Long createTime;

    @ThriftField(value=4, name="createTime", requiredness=Requiredness.OPTIONAL)
    public Long getCreateTime() { return createTime; }

    @ThriftField
    public void setCreateTime(final Long createTime) { this.createTime = createTime; }

    private Long updateTime;

    @ThriftField(value=5, name="updateTime", requiredness=Requiredness.OPTIONAL)
    public Long getUpdateTime() { return updateTime; }

    @ThriftField
    public void setUpdateTime(final Long updateTime) { this.updateTime = updateTime; }

    private Integer id;

    @ThriftField(value=6, name="id", requiredness=Requiredness.OPTIONAL)
    public Integer getId() { return id; }

    @ThriftField
    public void setId(final Integer id) { this.id = id; }

    @Override
    public String toString()
    {
        return toStringHelper(this)
            .add("name", name)
            .add("email", email)
            .add("password", password)
            .add("createTime", createTime)
            .add("updateTime", updateTime)
            .add("id", id)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TUser other = (TUser)o;

        return
            Objects.equals(name, other.name) &&
            Objects.equals(email, other.email) &&
            Objects.equals(password, other.password) &&
            Objects.equals(createTime, other.createTime) &&
            Objects.equals(updateTime, other.updateTime) &&
            Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(new Object[] {
            name,
            email,
            password,
            createTime,
            updateTime,
            id
        });
    }
}
