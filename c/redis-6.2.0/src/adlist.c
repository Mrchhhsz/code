/* adlist.c - A generic doubly linked list implementation
 *
 * Copyright (c) 2006-2010, Salvatore Sanfilippo <antirez at gmail dot com>
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


#include <stdlib.h>
#include "adlist.h"
#include "zmalloc.h"

/* Create a new list. The created list can be freed with
 * listRelease(), but private value of every node need to be freed
 * by the user before to call listRelease(), or by setting a free method using
 * listSetFreeMethod.
 *
 * On error, NULL is returned. Otherwise the pointer to the new list. */
 /** 创建节点列表 */
list *listCreate(void)
{
    struct list *list;
    // 分配list内存，并返回指针，分配失败直接返回
    if ((list = zmalloc(sizeof(*list))) == NULL)
        return NULL;
    // 初始化列表
    list->head = list->tail = NULL;
    list->len = 0;
    list->dup = NULL;
    list->free = NULL;
    list->match = NULL;
    return list;
}

/* Remove all the elements from the list without destroying the list itself. */
/** 移除所有的元素，但是不销毁列表 */
void listEmpty(list *list)
{
    unsigned long len;
    listNode *current, *next;
    // 获取头节点指针和列表长度指针
    current = list->head;
    len = list->len;
    while(len--) {
        next = current->next;
        // 如果列表存在free释放方法定义，则调用释放函数释放当前节点的函数指针
        if (list->free) list->free(current->value);
        // 先释放当前节点空间，在将当前指针指向下一个节点
        zfree(current);
        current = next;
    }
    // 头结点和尾节点置空
    list->head = list->tail = NULL;
    // 长度清0
    list->len = 0;
}

/* Free the whole list.
 *
 * This function can't fail. */
 /** 释放整个列表的空间 */
void listRelease(list *list)
{
    // 清空列表
    listEmpty(list);
    // 释放列表空间
    zfree(list);
}

/* Add a new node to the list, to head, containing the specified 'value'
 * pointer as value.
 *
 * On error, NULL is returned and no operation is performed (i.e. the
 * list remains unaltered).
 * On success the 'list' pointer you pass to the function is returned. */
 /** 列表添加头结点 */
list *listAddNodeHead(list *list, void *value)
{
    listNode *node;
    // 分配节点内存
    if ((node = zmalloc(sizeof(*node))) == NULL)
        return NULL;
    // 赋值
    node->value = value;
    // 判断列表的长度是否为0
    if (list->len == 0) {
        list->head = list->tail = node;
        node->prev = node->next = NULL;
    } else {
        // 节点的前驱为null
        node->prev = NULL;
        // 节点的后继为头节点
        node->next = list->head;
        // 更新头结点的前驱为当前节点
        list->head->prev = node;
        // 头指针指向当前节点
        list->head = node;
    }
    // 长度加1
    list->len++;
    return list;
}

/* Add a new node to the list, to tail, containing the specified 'value'
 * pointer as value.
 *
 * On error, NULL is returned and no operation is performed (i.e. the
 * list remains unaltered).
 * On success the 'list' pointer you pass to the function is returned. */
 /** 添加尾节点 */
list *listAddNodeTail(list *list, void *value)
{
    listNode *node;
    // 分配节点内存
    if ((node = zmalloc(sizeof(*node))) == NULL)
        return NULL;
    // 赋值
    node->value = value;
    // 判断列表长度是否为0
    if (list->len == 0) {
        list->head = list->tail = node;
        node->prev = node->next = NULL;
    } else {
        // 当前节点的前驱指向尾节点
        node->prev = list->tail;
        // 当前节点的后继为null
        node->next = NULL;
        // 尾节点的后继为当前节点
        list->tail->next = node;
        // 列表的尾指针指向当前节点
        list->tail = node;
    }
    // 长度加1
    list->len++;
    return list;
}

/**
  *插入一个节点
  */
list *listInsertNode(list *list, listNode *old_node, void *value, int after) {
    listNode *node;

    if ((node = zmalloc(sizeof(*node))) == NULL)
        return NULL;
    node->value = value;
    // after为1表示在给定节点后面插入节点
    if (after) {
        // 节点的前驱为给定节点
        node->prev = old_node;
        // 节点的后继为给定节点的后继
        node->next = old_node->next;
        // 如果给定节点是尾节点
        if (list->tail == old_node) {
            // 列表尾指针指向节点
            list->tail = node;
        }
    } else {
        // 如果在给定节点前插入节点
        // 节点的后继为给定节点
        node->next = old_node;
        // 节点的前驱为给定节点的前驱
        node->prev = old_node->prev;
        // 如果给定节点为列表的头节点
        if (list->head == old_node) {
            // 列表头指针指向节点
            list->head = node;
        }
    }
    // 上面代码执行完还没有将 节点的前驱和后继给断开
    // 如果节点的前驱不为空，节点的前驱的next指针指向节点
    if (node->prev != NULL) {
        node->prev->next = node;
    }
    // 如果节点的后继不为空，节点的后继和prev指针指向节点
    if (node->next != NULL) {
        node->next->prev = node;
    }
    list->len++;
    return list;
}

/* Remove the specified node from the specified list.
 * It's up to the caller to free the private value of the node.
 *
 * This function can't fail. */
 /** 删除节点 */
void listDelNode(list *list, listNode *node)
{

    if (node->prev)
    // 如果节点的前驱存在，节点的前驱的next指针指向节点的后继
        node->prev->next = node->next;
    else
    // 否则节点是头结点，将列表头指针指向节点的后继
        list->head = node->next;
    // next指正操作
    if (node->next)
        node->next->prev = node->prev;
    else
        list->tail = node->prev;
    if (list->free) list->free(node->value);
    // 释放节点空间
    zfree(node);
    list->len--;
}

/* Returns a list iterator 'iter'. After the initialization every
 * call to listNext() will return the next element of the list.
 *
 * This function can't fail. */
listIter *listGetIterator(list *list, int direction)
{
    listIter *iter;
    // 分配列表迭代器内存
    if ((iter = zmalloc(sizeof(*iter))) == NULL) return NULL;
    // 如果方向从头部开始
    if (direction == AL_START_HEAD)
        iter->next = list->head;
    else
        iter->next = list->tail;
    iter->direction = direction;
    return iter;
}

/* Release the iterator memory */
void listReleaseIterator(listIter *iter) {
    // 释放迭代器空间
    zfree(iter);
}

/* Create an iterator in the list private iterator structure */
void listRewind(list *list, listIter *li) {
    li->next = list->head;
    li->direction = AL_START_HEAD;
}

void listRewindTail(list *list, listIter *li) {
    li->next = list->tail;
    li->direction = AL_START_TAIL;
}

/* Return the next element of an iterator.
 * It's valid to remove the currently returned element using
 * listDelNode(), but not to remove other elements.
 *
 * The function returns a pointer to the next element of the list,
 * or NULL if there are no more elements, so the classical usage
 * pattern is:
 *
 * iter = listGetIterator(list,<direction>);
 * while ((node = listNext(iter)) != NULL) {
 *     doSomethingWith(listNodeValue(node));
 * }
 *
 * */
 /** 获取列表迭代器的下一个节点 */
listNode *listNext(listIter *iter)
{
    // 定义指针指向下一个节点
    listNode *current = iter->next;
    // 如果节点不为null
    if (current != NULL) {
        // 如果节点的方向从头开始
        if (iter->direction == AL_START_HEAD)
            // 迭代器的next指向当前节点的下一个节点
            iter->next = current->next;
        else
            // 否则，从尾开始，迭代器的next指向当前节点的上一个节点
            iter->next = current->prev;
    }
    return current;
}

/* Duplicate the whole list. On out of memory NULL is returned.
 * On success a copy of the original list is returned.
 *
 * The 'Dup' method set with listSetDupMethod() function is used
 * to copy the node value. Otherwise the same pointer value of
 * the original node is used as value of the copied node.
 *
 * The original list both on success or error is never modified. */
list *listDup(list *orig)
{
    list *copy;
    listIter iter;
    listNode *node;
    // 创建一个新列表
    if ((copy = listCreate()) == NULL)
        return NULL;
    // 新列表的dup方法等于旧列表的dup方法
    copy->dup = orig->dup;
    // 新列表的free方法等于旧列表的free方法
    copy->free = orig->free;
    copy->match = orig->match;
    // 重置迭代器，从头开始
    listRewind(orig, &iter);
    while((node = listNext(&iter)) != NULL) {
        // 从前往后遍历节点
        void *value;
        // 如果定义了列表复制方法
        if (copy->dup) {
            // 调用复制方法
            value = copy->dup(node->value);
            // 如果发生的OOM，直接释放新列表的所有空间
            if (value == NULL) {
                listRelease(copy);
                return NULL;
            }
        } else
            // 没定义直接复制指针
            value = node->value;
        // 在新列表的尾部插入节点
        if (listAddNodeTail(copy, value) == NULL) {
            listRelease(copy);
            return NULL;
        }
    }
    return copy;
}

/* Search the list for a node matching a given key.
 * The match is performed using the 'match' method
 * set with listSetMatchMethod(). If no 'match' method
 * is set, the 'value' pointer of every node is directly
 * compared with the 'key' pointer.
 *
 * On success the first matching node pointer is returned
 * (search starts from head). If no matching node exists
 * NULL is returned. */
 /** 根据关键字搜索节点 */
listNode *listSearchKey(list *list, void *key)
{
    listIter iter;
    listNode *node;

    listRewind(list, &iter);
    // 遍历节点
    while((node = listNext(&iter)) != NULL) {
        // 如果节点有match方法
        if (list->match) {
            // 使用match方法来做匹配，匹配成功返回节点
            if (list->match(node->value, key)) {
                return node;
            }
        } else {
            // 没有match方法，直接做等于判断
            if (key == node->value) {
                return node;
            }
        }
    }
    return NULL;
}

/* Return the element at the specified zero-based index
 * where 0 is the head, 1 is the element next to head
 * and so on. Negative integers are used in order to count
 * from the tail, -1 is the last element, -2 the penultimate
 * and so on. If the index is out of range NULL is returned. */
 /** 根据下标获取节点 */
listNode *listIndex(list *list, long index) {
    listNode *n;

    if (index < 0) {
    // 如果索引小于0，取索引的相反数 - 1作为索引
        index = (-index)-1;
        // 从尾部遍历列表，直到倒数第index个节点
        n = list->tail;
        while(index-- && n) n = n->prev;
    } else {
    // 如果索引大于等于0，从头部遍历节点。
        n = list->head;
        while(index-- && n) n = n->next;
    }
    return n;
}

/* Rotate the list removing the tail node and inserting it to the head. */
/** 将尾节点放到头节点 */
void listRotateTailToHead(list *list) {
    if (listLength(list) <= 1) return;

    /* Detach current tail */
    // 列表尾指针指向尾节点的prev,尾节点的prev的next指针置空
    listNode *tail = list->tail;
    list->tail = tail->prev;
    list->tail->next = NULL;
    /* Move it as head */
    list->head->prev = tail;
    tail->prev = NULL;
    tail->next = list->head;
    list->head = tail;
}

/* Rotate the list removing the head node and inserting it to the tail. */
void listRotateHeadToTail(list *list) {
    if (listLength(list) <= 1) return;

    listNode *head = list->head;
    /* Detach current head */
    list->head = head->next;
    list->head->prev = NULL;
    /* Move it as tail */
    list->tail->next = head;
    head->next = NULL;
    head->prev = list->tail;
    list->tail = head;
}

/* Add all the elements of the list 'o' at the end of the
 * list 'l'. The list 'other' remains empty but otherwise valid. */
 /** 将o列表添加到l列表的尾部 */
void listJoin(list *l, list *o) {
    if (o->len == 0) return;

    // o列表的头结点的前驱指向l列表的尾节点
    o->head->prev = l->tail;

    if (l->tail)
    // 如果l列表的尾节点存在，就将尾节点指向o列表的头节点
        l->tail->next = o->head;
    else
        l->head = o->head;
    // l结点的尾指针指向o列表的尾指针
    l->tail = o->tail;
    l->len += o->len;

    /* Setup other as an empty list. */
    o->head = o->tail = NULL;
    o->len = 0;
}
