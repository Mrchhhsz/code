package com.lmax.disruptor;

/**
 * 该接口是面向生产者的，生产者通过该接口获取队列容量，和可用空间，并在填充数据之后，发布对应的sequence(这些sequence对应的数据已可消费)
 * 使用二阶段提交，先申请空间，再填充数据，多个生产者之间使用自旋锁来竞争获取序列号，性能考虑
 * 申请空间之后，必须使用对应的发布方法，发布数据
 * 否则会因为数据结构出现断层，从而导致整个数据结构不可用
 */
public interface Sequenced
{
    /**
     * 数据结构大小
     * The capacity of the data structure to hold entries.
     *
     * @return the size of the RingBuffer.
     */
    int getBufferSize();

    /**
     * 是否有足够的空间
     * Has the buffer got capacity to allocate another sequence.  This is a concurrent
     * method so the response should only be taken as an indication of available capacity.
     *
     * @param requiredCapacity in the buffer
     * @return true if the buffer has the capacity to allocate the next sequence otherwise false.
     */
    boolean hasAvailableCapacity(int requiredCapacity);

    /**
     * 当前剩余容量，并不一定最新值，因为多线程模型下查询容量当前大小，它反映的总是一个旧值
     * Get the remaining capacity for this sequencer.
     *
     * @return The number of slots remaining.
     */
    long remainingCapacity();

    /**
     * 获取下一个数据的索引，空间不足会阻塞会阻塞
     * 因为是一个环形队列，当生产者生产的实际数组下标追上消费者消费的下标时，不能去覆盖原来还未消费的数据，所以需要
     * {@link WaitStrategy}等待策略来等待消费者先消费数据
     * 申请完空间之后，必须使用publish发布，否则会导致整个数据结构不可用
     *
     * 一旦进入该方法，除非有空间，否则无法退出，连中断都没有检查，即使消费者已经停止运行了，生产者也无法退出，可能导致死锁
     * Claim the next event in sequence for publishing.
     *
     * @return the claimed sequence value
     */
    long next();

    /**
     * Claim the next n events in sequence for publishing.  This is for batch event producing.  Using batch producing
     * requires a little care and some math.
     * <pre>
     * int n = 10;
     * long hi = sequencer.next(n);
     * long lo = hi - (n - 1);
     * for (long sequence = lo; sequence &lt;= hi; sequence++) {
     *     // Do work.
     * }
     * sequencer.publish(lo, hi);
     * </pre>
     *
     * @param n the number of sequences to claim
     * @return the highest claimed sequence value
     */
    long next(int n);

    /**
     * Attempt to claim the next event in sequence for publishing.  Will return the
     * number of the slot if there is at least <code>requiredCapacity</code> slots
     * available.
     *
     * @return the claimed sequence value
     * @throws InsufficientCapacityException thrown if there is no space available in the ring buffer.
     */
    long tryNext() throws InsufficientCapacityException;

    /**
     * Attempt to claim the next n events in sequence for publishing.  Will return the
     * highest numbered slot if there is at least <code>requiredCapacity</code> slots
     * available.  Have a look at {@link Sequencer#next()} for a description on how to
     * use this method.
     *
     * @param n the number of sequences to claim
     * @return the claimed sequence value
     * @throws InsufficientCapacityException thrown if there is no space available in the ring buffer.
     */
    long tryNext(int n) throws InsufficientCapacityException;

    /**
     * 发布指定序号的数据，表示sequence对应的数据可用
     * Publishes a sequence. Call when the event has been filled.
     *
     * @param sequence the sequence to be published.
     */
    void publish(long sequence);

    /**
     * 批量发布数据，表示lowest,highest区间整段数据可用了
     * Batch publish sequences.  Called when all of the events have been filled.
     *
     * @param lo first sequence number to publish
     * @param hi last sequence number to publish
     */
    void publish(long lo, long hi);
}