package trade;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:47
 * @Package: trade
 */
public class TradePublisher implements Runnable {

    Disruptor<Trade> disruptor;
    private CountDownLatch latch;

    private static int LOOP = 60;

    public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.disruptor = disruptor;
        this.latch = latch;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for(int i = 0; i < LOOP; i++){
            disruptor.publishEvent(tradeEventTranslator);
        }
        latch.countDown();
    }
}

class TradeEventTranslator implements EventTranslator<Trade> {

    @Override
    public void translateTo(Trade event, long sequence) {
       // event.setId(UUID.randomUUID().toString());
    }
}
