package trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:56
 * @Package: trade
 */
public class Handler3 implements EventHandler<Trade>, WorkHandler<Trade> {
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        onEvent(event);
    }

    @Override
    public void onEvent(Trade event) throws Exception {
        long threadId = Thread.currentThread().getId();
        long id = event.getId();
        System.out.println(String.format("%s：Thread Id %s trade info %s Handling ....",
                this.getClass().getSimpleName(), threadId, id));
    }
}
