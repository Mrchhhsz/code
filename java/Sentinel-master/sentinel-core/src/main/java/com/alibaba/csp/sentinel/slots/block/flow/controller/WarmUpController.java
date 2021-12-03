/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.slots.block.flow.controller;

import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.csp.sentinel.util.TimeUtil;
import com.alibaba.csp.sentinel.node.Node;
import com.alibaba.csp.sentinel.slots.block.flow.TrafficShapingController;

/**
 * <p>
 * The principle idea comes from Guava. However, the calculation of Guava is
 * rate-based, which means that we need to translate rate to QPS.
 * </p>
 *
 * <p>
 * Requests arriving at the pulse may drag down long idle systems even though it
 * has a much larger handling capability in stable period. It usually happens in
 * scenarios that require extra time for initialization, e.g. DB establishes a connection,
 * connects to a remote service, and so on. That’s why we need “warm up”.
 * </p>
 *
 * <p>
 * Sentinel's "warm-up" implementation is based on the Guava's algorithm.
 * However, Guava’s implementation focuses on adjusting the request interval,
 * which is similar to leaky bucket. Sentinel pays more attention to
 * controlling the count of incoming requests per second without calculating its interval,
 * which resembles token bucket algorithm.
 * </p>
 *
 * <p>
 * The remaining tokens in the bucket is used to measure the system utility.
 * Suppose a system can handle b requests per second. Every second b tokens will
 * be added into the bucket until the bucket is full. And when system processes
 * a request, it takes a token from the bucket. The more tokens left in the
 * bucket, the lower the utilization of the system; when the token in the token
 * bucket is above a certain threshold, we call it in a "saturation" state.
 * </p>
 *
 * <p>
 * Base on Guava’s theory, there is a linear equation we can write this in the
 * form y = m * x + b where y (a.k.a y(x)), or qps(q)), is our expected QPS
 * given a saturated period (e.g. 3 minutes in), m is the rate of change from
 * our cold (minimum) rate to our stable (maximum) rate, x (or q) is the
 * occupied token.
 * </p>
 *
 * @author jialiang.linjl
 * 当配置每秒能通过多少请求后，那么在这里 sentinel 也会每秒往桶内添加多少的令牌。当一个请求进入的时候，将会从中移除一个令牌。
 * 由此可以得出，桶内的令牌越多，也说明当前的系统利用率越低。因此，当桶内的令牌数量超过某个阈值后，那么当前的系统可以称之为处于饱和状态。
 * 当系统处于 饱和状态的时候，当前允许的最大 qps 将会随着剩余的令牌数量减少而缓慢增加，达到为系统预热热身的目的
 *
 * 预热的目的：当长时间没有请求的时候，突然有请求到来，系统需要额外的时间初始化，比如数据库连接的建立和远程服务的连接
 */
public class WarmUpController implements TrafficShapingController {

    // 当前QPS的阈值
    protected double count;
    // 冷却因子
    private int coldFactor;
    // 警戒的令牌数量
    protected int warningToken = 0;
    // 最大令牌数量
    private int maxToken;
    // 斜率
    protected double slope;

    protected AtomicLong storedTokens = new AtomicLong(0);
    protected AtomicLong lastFilledTime = new AtomicLong(0);

    /**
     *
     * @param count 每秒生成多少个令牌
     * @param warmUpPeriodInSec 热身时间，使用秒作为单位
     * @param coldFactor 冷却因子
     */
    public WarmUpController(double count, int warmUpPeriodInSec, int coldFactor) {
        construct(count, warmUpPeriodInSec, coldFactor);
    }

    public WarmUpController(double count, int warmUpPeriodInSec) {
        construct(count, warmUpPeriodInSec, 3);
    }

    private void construct(double count, int warmUpPeriodInSec, int coldFactor) {

        if (coldFactor <= 1) {
            throw new IllegalArgumentException("Cold factor should be larger than 1");
        }

        this.count = count;

        this.coldFactor = coldFactor;

        // thresholdPermits = 0.5 * warmupPeriod / stableInterval.
        // warningToken = 100;
        // 警戒令牌数 = （热身时间长度 * 每秒令牌的数量）/ (冷却因子 - 1)
        warningToken = (int)(warmUpPeriodInSec * count) / (coldFactor - 1);
        // / maxPermits = thresholdPermits + 2 * warmupPeriod /
        // (stableInterval + coldInterval)
        // maxToken = 200
        // 最大令牌数量 = 警戒令牌数 * (2 * 热身时长 * 每秒令牌数 / (1 + 冷却因子))
        maxToken = warningToken + (int)(2 * warmUpPeriodInSec * count / (1.0 + coldFactor));

        // slope
        // slope = (coldIntervalMicros - stableIntervalMicros) / (maxPermits
        // - thresholdPermits);
        // 系统预热速率
        slope = (coldFactor - 1.0) / count / (maxToken - warningToken);

    }

    @Override
    public boolean canPass(Node node, int acquireCount) {
        return canPass(node, acquireCount, false);
    }

    /**
     * 请求是否可以通过
     * 举个例子: count = 3， coldFactor = 3，热身时间为 4 的时候，警戒令牌数为 6，最大令牌数为 12，
     * 当剩余令牌处于 6 和 12 之间的时候，其 slope 斜率为 1 / 9。 那么当剩余令牌数为 9 的时候的允许 qps 为 1.5。
     * 其 qps 将会随着剩余令牌数的不断减少而直到增加到 count 的值
     * @param node resource node
     * @param acquireCount count to acquire
     * @param prioritized whether the request is prioritized
     * @return
     */
    @Override
    public boolean canPass(Node node, int acquireCount, boolean prioritized) {
        // 通过的请求数
        long passQps = (long) node.passQps();
        // 上一个窗口的通过请求数
        long previousQps = (long) node.previousPassQps();
        // 首先重新计算其桶内剩余的数量
        syncToken(previousQps);

        // 开始计算它的斜率
        // 如果进入了警戒线，开始调整他的qps
        // 剩余令牌数
        long restToken = storedTokens.get();
        if (restToken >= warningToken) {
            // 相差令牌数
            long aboveToken = restToken - warningToken;
            // 消耗的速度要比warning快，但是要比慢
            // current interval = restToken*slope+1/count
            double warningQps = Math.nextUp(1.0 / (aboveToken * slope + 1.0 / count));
            if (passQps + acquireCount <= warningQps) {
                return true;
            }
        } else {
            if (passQps + acquireCount <= count) {
                return true;
            }
        }

        return false;
    }

    protected void syncToken(long passQps) {
        long currentTime = TimeUtil.currentTimeMillis();
        // 将当前时间向下取秒
        currentTime = currentTime - currentTime % 1000;
        // 最后一次
        long oldLastFillTime = lastFilledTime.get();
        // 不超过一秒
        if (currentTime <= oldLastFillTime) {
            return;
        }

        // 桶内令牌数
        long oldValue = storedTokens.get();
        // 放入令牌之后的桶内令牌数
        long newValue = coolDownTokens(currentTime, passQps);

        if (storedTokens.compareAndSet(oldValue, newValue)) {
            // 从桶内移除相应请求数量的令牌
            long currentValue = storedTokens.addAndGet(0 - passQps);
            // 不能为负数，最少为0
            if (currentValue < 0) {
                storedTokens.set(0L);
            }
            // 重置最后一次放令牌时间
            lastFilledTime.set(currentTime);
        }

    }

    private long coolDownTokens(long currentTime, long passQps) {
        long oldValue = storedTokens.get();
        long newValue = oldValue;

        // 添加令牌的判断前提条件:
        // 当令牌的消耗程度远远低于警戒线的时候
        if (oldValue < warningToken) {
            //需要放入令牌的数量 = (当前时间 - 最后一次放令牌的时间) * 每秒令牌数 / 1000
            // 加上现有的令牌数量
            newValue = (long)(oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
        } else if (oldValue > warningToken) {
            if (passQps < (int)count / coldFactor) {
                newValue = (long)(oldValue + (currentTime - lastFilledTime.get()) * count / 1000);
            }
        }
        // 不能超过最大令牌数
        return Math.min(newValue, maxToken);
    }

}
