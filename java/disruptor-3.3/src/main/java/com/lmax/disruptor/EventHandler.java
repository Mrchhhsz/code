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
 * 批事件处理器的时间处理回调接口
 * Callback interface to be implemented for processing events as they become available in the {@link RingBuffer}
 *
 * @param <T> event implementation storing the data for sharing during exchange or parallel coordination of an event.
 * @see BatchEventProcessor#setExceptionHandler(ExceptionHandler) if you want to handle exceptions propagated out of the handler.
 */
public interface EventHandler<T>
{
    /**
     * 如果在处理事件时抛出异常，而没有指定{@link ExceptionHandler}时，会导致BatchEventProcessor停止工作，可能导致死锁
     * 系统默认的异常处理{@link FatalExceptionHandler}会将异常包装为RuntimeException重新抛出，直接退出循环，会导致死锁
     *
     * 这样做的好处是，你可以降低一些操作的消耗，可以攒到批量数据的结尾时进行一次操作
     * 如IO操作，对写复制容器的操作（写入时尽量将多次写入合并为一次写入）
     * Called when a publisher has published an event to the {@link RingBuffer}
     *
     * @param event      published to the {@link RingBuffer} 序号对应的事件数据
     * @param sequence   of the event being processed 序号
     * @param endOfBatch flag to indicate if this is the last event in a batch from the {@link RingBuffer} 是否是本次批处理的最后一个
     * @throws Exception if the EventHandler would like the exception handled further up the chain.
     */
    void onEvent(T event, long sequence, boolean endOfBatch) throws Exception;
}
