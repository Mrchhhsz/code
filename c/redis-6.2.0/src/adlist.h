/* adlist.h - A generic doubly linked list implementation
 *
 * Copyright (c) 2006-2012, Salvatore Sanfilippo <antirez at gmail dot com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of Redis nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#ifndef __ADLIST_H__
#define __ADLIST_H__

/* Node, List, and Iterator are the only data structures used currently. */

typedef struct listNode {
    // 节点的前一个节点
    struct listNode *prev;
    // 节点的下一个节点
    struct listNode *next;
    // Node的函数指针
    void *value;
} listNode;

/** list迭代器，只能为单向 */
typedef struct listIter {
    // 当前迭代位置的下一节点
    listNode *next;
    // 迭代器的方向
    int direction;
} listIter;

/** listNode列表 */
typedef struct list {
    // 列表头结点
    listNode *head;
    // 列表尾结点
    listNode *tail;

    // 下面三个方法为所有节点的公共方法，分别在相应情况下会调用
    // 复制函数指针
    void *(*dup)(void *ptr);
    // 释放函数指针
    void (*free)(void *ptr);
    // 匹配函数指针
    int (*match)(void *ptr, void *key);
    // 列表长度
    unsigned long len;
} list;

/* Functions implemented as macros */
// 获取列表长度
#define listLength(l) ((l)->len)
// 获取列表首部
#define listFirst(l) ((l)->head)
// 获取列表尾部
#define listLast(l) ((l)->tail)
// 获取给定节点的上一个几点
#define listPrevNode(n) ((n)->prev)
// 获取给定节点的下一个节点
#define listNextNode(n) ((n)->next)
// 给定几点的值，这个value不是一个数值类型，而是一个函数指针
#define listNodeValue(n) ((n)->value)

// 列表复制方法的设置
#define listSetDupMethod(l,m) ((l)->dup = (m))
// 列表释放方法的设置
#define listSetFreeMethod(l,m) ((l)->free = (m))
// 列表匹配方法的设置
#define listSetMatchMethod(l,m) ((l)->match = (m))

#define listGetDupMethod(l) ((l)->dup)
#define listGetFreeMethod(l) ((l)->free)
#define listGetMatchMethod(l) ((l)->match)

/* Prototypes */
list *listCreate(void); // 创建list列表
void listRelease(list *list); // 列表的释放
void listEmpty(list *list); // 移除所有的元素
list *listAddNodeHead(list *list, void *value); // 添加列表头节点
list *listAddNodeTail(list *list, void *value); // 添加列表尾节点
list *listInsertNode(list *list, listNode *old_node, void *value, int after); // 在某位置上插入节点（after表示是在前面还是后面）
void listDelNode(list *list, listNode *node); // 删除节点
listIter *listGetIterator(list *list, int direction); // 获取列表给定方向上的迭代器
listNode *listNext(listIter *iter); // 获取迭代器的下一个元素
void listReleaseIterator(listIter *iter); // 释放迭代器
list *listDup(list *orig); // 列表的复制
listNode *listSearchKey(list *list, void *key); // 关键字搜索具体的节点
listNode *listIndex(list *list, long index); // 下标查找具体的节点
void listRewind(list *list, listIter *li); // 重置迭代器为方向从头开始
void listRewindTail(list *list, listIter *li); // 重置迭代器为方向从尾部开始
void listRotateTailToHead(list *list); // 列表旋转操作
void listRotateHeadToTail(list *list);
void listJoin(list *l, list *o); // join两个列表

/* Directions for iterators */
#define AL_START_HEAD 0
#define AL_START_TAIL 1

#endif /* __ADLIST_H__ */
