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

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import com.lmax.disruptor.util.Util;

/**
 * 序列号组，可以动态的线程安全的添加和移除多个序列号
 * A {@link Sequence} group that can dynamically have {@link Sequence}s added and removed while being
 * thread safe.
 * <p>
 *     get,set方法使用无锁，如果需要考虑并发可以调用add,remove方法
 * The {@link SequenceGroup#get()} and {@link SequenceGroup#set(long)} methods are lock free and can be
 * concurrently be called with the {@link SequenceGroup#add(Sequence)} and {@link SequenceGroup#remove(Sequence)}.
 */
public final class SequenceGroup extends Sequence
{
    private static final AtomicReferenceFieldUpdater<SequenceGroup, Sequence[]> SEQUENCE_UPDATER =
        AtomicReferenceFieldUpdater.newUpdater(SequenceGroup.class, Sequence[].class, "sequences");
    private volatile Sequence[] sequences = new Sequence[0];

    /**
     * Default Constructor
     */
    public SequenceGroup()
    {
        super(-1);
    }

    /**
     * 获取消费者组中最小消费序列号
     * Get the minimum sequence value for the group.
     *
     * @return the minimum sequence value for the group.
     */
    @Override
    public long get()
    {
        return Util.getMinimumSequence(sequences);
    }

    /**
     * Set all {@link Sequence}s in the group to a given value.
     *
     * @param value to set the group of sequences to.
     */
    @Override
    public void set(final long value)
    {
        final Sequence[] sequences = this.sequences;
        for (int i = 0, size = sequences.length; i < size; i++)
        {
            sequences[i].set(value);
        }
    }

    /**
     * 添加一个Sequence实例进序列组，使用自旋锁cas原子操作更新序列组，写时复制，线程安全
     * Add a {@link Sequence} into this aggregate.  This should only be used during
     * initialisation.  Use {@link SequenceGroup#addWhileRunning(Cursored, Sequence)}
     *
     * @param sequence to be added to the aggregate.
     * @see SequenceGroup#addWhileRunning(Cursored, Sequence)
     */
    public void add(final Sequence sequence)
    {
        Sequence[] oldSequences;
        Sequence[] newSequences;
        do
        {
            // todo 获取旧的序列组
            oldSequences = sequences;
            final int oldSize = oldSequences.length;
            // todo 创建新的序列分组，在旧的容量上+1
            newSequences = new Sequence[oldSize + 1];
            // todo 拷贝旧数组数据到新数组
            System.arraycopy(oldSequences, 0, newSequences, 0, oldSize);
            // todo 添加新的Sequence实例
            newSequences[oldSize] = sequence;
        }
        while (!SEQUENCE_UPDATER.compareAndSet(this, oldSequences, newSequences));
    }

    /**
     * Remove the first occurrence of the {@link Sequence} from this aggregate.
     * 从此序列组聚合中删除第一个sequence
     *
     * @param sequence to be removed from this aggregate.
     * @return true if the sequence was removed otherwise false.
     */
    public boolean remove(final Sequence sequence)
    {
        return SequenceGroups.removeSequence(this, SEQUENCE_UPDATER, sequence);
    }

    /**
     * Get the size of the group.
     *
     * @return the size of the group.
     */
    public int size()
    {
        return sequences.length;
    }

    /**
     * Adds a sequence to the sequence group after threads have started to publish to
     * the Disruptor.  It will set the sequences to cursor value of the ringBuffer
     * just after adding them.  This should prevent any nasty rewind/wrapping effects.
     *
     * @param cursored The data structure that the owner of this sequence group will
     *                 be pulling it's events from.
     * @param sequence The sequence to add.
     */
    public void addWhileRunning(Cursored cursored, Sequence sequence)
    {
        SequenceGroups.addSequences(this, SEQUENCE_UPDATER, cursored, sequence);
    }
}
