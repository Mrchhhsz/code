package cn.chh.immutable.sequenced;

/**
 * @Description: TODO
 * @Author: chh
 * @Date: 2021/1/28 10:26
 * @Package: cn.chh.immutable.sequenced
 */
public abstract class TestDisruptor {

    public static final int RUNS = 7;

    protected void testImplementations() throws Exception {

        long[] disruptorOps = new long[RUNS];

        System.out.println("Starting Disruptor tests");

        for(int i = 0; i < RUNS; i++) {
            System.gc();
            disruptorOps[i] = runDisruptorPass();
            System.out.format("Run %d, Disruptor=%,d ops/sec%n", i, Long.valueOf(disruptorOps[i]));
        }
    }

    protected abstract long runDisruptorPass() throws Exception;
}
