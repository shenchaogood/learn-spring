package sc.learn.test.jdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

public class TestBIOApi {

	private class TimeServerHandler implements Runnable {
		private Socket socket;

		public TimeServerHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
				out = new PrintWriter(this.socket.getOutputStream(), true);
				String currentTime = null;
				String body = null;
				while (true) {
					body = in.readLine();
					if (body == null)
						break;
					System.out.println("The time server receive order : " + body);
					currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					out.println(currentTime);
				}

			} catch (Exception e) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				if (out != null) {
					out.close();
					out = null;
				}
				if (this.socket != null) {
					try {
						this.socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					this.socket = null;
				}
			}
		}
	}

	private int port = 10001;

	@Test
	public void testBioServer() throws Exception {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
//			while (true) {
				socket = server.accept();
				new Thread(new TimeServerHandler(socket)).start();
//			}
		} finally {
			if (server != null) {
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
		Thread.sleep(1000);
	}
	
	@Test
	public void testBioClient() throws IOException {
		Socket socket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			socket = new Socket("127.0.0.1", port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println("QUERY TIME ORDER");
			System.out.println("Send order 2 server succeed.");
			String resp = in.readLine();
			System.out.println("Now is : " + resp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}
		}
	}
}
