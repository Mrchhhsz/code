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

/**
 * 事件处理器
 * 一个事件处理器实现Runnable接口，在轮询时使用适当的等待策略{@link WaitStrategy}从{@link RingBuffer}中轮询获取可用事件
 * 最好不实现该接口而是优先使用{@link EventHandler}接口和{@link BatchEventProcessor}
 *
 * ◇ 事件处理器是最小的事件处理单元，继承Runnable接口，是一个无限执行的任务（轮询监听），在运行时不会让出线程，因此每一个事件处理器需要独立的线程
 * ◇ 一个消费者中可能只有一个事件处理器{@link BatchEventProcessor},也可能有多个事件处理器
 * ◇ {@link BatchEventProcessor} 既是事件处理器，也是消费者 {@link WorkProcessor}是事件处理器，但不是消费者，WorkPool才是消费者
 * ◇ 管理消费者，其实也是管理消费者们的所有事件处理器
 * ◇ 每一个事件处理器都有自己独立的{@link Sequence} ,如果多个事件处理器协作的话，这些处理器之间会进行同步
 * EventProcessors waitFor events to become available for consumption from the {@link RingBuffer}
 * <p>
 * An EventProcessor will generally be associated with a Thread for execution.
 */
public interface EventProcessor extends Runnable
{
    /**
     * Get a reference to the {@link Sequence} being used by this {@link EventProcessor}.
     *  获取正在被用的元素所占的序列，每一个事件处理器有自己独立的序列
     * @return reference to the {@link Sequence} for this {@link EventProcessor}
     */
    Sequence getSequence();

    /**
     * 通知事件处理器在完成本次消费之后，暂停下来，协作指令
     * Signal that this EventProcessor should stop when it has finished consuming at the next clean break.
     * It will call {@link SequenceBarrier#alert()} to notify the thread to check status.
     */
    void halt();

    /**
     * 查询事件处理器是否运行中
     * @return
     */
    boolean isRunning();
}
