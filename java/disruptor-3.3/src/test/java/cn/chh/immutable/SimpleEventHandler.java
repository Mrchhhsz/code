package cn.chh.immutable;

import com.lmax.disruptor.EventHandler;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 9:04
 * @Package: cn.chh.immutable
 */
public class SimpleEventHandler implements EventHandler<SimpleEvent> {

    public long counter;

    @Override
    public void onEvent(SimpleEvent event, long sequence, boolean endOfBatch) throws Exception {
        counter += event.getCounter();
    }
}
