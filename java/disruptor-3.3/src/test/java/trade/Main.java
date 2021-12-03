package trade;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/29 8:51
 * @Package: trade
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        int bufferSize = 16;
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        Disruptor<Trade> disruptor = new Disruptor<Trade>(new EventFactory<Trade>() {
            @Override
            public Trade newInstance() {
                return new Trade();
            }
        }, bufferSize, executor, ProducerType.SINGLE, new BusySpinWaitStrategy());

        EventHandlerGroup<Trade> handlerGroup = disruptor.handleEventsWith(new Handler1(), new Handler2());

        handlerGroup.then(new Handler3());

        disruptor.start();
        CountDownLatch latch = new CountDownLatch(1);
        executor.submit(new TradePublisher(latch, disruptor));
        latch.await();
        disruptor.shutdown();
        executor.shutdown();
        System.out.println(System.currentTimeMillis() - beginTime);
    }
}
