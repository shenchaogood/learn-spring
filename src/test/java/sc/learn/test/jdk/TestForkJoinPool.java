package sc.learn.test.jdk;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestForkJoinPool {
	// **没有返回值得并行计算**
	// 继承RecursiveAction来实现"可分解"的任务
	class PrintTask extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		// 每个“小任务”只最多只打印50个数
		private static final int THRESHOLD = 10;
		private int start;
		private int end;

		// 打印从start到end的任务
		public PrintTask(int start, int end) {
			this.start = start;
			this.end = end;
		}

		@Override
		protected void compute() {
			// 当end与start之间的差小于THRESHOLD时，开始打印
			if (end - start < THRESHOLD) {
				for (int i = start; i < end; i++) {
					System.out.println(Thread.currentThread().getName() + "的i值：" + i);
				}
			} else {
				// 如果当end与start之间的差大于THRESHOLD时，即要打印的数超过50个
				// 将大任务分解成两个小任务。
				int middle = (start + end) / 2;
				PrintTask left = new PrintTask(start, middle);
				PrintTask right = new PrintTask(middle, end);
				// 并行执行两个“小任务”
				left.fork();
				right.fork();
			}
		}
	}

	// 继承RecursiveTask来实现"可分解"的任务
	class CalTask extends RecursiveTask<Integer> {
		private static final long serialVersionUID = 1L;
		// 每个“小任务”只最多只累加20个数
		private static final int THRESHOLD = 20;
		private int arr[];
		private int start;
		private int end;

		// 累加从start到end的数组元素
		public CalTask(int[] arr, int start, int end) {
			this.arr = arr;
			this.start = start;
			this.end = end;
		}

		@Override
		protected Integer compute() {
			int sum = 0;
			// 当end与start之间的差小于THRESHOLD时，开始进行实际累加
			if (end - start < THRESHOLD) {
				for (int i = start; i < end; i++) {
					sum += arr[i];
				}
				return sum;
			} else {
				// 如果当end与start之间的差大于THRESHOLD时，即要打印的数超过20个
				// 将大任务分解成两个小任务。
				int middle = (start + end) / 2;
				CalTask left = new CalTask(arr, start, middle);
				CalTask right = new CalTask(arr, middle, end);
				// 并行执行两个“小任务”
				left.fork();
				right.fork();
				// 把两个“小任务”累加的结果合并起来
				return left.join() + right.join();
			}
		}
	}

	@Test
	public void testForkJoinPool2() throws Exception {
		int[] arr = new int[100];
		Random rand = new Random();
		int total = 0;
		// 初始化100个数字元素
		for (int i = 0, len = arr.length; i < len; i++) {
			int tmp = rand.nextInt(20);
			// 对数组元素赋值，并将数组元素的值添加到total总和中。
			total += (arr[i] = tmp);
		}
		System.out.println(total);

		ForkJoinPool pool = new ForkJoinPool();
		// 提交可分解的CalTask任务
		Future<Integer> future = pool.submit(new CalTask(arr, 0, arr.length));
		System.out.println(future.get());
		// 关闭线程池
		pool.shutdown();
	}

	@Test
	public void testForkJoinPool() throws InterruptedException {
		ForkJoinPool pool = new ForkJoinPool();
		// 提交可分解的PrintTask任务
		pool.submit(new PrintTask(0, 50));
		// 线程阻塞，等待所有任务完成
		pool.awaitTermination(2, TimeUnit.SECONDS);
		// 关闭线程池
		pool.shutdown();
	}

}
