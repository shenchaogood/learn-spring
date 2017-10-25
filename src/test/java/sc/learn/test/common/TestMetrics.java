package sc.learn.test.common;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge.Ratio;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

public class TestMetrics {

	MetricRegistry metrics = new MetricRegistry();
	Queue<String> queue = new LinkedList<>();
	ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();
	JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();

	@Before
	public void before() {
		reporter.start(1, TimeUnit.SECONDS);
		// 测试JMX
		// jmxReporter.start();
	}

	/**
	 * Gauge代表一个度量的即时值。 当你开汽车的时候， 当前速度是Gauge值。 你测体温的时候， 体温计的刻度是一个Gauge值。
	 * 当你的程序运行的时候， 内存使用量和CPU占用率都可以通过Gauge值来度量。 比如我们可以查看一个队列当前的size。
	 */
	@Test
	public void testGauge() throws Exception {
		Gauge<Integer> gauge = metrics.register(MetricRegistry.name(TestMetrics.class, "Gauges", "size"), queue::size);

		while (true) {
//			gauge.getValue();
			// reporter.report();
			queue.add("");
			Thread.sleep(1000);
		}
	}

	@Test
	public void testRatioGauge() throws Exception {
		Ratio.of(5, 6).getValue();
	}

	/**
	 * Counter是一个AtomicLong实例， 可以增加或者减少值。 例如，可以用它来计数队列中加入的Job的总数。
	 */
	@Test
	public void testCounter() throws Exception {
		Counter pendingJobs = metrics.counter(MetricRegistry.name(TestMetrics.class, "pending-jobs"));
		for (int i = 0; i < 5; i++) {
			pendingJobs.inc();
			queue.offer("job");
			// System.out.println("第"+(i+1)+"次循环"+pendingJobs.getCount());
		}

		pendingJobs.dec();
		queue.remove();
		// System.out.println("final:"+pendingJobs.getCount());
		Thread.sleep(9000000);
	}

	/**
	 * Meter用来计算事件的速率。 例如 request per second。 还可以提供1分钟，5分钟，15分钟不断更新的平均速率。
	 */
	@Test
	public void testMeter() throws Exception {
		Meter requests = metrics.meter(MetricRegistry.name(TestMetrics.class, "requests"));
		while (true) {
			if(requests.getCount()<5){
				requests.mark();
			}
			Thread.sleep(1000);
		}
	}

	/**
	 * Histogram可以为数据流提供统计数据。 除了最大值，最小值，平均值外，它还可以测量 中值(median)，
	 * 百分比比如XX%这样的Quantile数据 。
	 */
	@Test
	public void testHistogram() throws Exception {
		Histogram responseSizes = metrics.histogram(MetricRegistry.name(TestMetrics.class, "response-sizes"));
		while (true) {
			if(responseSizes.getCount()<5){
				responseSizes.update(new Random().nextInt(100));
			}
			Thread.sleep(1000);
		}
	}

	@Test
	public void testTimer() throws Exception {
		Timer timer = metrics.timer(MetricRegistry.name(TestMetrics.class, "responses"));
		Random rn = new Random();
		while (true) {
			Context context=timer.time();
			Thread.sleep(rn.nextInt(1000));
			context.stop();
		}
	}
}
