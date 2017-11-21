package sc.learn.thrift.swift;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.google.common.util.concurrent.ListenableFuture;

@ThriftService("TManageService")
public interface TManageService
{
    @ThriftService("TManageService")
    public interface Async
    {
        @ThriftMethod(value = "saveUser")
        ListenableFuture<Void> saveUser(
            @ThriftField(value=1, name="user", requiredness=Requiredness.NONE) final TUser user
        );

        @ThriftMethod(value = "getUserById")
        ListenableFuture<TUser> getUserById(
            @ThriftField(value=1, name="id", requiredness=Requiredness.NONE) final int id
        );
    }
    @ThriftMethod(value = "saveUser")
    void saveUser(
        @ThriftField(value=1, name="user", requiredness=Requiredness.NONE) final TUser user
    ) throws org.apache.thrift.TException;

    @ThriftMethod(value = "getUserById")
    TUser getUserById(
        @ThriftField(value=1, name="id", requiredness=Requiredness.NONE) final int id
    ) throws org.apache.thrift.TException;
}