package test.jdk;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 11:42
 * @Package: test.jdk
 */
public class TestBarrier {

    final static int NUM_THREAD = 2;

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREAD, () -> {
            System.out.println("executing self");
        });

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try {
                System.out.println("thread-1 first");
                TimeUnit.SECONDS.sleep(2);
                barrier.await();
                System.out.println("thread-1 second");
                TimeUnit.SECONDS.sleep(2);
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executor.submit(() -> {
            try {
                System.out.println("thread-2 first");
                TimeUnit.SECONDS.sleep(2);
                barrier.await();
                System.out.println("thread-2 second");
                TimeUnit.SECONDS.sleep(2);
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();

    }
}
