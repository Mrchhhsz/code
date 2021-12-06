package com.lmax.disruptor;

/**
 * Experimental poll-based interface for the Disruptor.
 * 事件轮询器
 */
public class EventPoller<T>
{
    // todo 数据提供者
    private final DataProvider<T> dataProvider;
    // todo 序号生成器
    private final Sequencer sequencer;
    // todo 消费序号
    private final Sequence sequence;
    // todo 依赖的序号，sequence必须小于gatingSequence
    private final Sequence gatingSequence;

    public interface Handler<T>
    {
        boolean onEvent(T event, long sequence, boolean endOfBatch) throws Exception;
    }

    public enum PollState
    {
        PROCESSING, GATING, IDLE
    }

    public EventPoller(
        final DataProvider<T> dataProvider,
        final Sequencer sequencer,
        final Sequence sequence,
        final Sequence gatingSequence)
    {
        this.dataProvider = dataProvider;
        this.sequencer = sequencer;
        this.sequence = sequence;
        this.gatingSequence = gatingSequence;
    }

    /**
     * 使用{@link EventHandler}的子类来做事件通知
     * @param eventHandler
     * @return
     * @throws Exception
     */
    public PollState poll(final Handler<T> eventHandler) throws Exception
    {
        // 获取当前消费的序列号
        final long currentSequence = sequence.get();
        // 下一个位置'
        long nextSequence = currentSequence + 1;
        final long availableSequence = sequencer.getHighestPublishedSequence(nextSequence, gatingSequence.get());
        // todo 如果需要分配的下一个序列号小于等于可用的序列号，则表示可以消费数据
        if (nextSequence <= availableSequence)
        {
            boolean processNextEvent;
            long processedSequence = currentSequence;

            try
            {
                do
                {
                    //
                    final T event = dataProvider.get(nextSequence);
                    processNextEvent = eventHandler.onEvent(event, nextSequence, nextSequence == availableSequence);
                    processedSequence = nextSequence;
                    nextSequence++;

                }
                while (nextSequence <= availableSequence & processNextEvent);
            }
            finally
            {
                sequence.set(processedSequence);
            }

            return PollState.PROCESSING;
        }
        else if (sequencer.getCursor() >= nextSequence)
        {
            return PollState.GATING;
        }
        else
        {
            return PollState.IDLE;
        }
    }

    public static <T> EventPoller<T> newInstance(
        final DataProvider<T> dataProvider,
        final Sequencer sequencer,
        final Sequence sequence,
        final Sequence cursorSequence,
        final Sequence... gatingSequences)
    {
        Sequence gatingSequence;
        if (gatingSequences.length == 0)
        {
            gatingSequence = cursorSequence;
        }
        else if (gatingSequences.length == 1)
        {
            gatingSequence = gatingSequences[0];
        }
        else
        {
            gatingSequence = new FixedSequenceGroup(gatingSequences);
        }

        return new EventPoller<T>(dataProvider, sequencer, sequence, gatingSequence);
    }

    public Sequence getSequence()
    {
        return sequence;
    }
}
