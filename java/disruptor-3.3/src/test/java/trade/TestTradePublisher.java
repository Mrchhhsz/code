package trade;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 9:55
 * @Package: trade
 */
public class TestTradePublisher implements Runnable {

    private final CyclicBarrier cyclicBarrier;
    private final RingBuffer<Trade> ringBuffer;
    private final long iterations;

    public TestTradePublisher(
            final CyclicBarrier cyclicBarrier,
            final RingBuffer<Trade> ringBuffer,
            final long iterations
    ) {
        this.cyclicBarrier = cyclicBarrier;
        this.ringBuffer = ringBuffer;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        try {
            cyclicBarrier.await();
            for(long i = 0; i < iterations; i++) {
                long sequence = ringBuffer.next();
                Trade event = ringBuffer.get(sequence);
                event.setId(i);
                ringBuffer.publish(sequence);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }
}
