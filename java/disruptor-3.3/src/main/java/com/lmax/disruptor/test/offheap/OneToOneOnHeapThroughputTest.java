package com.lmax.disruptor.test.offheap;

import com.lmax.disruptor.*;
import com.lmax.disruptor.test.AbstractPerfTestDisruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class OneToOneOnHeapThroughputTest extends AbstractPerfTestDisruptor
{
    // 快大小
    private static final int BLOCK_SIZE = 256;
    // 缓冲数组长度
    private static final int BUFFER_SIZE = 1024 * 1024;
    // 数据量
    private static final long ITERATIONS = 1000 * 1000 * 10L;

    // 线程池 一个线程
    private final Executor executor = Executors.newFixedThreadPool(1, DaemonThreadFactory.INSTANCE);
    // 等待策略，使用Thread.yield
    private final WaitStrategy waitStrategy = new YieldingWaitStrategy();
    // 初始化一个环形缓冲区
    private final RingBuffer<ByteBuffer> buffer =
        RingBuffer.createSingleProducer(BufferFactory.direct(BLOCK_SIZE), BUFFER_SIZE, waitStrategy);
    private final ByteBufferHandler handler = new ByteBufferHandler();
    // 事件处理这
    private final BatchEventProcessor<ByteBuffer> processor =
        new BatchEventProcessor<ByteBuffer>(buffer, buffer.newBarrier(), handler);

    {
        buffer.addGatingSequences(processor.getSequence());
    }
    // 随机数
    private final Random r = new Random(1);
    // 字节数组
    private final byte[] data = new byte[BLOCK_SIZE];

    public OneToOneOnHeapThroughputTest()
    {
        r.nextBytes(data);
    }

    @Override
    protected int getRequiredProcessorCount()
    {
        return 2;
    }

    @Override
    protected long runDisruptorPass() throws Exception
    {
        byte[] data = this.data;

        final CountDownLatch latch = new CountDownLatch(1);
        long expectedCount = processor.getSequence().get() + ITERATIONS;
        handler.reset(latch, ITERATIONS);
        executor.execute(processor);
        long start = System.currentTimeMillis();

        final RingBuffer<ByteBuffer> rb = buffer;

        for (long i = 0; i < ITERATIONS; i++)
        {
            long next = rb.next();
            ByteBuffer event = rb.get(next);
            event.clear();
            event.put(data);
            event.flip();
            rb.publish(next);
        }

        latch.await();
        long opsPerSecond = (ITERATIONS * 1000L) / (System.currentTimeMillis() - start);
        waitForEventProcessorSequence(expectedCount);
        processor.halt();

        return opsPerSecond;
    }

    private void waitForEventProcessorSequence(long expectedCount)
    {
        while (processor.getSequence().get() < expectedCount)
        {
            LockSupport.parkNanos(1);
        }
    }

    public static void main(String[] args) throws Exception
    {
        new OneToOneOnHeapThroughputTest().testImplementations();
    }

    public static class ByteBufferHandler implements EventHandler<ByteBuffer>
    {
        private long total = 0;
        private long expectedCount;
        private CountDownLatch latch;

        @Override
        public void onEvent(ByteBuffer event, long sequence, boolean endOfBatch) throws Exception
        {
            for (int i = 0; i < BLOCK_SIZE; i += 8)
            {
                total += event.getLong();
            }

            if (--expectedCount == 0)
            {
                latch.countDown();
            }
        }

        public long getTotal()
        {
            return total;
        }

        public void reset(CountDownLatch latch, long expectedCount)
        {
            this.latch = latch;
            this.expectedCount = expectedCount;
        }
    }

    private static final class BufferFactory implements EventFactory<ByteBuffer>
    {
        private final boolean isDirect;
        private final int size;

        private BufferFactory(boolean isDirect, int size)
        {
            this.isDirect = isDirect;
            this.size = size;
        }

        @Override
        public ByteBuffer newInstance()
        {
            if (isDirect)
            {
                return ByteBuffer.allocateDirect(size);
            }
            else
            {
                return ByteBuffer.allocate(size);
            }
        }

        public static BufferFactory direct(int size)
        {
            return new BufferFactory(true, size);
        }

        @SuppressWarnings("unused")
        public static BufferFactory heap(int size)
        {
            return new BufferFactory(false, size);
        }
    }
}
