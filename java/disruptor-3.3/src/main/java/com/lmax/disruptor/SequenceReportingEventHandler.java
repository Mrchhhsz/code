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
 * Sequence提交，用于获取{@link BatchEventProcessor} 绑定的{@link Sequence}
 * 用于{@link BatchEventProcessor} 设置一个回调，允许EventHandler在完成事件消费之后进行通知
 *
 * 通常，这将在handler执行某些批处理操作时使用；
 * 在操作完成后，接口的实现者应该调用{@link Sequence#set}来更新序列，并允许依赖于此处理程序的其他进程进行
 *
 * Used by the {@link BatchEventProcessor} to set a callback allowing the {@link EventHandler} to notify
 * when it has finished consuming an event if this happens after the {@link EventHandler#onEvent(Object, long, boolean)} call.
 * <p>
 * Typically this would be used when the handler is performing some sort of batching operation such as writing to an IO
 * device; after the operation has completed, the implementation should call {@link Sequence#set} to update the
 * sequence and allow other processes that are dependent on this handler to progress.
 *
 * @param <T> event implementation storing the data for sharing during exchange or parallel coordination of an event.
 */
public interface SequenceReportingEventHandler<T>
    extends EventHandler<T>
{
    /**
     * 用于{@link BatchEventProcessor}建立回调机制
     * Call by the {@link BatchEventProcessor} to setup the callback.
     *
     * @param sequenceCallback callback on which to notify the {@link BatchEventProcessor} that the sequence has progressed.
     */
    void setSequenceCallback(Sequence sequenceCallback);
}
