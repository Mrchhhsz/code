package cn.chh.immutable;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.SingleProducerSequencer;
import com.lmax.disruptor.YieldingWaitStrategy;

import java.util.concurrent.locks.LockSupport;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 8:58
 * @Package: cn.chh.immutable
 */
public class CustomPerformanceTest {

    private final CustomRingBuffer<SimpleEvent> ringBuffer;

    public CustomPerformanceTest() {
        ringBuffer = new CustomRingBuffer<SimpleEvent>(new SingleProducerSequencer(Constants.size, new YieldingWaitStrategy()));
    }

    public void run() {
        try {
            doRun();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doRun() throws InterruptedException {
        BatchEventProcessor<?> batchEventProcessor = ringBuffer.createHandler(new SimpleEventHandler());

        Thread t = new Thread(batchEventProcessor);
        long start = System.currentTimeMillis();
        t.start();

        long iterations = Constants.ITERATIONS;

        for(long l = 0; l < iterations; l++) {
            SimpleEvent e = new SimpleEvent(l, l, l, l);
            ringBuffer.put(e);
        }

        while(batchEventProcessor.getSequence().get() != iterations - 1) {
            LockSupport.parkNanos(1);
        }

        batchEventProcessor.halt();
        t.join();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static void main(String[] args) {
        new CustomPerformanceTest().run();
    }
}
