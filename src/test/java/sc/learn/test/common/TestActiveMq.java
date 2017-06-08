package sc.learn.test.common;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestActiveMq {

	private static ActiveMQConnectionFactory connectionFactory;
//	private static final String BROKERURL = "failover:(" + "tcp://localhost:61617," + "tcp://localhost:61618," + "tcp://localhost:61619," + "tcp://localhost:61620,"
//			+ "tcp://localhost:61621," + "tcp://localhost:61622," + ")?randomize=false";

	static String BROKERURL="failover:tcp://10.38.161.15:6161";
	@BeforeClass
	public static void init() {
		connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, BROKERURL);
	}

	@Test
	public void testProducer() throws JMSException {
		Connection connection = connectionFactory.createConnection();
		// 启动连接
		connection.start();
		// 创建session
		Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		// 创建一个名称为HelloWorld的消息队列
		Queue destination = session.createQueue("HelloWorld");
		// 创建消息生产者
		MessageProducer messageProducer = session.createProducer(destination);
		// 发送消息
		TextMessage message = session.createTextMessage("ActiveMQ 发送消息");
		// 通过消息生产者发出消息
		messageProducer.send(message);
		session.commit();
		connection.close();
	}

	@Test
	public void testConsumer() throws JMSException {
		// 通过连接工厂获取连接
		Connection connection = connectionFactory.createConnection();
		// 启动连接
		connection.start();
		// 创建session
		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		// 创建一个连接HelloWorld的消息队列
		Queue destination = session.createQueue("HelloWorld");
		// 创建消息消费者
		MessageConsumer messageConsumer = session.createConsumer(destination);

		while (true) {
			TextMessage textMessage = (TextMessage) messageConsumer.receive(100000);
			if (textMessage != null) {
				System.out.println("收到的消息:" + textMessage.getText());
				textMessage.acknowledge();
//				session.recover();
			} else {
				break;
			}
		}
	}

}
