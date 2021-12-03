package trade;

import com.lmax.disruptor.EventHandler;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:45
 * @Package: trade
 */
public class Handler2 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        long threadId = Thread.currentThread().getId();

        long id = event.getId();
        System.out.println(String.format("%s：Thread Id %s trade info %s sending ....",
                this.getClass().getSimpleName(), threadId, id));
    }
}
