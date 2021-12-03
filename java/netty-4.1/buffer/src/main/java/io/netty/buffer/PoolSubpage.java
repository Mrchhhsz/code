/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.buffer;

import static io.netty.buffer.PoolChunk.RUN_OFFSET_SHIFT;
import static io.netty.buffer.PoolChunk.SIZE_SHIFT;
import static io.netty.buffer.PoolChunk.IS_USED_SHIFT;
import static io.netty.buffer.PoolChunk.IS_SUBPAGE_SHIFT;
import static io.netty.buffer.SizeClasses.LOG2_QUANTUM;

final class PoolSubpage<T> implements PoolSubpageMetric {

    // 记录当前PoolSubpage的8KB内存是从那一块PoolChunk中申请到的
    final PoolChunk<T> chunk;
    private final int pageShifts;
    private final int runOffset;
    // 当前总内存大小
    private final int runSize;
    // 存储当前PoolSubpage中各内存块的使用情况
    private final long[] bitmap;

    PoolSubpage<T> prev; // 指向前置节点的指针
    PoolSubpage<T> next; // 指向后置节点的指针

    boolean doNotDestroy; // 表示当前PoolSubpage是否已经被销毁了
    int elemSize; // 每个内存块的大小
    private int maxNumElems; // 记录内存块的总个数
    private int bitmapLength; // 记录总可使用的bitmap数组的元素的个数
    // 记录下一个可用的节点，初始为0，只要在该PoolSubpage中申请过一次内存，就会更新为-1
    private int nextAvail;
    // 剩余可用的内存块的个数
    private int numAvail;

    // TODO: Test if adding padding helps under contention
    //private long pad0, pad1, pad2, pad3, pad4, pad5, pad6, pad7;

    /** Special constructor that creates a linked list head */
    PoolSubpage() {
        chunk = null;
        pageShifts = -1;
        runOffset = -1;
        elemSize = -1;
        runSize = -1;
        bitmap = null;
    }

    PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int pageShifts, int runOffset, int runSize, int elemSize) {
        this.chunk = chunk; // 所在块
        this.pageShifts = pageShifts; //
        this.runOffset = runOffset; // 块内偏移量
        this.runSize = runSize; //
        this.elemSize = elemSize;
        bitmap = new long[runSize >>> 6 + LOG2_QUANTUM]; // runSize / 64 / QUANTUM

        doNotDestroy = true;
        if (elemSize != 0) {
            // 总内存大小 / 每个元素占内存大小 = 最大元素个数
            maxNumElems = numAvail = runSize / elemSize;
            // 初始化下一个元素的索引为0
            nextAvail = 0;
            // bitmap数组的长度 = 元素个数 / 2^6; bitmap是long数组，一个long的位数是64即2^6
            bitmapLength = maxNumElems >>> 6;
            // 判断最大元素个数是否小于64（因为最大元素个数一定会是2的次方）
            // 如果最大元素小于64，上面的位移操作会为0需要+1 = 1；
            if ((maxNumElems & 63) != 0) {
                bitmapLength ++;
            }
            // 初始化bitmap的值
            for (int i = 0; i < bitmapLength; i ++) {
                bitmap[i] = 0;
            }
        }
        // 将当前poolSubpage添加到PoolSubpage的链表中
        addToPool(head);
    }

    /**
     * Returns the bitmap index of the subpage allocation.
     */
    long allocate() {
        if (numAvail == 0 || !doNotDestroy) {
            return -1;
        }
        // 计算下一个可用的内存块的位置，bitmap展开后的第多少bit位
        final int bitmapIdx = getNextAvail();
        // 获取该内存块是bitmap数组中的第几号元素，每一号元素是2^6位
        int q = bitmapIdx >>> 6;
        // 获取该内存块是bitmap数组中q号元素的第多少位
        int r = bitmapIdx & 63;
        assert (bitmap[q] >>> r & 1) == 0;
        // 将bitmap数组中q号元素的目标内存块位置标记为1，表示已经使用
        bitmap[q] |= 1L << r;

        // 如果当前poolSubpage中可用的内存块为0，则将其从链表中移除
        if (-- numAvail == 0) {
            removeFromPool();
        }

        // 将得到的bitmapIdx放到返回值的高32位中
        return toHandle(bitmapIdx);
    }

    /**
     * @return {@code true} if this subpage is in use.
     *         {@code false} if this subpage is not used by its chunk and thus it's OK to be released.
     */
    boolean free(PoolSubpage<T> head, int bitmapIdx) {
        if (elemSize == 0) {
            return true;
        }
        int q = bitmapIdx >>> 6;
        int r = bitmapIdx & 63;
        assert (bitmap[q] >>> r & 1) != 0;
        bitmap[q] ^= 1L << r;

        setNextAvail(bitmapIdx);

        if (numAvail ++ == 0) {
            addToPool(head);
            return true;
        }

        if (numAvail != maxNumElems) {
            return true;
        } else {
            // Subpage not in use (numAvail == maxNumElems)
            if (prev == next) {
                // Do not remove if this subpage is the only one left in the pool.
                return true;
            }

            // Remove this subpage from the pool if there are other subpages left in the pool.
            doNotDestroy = false;
            removeFromPool();
            return false;
        }
    }

    // 循环链表
    private void addToPool(PoolSubpage<T> head) {
        assert prev == null && next == null;
        prev = head;
        next = head.next;
        next.prev = this;
        head.next = this;
    }

    private void removeFromPool() {
        assert prev != null && next != null;
        prev.next = next;
        next.prev = prev;
        next = null;
        prev = null;
    }

    private void setNextAvail(int bitmapIdx) {
        nextAvail = bitmapIdx;
    }

    private int getNextAvail() {
        // this.nextAvail 表示是否是第一次申请内存，第一次申请后就会变为-1
        int nextAvail = this.nextAvail;
        if (nextAvail >= 0) {
            this.nextAvail = -1;
            return nextAvail;
        }
        // 如果不是第一次申请内存，则在bitmap中进行遍历获取
        return findNextAvail();
    }

    private int findNextAvail() {
        final long[] bitmap = this.bitmap;
        final int bitmapLength = this.bitmapLength;
        for (int i = 0; i < bitmapLength; i ++) {
            long bits = bitmap[i];
            // 判断当前long型元素中是否有可用的内存块
            if (~bits != 0) {
                return findNextAvail0(i, bits);
            }
        }
        return -1;
    }

    /**
     * @param i 表示第几个元素
     * @param bits 表示该元素的值
     * @return
     */
    private int findNextAvail0(int i, long bits) {
        final int maxNumElems = this.maxNumElems;
        // 将当前是第几号元素放到返回值的第7~9号位置上
        final int baseVal = i << 6;

        // 对bits的0~63号元素进行遍历，判断其是否为0，为0表示该位置时可用内存块，
        // 从而将位置数据和baseVal进行或操作，从而得到一个表示目标内存块位置的整形数据
        for (int j = 0; j < 64; j ++) {
            if ((bits & 1) == 0) {
                int val = baseVal | j;
                if (val < maxNumElems) {
                    return val;
                } else {
                    break;
                }
            }
            // 将bits不断向右移
            bits >>>= 1;
        }
        return -1;
    }

    private long toHandle(int bitmapIdx) {
        int pages = runSize >> pageShifts;
        return (long) runOffset << RUN_OFFSET_SHIFT
               | (long) pages << SIZE_SHIFT
               | 1L << IS_USED_SHIFT
               | 1L << IS_SUBPAGE_SHIFT
               | bitmapIdx;
    }

    @Override
    public String toString() {
        final boolean doNotDestroy;
        final int maxNumElems;
        final int numAvail;
        final int elemSize;
        if (chunk == null) {
            // This is the head so there is no need to synchronize at all as these never change.
            doNotDestroy = true;
            maxNumElems = 0;
            numAvail = 0;
            elemSize = -1;
        } else {
            synchronized (chunk.arena) {
                if (!this.doNotDestroy) {
                    doNotDestroy = false;
                    // Not used for creating the String.
                    maxNumElems = numAvail = elemSize = -1;
                } else {
                    doNotDestroy = true;
                    maxNumElems = this.maxNumElems;
                    numAvail = this.numAvail;
                    elemSize = this.elemSize;
                }
            }
        }

        if (!doNotDestroy) {
            return "(" + runOffset + ": not in use)";
        }

        return "(" + runOffset + ": " + (maxNumElems - numAvail) + '/' + maxNumElems +
                ", offset: " + runOffset + ", length: " + runSize + ", elemSize: " + elemSize + ')';
    }

    @Override
    public int maxNumElements() {
        if (chunk == null) {
            // It's the head.
            return 0;
        }

        synchronized (chunk.arena) {
            return maxNumElems;
        }
    }

    @Override
    public int numAvailable() {
        if (chunk == null) {
            // It's the head.
            return 0;
        }

        synchronized (chunk.arena) {
            return numAvail;
        }
    }

    @Override
    public int elementSize() {
        if (chunk == null) {
            // It's the head.
            return -1;
        }

        synchronized (chunk.arena) {
            return elemSize;
        }
    }

    @Override
    public int pageSize() {
        return 1 << pageShifts;
    }

    void destroy() {
        if (chunk != null) {
            chunk.destroy();
        }
    }
}
