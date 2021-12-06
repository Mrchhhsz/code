package com.lmax.disruptor;

/**
 * 批处理事件处理通知（感知）
 * 如果{@link EventHandler}实现了该接口，那么当批处理开始时，会受到通知
 */
public interface BatchStartAware
{
    /**
     * 批处理开始
     * @param batchSize 本批次事件个数
     */
    void onBatchStart(long batchSize);
}
