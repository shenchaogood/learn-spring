package sc.learn.test.jdk;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class TestNIOApi {

	class MultiplexerTimeServer implements Runnable {
		private Selector selector;
		private ServerSocketChannel servChannel;
		private volatile boolean stop;

		/**
		 * 初始化多路复用器、绑定监听端口
		 * 
		 * @param port
		 */
		public MultiplexerTimeServer(int port) {
			try {
				selector = Selector.open();
				servChannel = ServerSocketChannel.open();
				servChannel.configureBlocking(false);
				servChannel.socket().bind(new InetSocketAddress(port), 1024);
				servChannel.register(selector, SelectionKey.OP_ACCEPT);
				System.out.println("The time server is start in port : " + port);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		public void stop() {
			this.stop = true;
		}

		@Override
		public void run() {
			while (!stop) {
				try {
					selector.select(1000);
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					SelectionKey key = null;
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						try {
							handleInput(key);
						} catch (Exception e) {
							if (key != null) {
								key.cancel();
								if (key.channel() != null)
									key.channel().close();
							}
						}
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}

			// 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
			if (selector != null)
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		private void handleInput(SelectionKey key) throws IOException {

			if (key.isValid()) {
				// 处理新接入的请求消息
				if (key.isAcceptable()) {
					// Accept the new connection
					ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);
					// Add the new connection to the selector
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (key.isReadable()) {
					// Read the data
					SocketChannel sc = (SocketChannel) key.channel();
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int readBytes = sc.read(readBuffer);
					if (readBytes > 0) {
						readBuffer.flip();
						byte[] bytes = new byte[readBuffer.remaining()];
						readBuffer.get(bytes);
						String body = new String(bytes, "UTF-8");
						System.out.println("The time server receive order : " + body);
						String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
						doWrite(sc, currentTime);
					} else if (readBytes < 0) {
						// 对端链路关闭
						key.cancel();
						sc.close();
					} else
						; // 读到0字节，忽略
				}
			}
		}

		private void doWrite(SocketChannel channel, String response) throws IOException {
			if (response != null && response.trim().length() > 0) {
				byte[] bytes = response.getBytes();
				ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
				writeBuffer.put(bytes);
				writeBuffer.flip();
				channel.write(writeBuffer);
			}
		}
	}

	private class TimeClientHandle implements Runnable {
		private String host;
		private int port;
		private Selector selector;
		private SocketChannel socketChannel;
		private volatile boolean stop;

		public TimeClientHandle(String host, int port) {
			this.host = host == null ? "127.0.0.1" : host;
			this.port = port;
			try {
				selector = Selector.open();
				socketChannel = SocketChannel.open();
				socketChannel.configureBlocking(false);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		@Override
		public void run() {
			try {
				doConnect();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			while (!stop) {
				try {
					selector.select(1000);
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					SelectionKey key = null;
					while (it.hasNext()) {
						key = it.next();
						it.remove();
						try {
							handleInput(key);
						} catch (Exception e) {
							if (key != null) {
								key.cancel();
								if (key.channel() != null)
									key.channel().close();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}

			// 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
			if (selector != null)
				try {
					selector.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}

		private void handleInput(SelectionKey key) throws IOException {

			if (key.isValid()) {
				// 判断是否连接成功
				SocketChannel sc = (SocketChannel) key.channel();
				if (key.isConnectable()) {
					if (sc.finishConnect()) {
						sc.register(selector, SelectionKey.OP_READ);
						doWrite(sc);
					} else
						System.exit(1);// 连接失败，进程退出
				}
				if (key.isReadable()) {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					int readBytes = sc.read(readBuffer);
					if (readBytes > 0) {
						readBuffer.flip();
						byte[] bytes = new byte[readBuffer.remaining()];
						readBuffer.get(bytes);
						String body = new String(bytes, "UTF-8");
						System.out.println("Now is : " + body);
						this.stop = true;
					} else if (readBytes < 0) {
						// 对端链路关闭
						key.cancel();
						sc.close();
					} else
						; // 读到0字节，忽略
				}
			}

		}

		private void doConnect() throws IOException {
			// 如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
			if (socketChannel.connect(new InetSocketAddress(host, port))) {
				socketChannel.register(selector, SelectionKey.OP_READ);
				doWrite(socketChannel);
			} else
				socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}

		private void doWrite(SocketChannel sc) throws IOException {
			byte[] req = "QUERY TIME ORDER".getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
			writeBuffer.put(req);
			writeBuffer.flip();
			sc.write(writeBuffer);
			if (!writeBuffer.hasRemaining())
				System.out.println("Send order 2 server succeed.");
		}

	}

	private int port = 10002;

	@Test
	public void testServer() throws Exception {
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
		Thread.sleep(1000);
	}

	@Test
	public void testClient() throws Exception {
		new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
		Thread.sleep(1000);
	}
}
