package sc.learn.test.jdk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class TestAIOApi {

	private class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel channel;

		public ReadCompletionHandler(AsynchronousSocketChannel channel) {
			if (this.channel == null)
				this.channel = channel;
		}

		@Override
		public void completed(Integer result, ByteBuffer attachment) {
			attachment.flip();
			byte[] body = new byte[attachment.remaining()];
			attachment.get(body);
			try {
				String req = new String(body, "UTF-8");
				System.out.println("The time server receive order : " + req);
				String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
				doWrite(currentTime);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		private void doWrite(String currentTime) {
			if (currentTime != null && currentTime.trim().length() > 0) {
				byte[] bytes = (currentTime).getBytes();
				ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
				writeBuffer.put(bytes);
				writeBuffer.flip();
				channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
					@Override
					public void completed(Integer result, ByteBuffer buffer) {
						// 如果没有发送完成，继续发送
						if (buffer.hasRemaining())
							channel.write(buffer, buffer, this);
					}

					@Override
					public void failed(Throwable exc, ByteBuffer attachment) {
						try {
							channel.close();
						} catch (IOException e) {
							// ingnore on close
						}
					}
				});
			}
		}

		@Override
		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {
		@Override
		public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
			attachment.asynchronousServerSocketChannel.accept(attachment, this);
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			result.read(buffer, buffer, new ReadCompletionHandler(result));
		}

		@Override
		public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
			exc.printStackTrace();
			attachment.latch.countDown();
		}
	}

	private class AsyncTimeServerHandler implements Runnable {
		CountDownLatch latch;
		AsynchronousServerSocketChannel asynchronousServerSocketChannel;

		public AsyncTimeServerHandler(int port) {
			try {
				asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
				asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
				System.out.println("The time server is start in port : " + port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			latch = new CountDownLatch(1);
			doAccept();
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void doAccept() {
			asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
		}

	}

	private class AsyncTimeClientHandler implements CompletionHandler<Void, AsyncTimeClientHandler>, Runnable {

		private AsynchronousSocketChannel client;
		private String host;
		private int port;
		private CountDownLatch latch;

		public AsyncTimeClientHandler(String host, int port) {
			this.host = host;
			this.port = port;
			try {
				client = AsynchronousSocketChannel.open();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {

			latch = new CountDownLatch(1);
			client.connect(new InetSocketAddress(host, port), this, this);
			try {
				latch.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void completed(Void result, AsyncTimeClientHandler attachment) {
			byte[] req = "QUERY TIME ORDER".getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
			writeBuffer.put(req);
			writeBuffer.flip();
			client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					if (buffer.hasRemaining()) {
						client.write(buffer, buffer, this);
					} else {
						ByteBuffer readBuffer = ByteBuffer.allocate(1024);
						client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
							@Override
							public void completed(Integer result, ByteBuffer buffer) {
								buffer.flip();
								byte[] bytes = new byte[buffer.remaining()];
								buffer.get(bytes);
								String body;
								try {
									body = new String(bytes, "UTF-8");
									System.out.println("Now is : " + body);
									latch.countDown();
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void failed(Throwable exc, ByteBuffer attachment) {
								try {
									client.close();
									latch.countDown();
								} catch (IOException e) {
									// ingnore on close
								}
							}
						});
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						client.close();
						latch.countDown();
					} catch (IOException e) {
						// ingnore on close
					}
				}
			});
		}

		@Override
		public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
			exc.printStackTrace();
			try {
				client.close();
				latch.countDown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private int port = 10003;

	@Test
	public void testServer() {
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer, "AIO-AsyncTimeServerHandler-001").start();
	}

	@Test
	public void testClient() {
		new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
	}

}
