package trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.util.PaddedLong;

import java.util.concurrent.CountDownLatch;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:42
 * @Package: trade
 */
public class Handler1 implements EventHandler<Trade> {

    private final PaddedLong value = new PaddedLong();
    private long count;
    private CountDownLatch latch;

    public void reset(final CountDownLatch latch, final long expectedCount) {
        value.set(0L);
        this.latch = latch;
        count = expectedCount;
    }

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        event.setId(sequence);
        event.setName("trade" + sequence);
        System.out.println(event.getId());
        if(--count == 0) {
            latch.countDown();
        }
    }
}
