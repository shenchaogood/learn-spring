package sc.learn.test.swift;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import org.apache.thrift.TException;
import org.junit.Test;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientManager;
import com.facebook.swift.service.ThriftServer;

import sc.learn.thrift.swift.TManageService;

public class TestSwift {
	
	@Test
	public void testSwiftServer() throws InterruptedException{
		ServerCreator serverCreator = new ServerCreator().invoke();
        ThriftServer server = serverCreator.getServer();
        server.start();
        System.out.println("服务已启动!");
        Thread.sleep(500000);
	}
	
	@Test
	public void testSwiftClient() throws InterruptedException, ExecutionException, TException{
		ThriftClientManager clientManager = new ThriftClientManager();
		TManageService service = clientManager.createClient(
                new FramedClientConnector(/*fromParts("localhost", 12345)*/new InetSocketAddress(12345)),
                TManageService.class).get();
        System.out.println(service.getUserById(1));
 
        int max = 100000;
        Long start = System.currentTimeMillis();
        for (int i = 0; i < max; i++) {
        	service.getUserById(1);
        }
        Long end = System.currentTimeMillis();
        Long elapse = end - start;
        int perform = Double.valueOf(max / (elapse / 1000d)).intValue();
 
        System.out.print("thrift " + max + " 次RPC调用，耗时：" + elapse + "毫秒，平均" + perform + "次/秒");
        clientManager.close();
	}

}
