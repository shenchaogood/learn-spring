package sc.learn.test.netty;

import java.io.Serializable;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.junit.Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestMarshalling {

	class SubscribeReq implements Serializable {

		/**
		 * 默认的序列号ID
		 */
		private static final long serialVersionUID = 1L;

		private int subReqID;

		private String userName;

		private String productName;

		private String phoneNumber;

		private String address;

		/**
		 * @return the subReqID
		 */
		public final int getSubReqID() {
			return subReqID;
		}

		/**
		 * @param subReqID
		 *            the subReqID to set
		 */
		public final void setSubReqID(int subReqID) {
			this.subReqID = subReqID;
		}

		/**
		 * @return the userName
		 */
		public final String getUserName() {
			return userName;
		}

		/**
		 * @param userName
		 *            the userName to set
		 */
		public final void setUserName(String userName) {
			this.userName = userName;
		}

		/**
		 * @return the productName
		 */
		public final String getProductName() {
			return productName;
		}

		/**
		 * @param productName
		 *            the productName to set
		 */
		public final void setProductName(String productName) {
			this.productName = productName;
		}

		/**
		 * @return the phoneNumber
		 */
		public final String getPhoneNumber() {
			return phoneNumber;
		}

		/**
		 * @param phoneNumber
		 *            the phoneNumber to set
		 */
		public final void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		/**
		 * @return the address
		 */
		public final String getAddress() {
			return address;
		}

		/**
		 * @param address
		 *            the address to set
		 */
		public final void setAddress(String address) {
			this.address = address;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SubscribeReq [subReqID=" + subReqID + ", userName=" + userName + ", productName=" + productName + ", phoneNumber=" + phoneNumber + ", address=" + address + "]";
		}
	}

	class SubscribeResp implements Serializable {

		/**
		 * 默认序列ID
		 */
		private static final long serialVersionUID = 1L;

		private int subReqID;

		private int respCode;

		private String desc;

		/**
		 * @return the subReqID
		 */
		public final int getSubReqID() {
			return subReqID;
		}

		/**
		 * @param subReqID
		 *            the subReqID to set
		 */
		public final void setSubReqID(int subReqID) {
			this.subReqID = subReqID;
		}

		/**
		 * @return the respCode
		 */
		public final int getRespCode() {
			return respCode;
		}

		/**
		 * @param respCode
		 *            the respCode to set
		 */
		public final void setRespCode(int respCode) {
			this.respCode = respCode;
		}

		/**
		 * @return the desc
		 */
		public final String getDesc() {
			return desc;
		}

		/**
		 * @param desc
		 *            the desc to set
		 */
		public final void setDesc(String desc) {
			this.desc = desc;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "SubscribeResp [subReqID=" + subReqID + ", respCode=" + respCode + ", desc=" + desc + "]";
		}

	}

	private static class MarshallingCodeCFactory {
		/**
		 * 创建Jboss Marshalling解码器MarshallingDecoder
		 * 
		 * @return
		 */
		public static MarshallingDecoder buildMarshallingDecoder() {
			final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
			final MarshallingConfiguration configuration = new MarshallingConfiguration();
			configuration.setVersion(5);
			UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
			MarshallingDecoder decoder = new MarshallingDecoder(provider, 1024);
			return decoder;
		}

		/**
		 * 创建Jboss Marshalling编码器MarshallingEncoder
		 * 
		 * @return
		 */
		public static MarshallingEncoder buildMarshallingEncoder() {
			final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
			final MarshallingConfiguration configuration = new MarshallingConfiguration();
			configuration.setVersion(5);
			MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
			MarshallingEncoder encoder = new MarshallingEncoder(provider);
			return encoder;
		}
	}

	@Sharable
	private class SubReqServerHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			SubscribeReq req = (SubscribeReq) msg;
			if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
				System.out.println("Service accept client subscrib req : [" + req.toString() + "]");
				ctx.writeAndFlush(resp(req.getSubReqID()));
			}
		}

		private SubscribeResp resp(int subReqID) {
			SubscribeResp resp = new SubscribeResp();
			resp.setSubReqID(subReqID);
			resp.setRespCode(0);
			resp.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
			return resp;
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();// 发生异常，关闭链路
		}
	}

	private class SubReqClientHandler extends ChannelInboundHandlerAdapter {

		/**
		 * Creates a client-side handler.
		 */
		public SubReqClientHandler() {
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			for (int i = 0; i < 10; i++) {
				ctx.write(subReq(i));
			}
			ctx.flush();
		}

		private SubscribeReq subReq(int i) {
			SubscribeReq req = new SubscribeReq();
			req.setAddress("NanJing YuHuaTai");
			req.setPhoneNumber("138xxxxxxxxx");
			req.setProductName("Netty Book For Marshalling");
			req.setSubReqID(i);
			req.setUserName("Lilinfeng");
			return req;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("Receive server response : [" + msg + "]");
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			ctx.flush();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}
	}

	private int port = 10005;

	@Test
	public void testServer() throws InterruptedException {
		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) {
							ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
							ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
							ch.pipeline().addLast(new SubReqServerHandler());
						}
					});

			// 绑定端口，同步等待成功
			ChannelFuture f = b.bind(port).sync();

			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Test
	public void testClient() throws InterruptedException {
		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
					ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
					ch.pipeline().addLast(new SubReqClientHandler());
				}
			});

			// 发起异步连接操作
			ChannelFuture f = b.connect("127.0.0.1", port).sync();

			// 当代客户端链路关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}
}
