//
// Created by DELL on 2021/2/5.
//

#include <stdio.h>
#include <string.h>
#include "../sds.h"
#include "../adlist.h"
#include "../dict.h"
#include "../ziplist.h"

void testList();
void testSds();
void testDict();
// void testZiplist();

int main() {
    testSds();
    //testDict();
    // testZiplist();
    return 0;
}

void testSds() {
    sds empty = sdsempty();
    sdscat(empty, "hello");
    size_t i = sdsavail(empty);
    size_t j = sdslen(empty);
    printf("%d,%d",i,j);
}

void testList() {
    list *l = listCreate();
    int i = 2;
    list *l1 = listAddNodeHead(l, &i);
    listNode *head = l1->head;
    printf("%d", head->value);
}
uint64_t hashCallback(const void *key) {
    return dictGenHashFunction((unsigned char*)key, sdslen((char*)key));
}

int compareCallback(void *privdata, const void *key1, const void *key2) {
    int l1,l2;
    DICT_NOTUSED(privdata);

    l1 = sdslen((sds)key1);
    l2 = sdslen((sds)key2);
    if (l1 != l2) return 0;
    return memcmp(key1, key2, l1) == 0;
}

void freeCallback(void *privdata, void *val) {
    DICT_NOTUSED(privdata);

    sdsfree(val);
}

void _serverAssert(char *estr, char *file, int line){}
void _serverPanic(const char *file, int line, const char *msg, ...){}


void testDict() {
    dictType BenchmarkDictType = {
            hashCallback,
            NULL,
            NULL,
            compareCallback,
            freeCallback,
            NULL,
            NULL
    };
    int i = 3;
    dict *d = dictCreate(&BenchmarkDictType, &i);
    int j = 2;
    dictAdd(d, "a", &j);
}

