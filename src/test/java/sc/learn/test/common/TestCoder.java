package sc.learn.test.common;

import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sc.learn.common.util.security.Coder;

public class TestCoder {
	
	@Test
	public void testBase64() throws Exception{
		File f=new File(getClass().getResource("/car.gif").getFile());
		byte[] bytes=FileUtils.readFileToByteArray(f);
		System.out.println(Base64.encodeBase64URLSafeString(Coder.encryptMD5(bytes)));
	}
	
	@Test
	public void testDecodeBase64()throws Exception{
		String url="http://staging.api.mifi.xiaomi.srv/v1/userinfo/idImg?key=28812ed7096fa6c7af8b3bba78e999d1&mifiId=392087489073119232";
		HttpGet httpGet = new HttpGet(url);
		String content = EntityUtils.toString(HttpClients.createDefault().execute(httpGet).getEntity());
		JSONObject json=(JSONObject)JSON.parse(content);
		byte[] pic1=Coder.decryptBASE64(json.getJSONObject("value").getString("front"));
		FileUtils.writeByteArrayToFile(new File("/tmp/learn-spring/photo1.jpg"), pic1);
		byte[] pic2=Coder.decryptBASE64(json.getJSONObject("value").getString("back"));
		FileUtils.writeByteArrayToFile(new File("/tmp/learn-spring/photo2.jpg"), pic2);
	}

}
