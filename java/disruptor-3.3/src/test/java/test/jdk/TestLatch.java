package test.jdk;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 11:25
 * @Package: test.jdk
 */
public class TestLatch {

    public static void main(String[] args) throws InterruptedException {
        final int NUM_THREAD = 2;
        final CountDownLatch latch = new CountDownLatch(2);
        for(int i = 0; i < NUM_THREAD; i++) {
            new Thread(() -> {

                try {
                    System.out.println(Thread.currentThread().getName() + " is executing");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(Thread.currentThread().getName() + " is completing");
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "thread-" + i).start();
        }

        System.out.println("waiting");
        latch.await();
        System.out.println("completing");
        System.out.println("main");

    }
}
