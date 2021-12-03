/* Hash Tables Implementation.
 *
 * This file implements in-memory hash tables with insert/del/replace/find/
 * get-random-element operations. Hash tables will auto-resize if needed
 * tables of power of two in size are used, collisions are handled by
 * chaining. See the source code for more information... :)
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

#ifndef __DICT_H
#define __DICT_H

#include <limits.h>
#include <stdint.h>
#include <stdlib.h>
#include "mt19937-64.h"

/** 定义成功值与错误值 */
#define DICT_OK 0
#define DICT_ERR 1

/* Unused arguments generate annoying warnings... */
// Dict没被用到时，用来提示警告的
#define DICT_NOTUSED(V) ((void) V)

/** 字典结构体，保存K-V值的结构体 */
typedef struct dictEntry {
    void *key;
    union {
        void *val;
        // 无符号整形值
        uint64_t u64;
        // 有符号整形值
        int64_t s64;
        // double类型的值
        double d;
    } v;
    // 下一个字典节点
    struct dictEntry *next;
} dictEntry;

/** 字典类型 */
typedef struct dictType {
    // 哈希计算方法，返回无符号整型
    uint64_t (*hashFunction)(const void *key);
    // 复制key方法
    void *(*keyDup)(void *privdata, const void *key);
    // 复制val方法
    void *(*valDup)(void *privdata, const void *obj);
    // key值比较方法
    int (*keyCompare)(void *privdata, const void *key1, const void *key2);
    // key的析构函数
    void (*keyDestructor)(void *privdata, void *key);
    // val的析构函数
    void (*valDestructor)(void *privdata, void *obj);
    // 是否允许扩展函数
    int (*expandAllowed)(size_t moreMem, double usedRatio);
} dictType;

/* This is our hash table structure. Every dictionary has two of this as we
 * implement incremental rehashing, for the old to the new table. */
 /** 哈希表结构体 */
typedef struct dictht {
    // 字典实体
    dictEntry **table;
    // 容纳的尺寸
    unsigned long size;
    // 掩码 通常是size - 1,做按位与运算
    unsigned long sizemask;
    // 被使用的尺寸
    unsigned long used;
} dictht;

typedef struct dict {
    // 字典类型
    dictType *type;
    // 私有数据指针
    void *privdata;
    // 字典哈希表，共两张，一张旧表，一张新的
    dictht ht[2];
    // 重定向hash时的下标
    long rehashidx; /* rehashing not in progress if rehashidx == -1 */
    int16_t pauserehash; /* If >0 rehashing is paused (<0 indicates coding error) */
} dict;

/* If safe is set to 1 this is a safe iterator, that means, you can call
 * dictAdd, dictFind, and other functions against the dictionary even while
 * iterating. Otherwise it is a non safe iterator, and only dictNext()
 * should be called while iterating. */
 /** 字典迭代器，如果是安全迭代器，safe设置为1，可以调用dictAdd, dictFind */
 /** 如果是不安全的，则只能调用dictNext */
typedef struct dictIterator {
    // 当前字典
    dict *d;
    // 下标
    long index;
    // 表格，安全值的表格代表的是旧的表格还是新的表格
    int table, safe;
    // 字典实体
    dictEntry *entry, *nextEntry;
    /* unsafe iterator fingerprint for misuse detection. */
    // 指纹标记，避免不安全的迭代器滥用现象
    long long fingerprint;
} dictIterator;

// 字典扫描方法
typedef void (dictScanFunction)(void *privdata, const dictEntry *de);
typedef void (dictScanBucketFunction)(void *privdata, dictEntry **bucketref);

/* This is the initial size of every hash table */
// 初始化哈希表的数目
#define DICT_HT_INITIAL_SIZE     4

/* ------------------------------- Macros ------------------------------------*/
// 如果字典定义了val析构函数，则调用val析构函数
#define dictFreeVal(d, entry) \
    if ((d)->type->valDestructor) \
        (d)->type->valDestructor((d)->privdata, (entry)->v.val)
// 字典val函数复制时候调用，如果dict中的dictType定义了这个函数指针
#define dictSetVal(d, entry, _val_) do { \
    if ((d)->type->valDup) \
        (entry)->v.val = (d)->type->valDup((d)->privdata, _val_); \
    else \
        (entry)->v.val = (_val_); \
} while(0)
// 设置dictEntry中共用体v中有符号类型的值
#define dictSetSignedIntegerVal(entry, _val_) \
    do { (entry)->v.s64 = _val_; } while(0)
// 设置dictEntry中共用体v中无符号类型的值
#define dictSetUnsignedIntegerVal(entry, _val_) \
    do { (entry)->v.u64 = _val_; } while(0)
// 设置dictEntry中共用体v中double类型的值
#define dictSetDoubleVal(entry, _val_) \
    do { (entry)->v.d = _val_; } while(0)
// 调用dictType定义的key析构函数
#define dictFreeKey(d, entry) \
    if ((d)->type->keyDestructor) \
        (d)->type->keyDestructor((d)->privdata, (entry)->key)
// 调用dictType定义的key复制函数，没有定义直接赋值
#define dictSetKey(d, entry, _key_) do { \
    if ((d)->type->keyDup) \
        (entry)->key = (d)->type->keyDup((d)->privdata, _key_); \
    else \
        (entry)->key = (_key_); \
} while(0)
// 调用dictType定义的key比较函数，没有定义直接key值直接比较
#define dictCompareKeys(d, key1, key2) \
    (((d)->type->keyCompare) ? \
        (d)->type->keyCompare((d)->privdata, key1, key2) : \
        (key1) == (key2))

// 哈希定位方法
#define dictHashKey(d, key) (d)->type->hashFunction(key)
// 获取dictEntry的key值
#define dictGetKey(he) ((he)->key)
// 获取dictEntry中共用体v中的val值
#define dictGetVal(he) ((he)->v.val)
// 获取dictEntry中共用体v中的有符号值
#define dictGetSignedIntegerVal(he) ((he)->v.s64)
// 获取dictEntry中共用体v中的无符号值
#define dictGetUnsignedIntegerVal(he) ((he)->v.u64)
// 获取dictEntry中共用体v中的double类型值
#define dictGetDoubleVal(he) ((he)->v.d)
// 获取dict字典中总的表大小
#define dictSlots(d) ((d)->ht[0].size+(d)->ht[1].size)
// 获取dict字典中总的表正在被使用的数量
#define dictSize(d) ((d)->ht[0].used+(d)->ht[1].used)
// 字典有无被重定位过
#define dictIsRehashing(d) ((d)->rehashidx != -1)
#define dictPauseRehashing(d) (d)->pauserehash++
#define dictResumeRehashing(d) (d)->pauserehash--

/* If our unsigned long type can store a 64 bit number, use a 64 bit PRNG. */
//#if ULONG_MAX >= 0xffffffffffffffff
//#define randomULong() ((unsigned long) genrand64_int64())
//#else
#define randomULong() random()
//#endif

/* API */
dict *dictCreate(dictType *type, void *privDataPtr); // 创建dict字典
int dictExpand(dict *d, unsigned long size); // 字典扩容方法
int dictTryExpand(dict *d, unsigned long size); 
int dictAdd(dict *d, void *key, void *val); // 字典根据key,val添加一个字典集
dictEntry *dictAddRaw(dict *d, void *key, dictEntry **existing); // 字典添加一个只有key值的dictEntry
dictEntry *dictAddOrFind(dict *d, void *key);
int dictReplace(dict *d, void *key, void *val); // 代替dict中一个字典集
int dictDelete(dict *d, const void *key); // 删除dict中的一个字典集
dictEntry *dictUnlink(dict *ht, const void *key); // 
void dictFreeUnlinkedEntry(dict *d, dictEntry *he);
void dictRelease(dict *d); // 释放整个dict
dictEntry * dictFind(dict *d, const void *key); // 根据key查找一个字典集
void *dictFetchValue(dict *d, const void *key); // 根据key值寻找相应的val
int dictResize(dict *d); // 重新计算大小
dictIterator *dictGetIterator(dict *d); // 获取dict迭代器
dictIterator *dictGetSafeIterator(dict *d); // 获取dict安全迭代器
dictEntry *dictNext(dictIterator *iter); // 根据字典迭代器获取下一个字典集
void dictReleaseIterator(dictIterator *iter); // 释放迭代器
dictEntry *dictGetRandomKey(dict *d); // 随机获取一个字典集
dictEntry *dictGetFairRandomKey(dict *d); // 
unsigned int dictGetSomeKeys(dict *d, dictEntry **des, unsigned int count);
void dictGetStats(char *buf, size_t bufsize, dict *d); // 获取当前字典状态
uint64_t dictGenHashFunction(const void *key, int len); // 输入的key值，目标长度，此方法帮你计算出索引值
uint64_t dictGenCaseHashFunction(const unsigned char *buf, int len); // 提供了一种比较简单的哈希算法
void dictEmpty(dict *d, void(callback)(void*)); // 清空字典
void dictEnableResize(void); // 启用调整方法
void dictDisableResize(void); // 禁用调整方法
int dictRehash(dict *d, int n); // hash重定位，主要从旧的表映射到新表中
int dictRehashMilliseconds(dict *d, int ms); // 在给定时间内，循环执行哈希重定位
void dictSetHashFunctionSeed(uint8_t *seed); // 设置hash方法的种子
uint8_t *dictGetHashFunctionSeed(void); // 获取hash种子
// 字典扫描方法
unsigned long dictScan(dict *d, unsigned long v, dictScanFunction *fn, dictScanBucketFunction *bucketfn, void *privdata);
// 获取key的hash值
uint64_t dictGetHash(dict *d, const void *key);
dictEntry **dictFindEntryRefByPtrAndHash(dict *d, const void *oldptr, uint64_t hash);

/* Hash table types */
/** 字典的类型 */
extern dictType dictTypeHeapStringCopyKey;
extern dictType dictTypeHeapStrings;
extern dictType dictTypeHeapStringCopyKeyValue;

#endif /* __DICT_H */
