package trade;

import cn.chh.immutable.sequenced.TestDisruptor;
import cn.chh.immutable.sequenced.ThreeTest;
import com.lmax.disruptor.*;
import com.lmax.disruptor.test.support.MultiBufferBatchEventProcessor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.util.concurrent.*;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 9:39
 * @Package: trade
 */
public class Test extends TestDisruptor {

    // 生产者数量
    private static final int NUM_PUBLISHERS = 1;
    private static final int BUFFER_SIZE = 2;
    private static final long ITERATIONS = 60;

    // 线程池
    private final ExecutorService executor =
            Executors.newFixedThreadPool(NUM_PUBLISHERS + 1, DaemonThreadFactory.INSTANCE);
    // 测试速度的栅栏
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(NUM_PUBLISHERS + 1);
    // 环形缓冲队列，队列元素类型是Trade
    private final RingBuffer<Trade> buffers;
    // 栅栏，当生产者在队列中的索引追上消费者索引，需要拦住生产者
    private final SequenceBarrier barriers;
    private final TestTradePublisher valuePublishers;
    private final Handler1 handler1 = new Handler1();
    private final BatchEventProcessor<Trade> batchEventProcessor;

    // 元素生产工厂
    private static final EventFactory<Trade> FACTORY = new EventFactory<Trade>() {
        @Override
        public Trade newInstance() {
            return new Trade();
        }
    };

    {
        // 配置环形队列的元素生产工厂，队列大小，等待策略
        buffers = RingBuffer.createSingleProducer(FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
        barriers = buffers.newBarrier();
        valuePublishers = new TestTradePublisher(cyclicBarrier, buffers, ITERATIONS);
        /*for(int i = 0; i < NUM_PUBLISHERS; i++) {
            buffers[i] = RingBuffer.createSingleProducer(FACTORY, BUFFER_SIZE, new YieldingWaitStrategy());
            barriers[i] = buffers[i].newBarrier();

            valuePublishers[i] = new TestTradePublisher(cyclicBarrier, buffers, ITERATIONS);
        }*/
        batchEventProcessor = new BatchEventProcessor<>(buffers, barriers, handler1);
        buffers.addGatingSequences(batchEventProcessor.getSequence());
        /*for(int i = 0; i < NUM_PUBLISHERS; i++) {
            buffers[i].addGatingSequences(batchEventProcessor.getSequences()[i]);
        }*/
    }


    @Override
    protected long runDisruptorPass() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        handler1.reset(latch, ITERATIONS);

        //Future<?>[] futures = new Future[NUM_PUBLISHERS];
       // for(int i = 0; i < NUM_PUBLISHERS; i++) {
            Future<?> future = executor.submit(valuePublishers);
       // }
        executor.submit(batchEventProcessor);
        long start = System.currentTimeMillis();
        cyclicBarrier.await();
        future.get();
        /*for(int i = 0; i < NUM_PUBLISHERS; i++) {
            futures[i].get();
        }*/

        latch.await();
  //      long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        batchEventProcessor.halt();
//        return opsPerSecond;
        return 0;
    }

    public static void main(String[] args) throws Exception {
        new Test().testImplementations();
    }

}
