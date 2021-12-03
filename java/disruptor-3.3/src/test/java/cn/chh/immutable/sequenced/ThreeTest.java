package cn.chh.immutable.sequenced;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.test.support.LongArrayEventHandler;
import com.lmax.disruptor.test.support.LongArrayPublisher;
import com.lmax.disruptor.test.support.MultiBufferBatchEventProcessor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.*;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 10:06
 * @Package: cn.chh.immutable.sequenced
 */
public class ThreeTest extends TestDisruptor {

    private static final int NUM_PUBLISHERS = 3;
    private static final int ARRAY_SIZE = 3;

    private static final int BUFFER_SIZE = 1024 * 64;
    private static final long ITERATIONS = 1000L * 1000L * 180L;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(NUM_PUBLISHERS + 1, DaemonThreadFactory.INSTANCE);

    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_PUBLISHERS + 1);

    private final RingBuffer<long[]>[] buffers = new RingBuffer[NUM_PUBLISHERS];
    private final SequenceBarrier[] barriers = new SequenceBarrier[NUM_PUBLISHERS];
    private final LongArrayPublisher[] valuePublishers = new LongArrayPublisher[NUM_PUBLISHERS];

    private final LongArrayEventHandler handler = new LongArrayEventHandler();
    private final MultiBufferBatchEventProcessor<long[]> batchEventProcessor;

    private static final EventFactory<long[]> FACTORY = new EventFactory<long[]>() {
        @Override
        public long[] newInstance() {
            return new long[ARRAY_SIZE];
        }
    };

    {
        for(int i = 0; i < NUM_PUBLISHERS; i++) {
            buffers[i] = RingBuffer.createSingleProducer(FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
            barriers[i] = buffers[i].newBarrier();
            valuePublishers[i] = new LongArrayPublisher(
                    cyclicBarrier,
                    buffers[i],
                    ITERATIONS / NUM_PUBLISHERS,
                    ARRAY_SIZE
            );
        }
        batchEventProcessor = new MultiBufferBatchEventProcessor<long[]>(buffers, barriers, handler);

        for(int i = 0; i < NUM_PUBLISHERS; i++) {
            buffers[i].addGatingSequences(batchEventProcessor.getSequences()[i]);
        }
    }

    protected int getRequiredProcessorCount(){
        return 4;
    }

    protected long runDisruptorPass() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        handler.reset(latch, ITERATIONS);

        Future<?>[] futures = new Future[NUM_PUBLISHERS];
        for(int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i] = executor.submit(valuePublishers[i]);
        }
        executor.submit(batchEventProcessor);

        long start = System.currentTimeMillis();
        cyclicBarrier.await();
        for(int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i].get();
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L * ARRAY_SIZE) / (System.currentTimeMillis() - start);
        batchEventProcessor.halt();
        return opsPerSecond;
    }

    public static void main(String[] args) throws Exception {
        new ThreeTest().testImplementations();
    }


}
