package sc.learn.test.jdk;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

public class TestCompletableFuture {
	
	@Test(expected=CompletionException.class)
	public void testJoin() throws InterruptedException, ExecutionException{
		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
		    int i = 1/0;
		    return i;
		});
		future.join();
		future.get();
	}
	
	@Test
	public void testComplete() throws IOException{
		final CompletableFuture<Integer> f = new CompletableFuture<>();
        class Client extends Thread {
            CompletableFuture<Integer> f;
            Client(String threadName, CompletableFuture<Integer> f) {
                super(threadName);
                this.f = f;
            }
            @Override
            public void run() {
                try {
                    System.out.println(this.getName() + ": " + f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        new Client("Client1", f).start();
        new Client("Client2", f).start();
        System.out.println("waiting");
        f.complete(100);
//        f.completeExceptionally(new Exception());
//        System.in.read();
	}
	
	@Test
	public void testWhenComplete() throws InterruptedException, ExecutionException{
		Random rand = new Random();
		long t = System.currentTimeMillis();
		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
			System.out.println("begin to start compute");
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {
	        }
	        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t)/1000 + " seconds");
	        return rand.nextInt(1000);
		});
        Future<Integer> f = future.whenCompleteAsync((v, e) -> {
            System.out.println(v);
            System.out.println(e);
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
			}
        });
        System.out.println(f.get());
	}
	
	@Test
	public void testExceptionally() throws InterruptedException, ExecutionException{
		int a=(int) CompletableFuture.supplyAsync(()->{
			throw new RuntimeException();
//			return 1;
		}).exceptionally((e)->0).get();
		
		System.out.println(a);
	}

}
