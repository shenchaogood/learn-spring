package sc.learn.test.common;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;

public class TestBlockIO {

	@Test
	public void testServer() throws Exception {
		DatagramSocket socket = new DatagramSocket(8800);
		boolean isFirst=true;
		while (true) {
			if(isFirst){
				Thread.sleep(10000);
				isFirst=false;
			}
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			System.out.println("****服务器端已经启动，等待客户端发送信息");
			socket.receive(packet);
			String info = new String(data, 0, packet.getLength());
			System.out.println("我是服务器，客户端说：" + info);
		}
	}

	@Test
	public void testClient() throws Exception {
		InetAddress address = InetAddress.getByName("localhost");
		int port = 8800;
		DatagramSocket socket = new DatagramSocket();
		for(int i=0;i<1000000;i++){
			byte[] data = ("用户名：admin"+(i+1)+"; 密码:123").getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
			socket.send(packet);
		}
	}
}
