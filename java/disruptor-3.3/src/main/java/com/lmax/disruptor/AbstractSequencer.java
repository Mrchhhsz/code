/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.lmax.disruptor.util.Util;

/**
 * 抽象序号生成器，作为单生产者和多生产者序列号生成器的超类，实现一些公共的功能（添加删除gatingSequence）
 * Base class for the various sequencer types (single/multi).  Provides
 * common functionality like the management of gating sequences (add/remove) and
 * ownership of the current cursor.
 */
public abstract class AbstractSequencer implements Sequencer
{
    /**
     * 原子方式更新追踪的Sequence
     */
    private static final AtomicReferenceFieldUpdater<AbstractSequencer, Sequence[]> SEQUENCE_UPDATER =
        AtomicReferenceFieldUpdater.newUpdater(AbstractSequencer.class, Sequence[].class, "gatingSequences");

    /**
     * 序列生成器缓冲区大小（ringBuffer有效数据缓冲区大小）
     */
    protected final int bufferSize;
    /**
     * 消费者的等待策略。
     * SequenceBarrier由Sequencer创建，Barrier需要生产者的Sequence信息
     */
    protected final WaitStrategy waitStrategy;
    /**
     * 生产者的序列，表示生产者的进度。
     * 代码里面的带cursor的都表示生产者们的Sequence
     *
     * 消费者和生产者之间的交互是通过volatile变量的读写来保证的
     * 消费者们观察生产者的进度，当看见生产者进度增大时，生产者这期间的操作对消费者来说都是可见的
     * volatile的happens-before原则，生产者的进度变大（写volatile）先于消费者看见它变大
     * 在多生产者情况下，只能看见空间分配操作，要确定哪些数据发布还需要额外保证
     */
    protected final Sequence cursor = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
    /**
     * 序号生成器都必须和这些Sequence满足的约束；
     * cursor-bufferSize <= Min(gatingSequence)
     * 即所有的gatingSequence让出下一个插槽后，生成者才能获取该插槽
     *
     * 对于生产者来讲，他只需要关注消费链最末端的消费者的进度，
     * gatingSequences就是所有消费链最末端的消费者们所拥有的Sequence
     */
    protected volatile Sequence[] gatingSequences = new Sequence[0];

    /**
     * Create with the specified buffer size and wait strategy.
     * 使用指定的缓冲区大小和等待策略创建Sequencer
     *
     * @param bufferSize   The total number of entries, must be a positive power of 2.
     * @param waitStrategy The wait strategy used by this sequencer
     */
    public AbstractSequencer(int bufferSize, WaitStrategy waitStrategy)
    {
        if (bufferSize < 1)
        {
            throw new IllegalArgumentException("bufferSize must not be less than 1");
        }
        if (Integer.bitCount(bufferSize) != 1)
        {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.bufferSize = bufferSize;
        this.waitStrategy = waitStrategy;
    }

    /**
     * @see Sequencer#getCursor()
     * 获取当前游标（生产者的生产进度，已发布的最大序号）
     */
    @Override
    public final long getCursor()
    {
        return cursor.get();
    }

    /**
     * 获取缓冲区大小
     * @see Sequencer#getBufferSize()
     */
    @Override
    public final int getBufferSize()
    {
        return bufferSize;
    }

    /**
     * 添加gatingSequence（末端消费者的消费进度）
     * @see Sequencer#addGatingSequences(Sequence...)
     */
    @Override
    public final void addGatingSequences(Sequence... gatingSequences)
    {
        SequenceGroups.addSequences(this, SEQUENCE_UPDATER, this, gatingSequences);
    }

    /**
     * 移除消费者的进度信息
     * @see Sequencer#removeGatingSequence(Sequence)
     */
    @Override
    public boolean removeGatingSequence(Sequence sequence)
    {
        return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, sequence);
    }

    /**
     * 最末端的消费Sequence
     * @see Sequencer#getMinimumSequence()
     */
    @Override
    public long getMinimumSequence()
    {
        return Util.getMinimumSequence(gatingSequences, cursor.get());
    }

    /**
     * 创建屏障，协调生产者和消费者之间的进度
     * @see Sequencer#newBarrier(Sequence...)
     */
    @Override
    public SequenceBarrier newBarrier(Sequence... sequencesToTrack)
    {
        return new ProcessingSequenceBarrier(this, waitStrategy, cursor, sequencesToTrack);
    }

    /**
     * 创建一个事件轮询器
     * Creates an event poller for this sequence that will use the supplied data provider and
     * gating sequences.
     *
     * @param dataProvider    The data source for users of this event poller
     * @param gatingSequences Sequence to be gated on.
     * @return A poller that will gate on this ring buffer and the supplied sequences.
     */
    @Override
    public <T> EventPoller<T> newPoller(DataProvider<T> dataProvider, Sequence... gatingSequences)
    {
        return EventPoller.newInstance(dataProvider, this, new Sequence(), cursor, gatingSequences);
    }

    @Override
    public String toString()
    {
        return "AbstractSequencer{" +
            "waitStrategy=" + waitStrategy +
            ", cursor=" + cursor +
            ", gatingSequences=" + Arrays.toString(gatingSequences) +
            '}';
    }
}