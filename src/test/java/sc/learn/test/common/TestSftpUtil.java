package sc.learn.test.common;

import org.junit.Test;

import sc.learn.common.util.SFTPUtil;
import sc.learn.common.util.SFTPUtil.SFTPClient;

public class TestSftpUtil {
	
	private final static String USER=System.getProperty("user.name");
	
	@Test
	public void testDownload() throws Exception{
		SFTPClient client=SFTPUtil.createSFTPClient(USER, "XXXXXX", "localhost", 22);
		client.login();
		client.download("/home/"+USER, "hs_err_pid15824.log", "/home/"+USER+"/hs_err_pid15824.log.bak");
		client.logout();  
	}
	
	@Test
	public void testUpload() throws Exception{
//		SFTPClient client=SFTPUtil.createSFTPClient(USER, "XXXXXX", "localhost", 22);
		SFTPClient client=SFTPUtil.createSFTPClient(USER, "localhost", 22,"/home/"+USER+"/.ssh/id_rsa");
		client.login();
		client.upload("/home/"+USER+"/aaa/bbb/ccc","/home/"+USER+"/下载/step01_20170310.sql");
		client.logout();  
	}
	

}
