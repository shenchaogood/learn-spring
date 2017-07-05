package sc.learn.test.common;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import sc.learn.common.util.security.Coder;

public class TestCoder {
	
	@Test
	public void testBase64() throws Exception{
		File f=new File(getClass().getResource("/car.gif").getFile());
		byte[] bytes=FileUtils.readFileToByteArray(f);
		String base64=Coder.encryptBASE64(bytes);
		System.out.println(Coder.encryptMD5(base64));
		System.out.println(Base64.encodeBase64URLSafeString(Coder.encryptMD5(bytes)));
	}

}
