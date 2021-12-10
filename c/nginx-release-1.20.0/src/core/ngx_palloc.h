
/*
 * Copyright (C) Igor Sysoev
 * Copyright (C) Nginx, Inc.
 */


#ifndef _NGX_PALLOC_H_INCLUDED_
#define _NGX_PALLOC_H_INCLUDED_


#include <ngx_config.h>
#include <ngx_core.h>


/*
 * NGX_MAX_ALLOC_FROM_POOL should be (ngx_pagesize - 1), i.e. 4095 on x86.
 * On Windows NT it decreases a number of locked pages in a kernel.
 */
#define NGX_MAX_ALLOC_FROM_POOL  (ngx_pagesize - 1)

#define NGX_DEFAULT_POOL_SIZE    (16 * 1024)

#define NGX_POOL_ALIGNMENT       16
#define NGX_MIN_POOL_SIZE                                                     \
    ngx_align((sizeof(ngx_pool_t) + 2 * sizeof(ngx_pool_large_t)),            \
              NGX_POOL_ALIGNMENT)


typedef void (*ngx_pool_cleanup_pt)(void *data);

typedef struct ngx_pool_cleanup_s  ngx_pool_cleanup_t;

/**
 * 销毁内存池的时候处理其他操作，保证内存的正确销毁，在销毁的时候触发handler函数指针
 */
struct ngx_pool_cleanup_s {
    // 清理的回调函数
    ngx_pool_cleanup_pt   handler;
    // 指向存储的数据地址
    void                 *data;
    // 下一个ngx_pool_cleanup_t
    ngx_pool_cleanup_t   *next;
};


typedef struct ngx_pool_large_s  ngx_pool_large_t;

struct ngx_pool_large_s {
    // 指向下一个存储地址，通过这个地址可以知道当前块长度
    ngx_pool_large_t     *next;
    // 数据块指针地址
    void                 *alloc;
};


typedef struct {
    // 内存池中未使用内存的开始节点地址
    u_char               *last;
    // 内存池的结束地址
    u_char               *end;
    // 指向下一个内存池
    ngx_pool_t           *next;
    // 失败次数
    ngx_uint_t            failed;
} ngx_pool_data_t;

/**
 * nginx内存池数据结构
 */
struct ngx_pool_s {
    // 内存池的数据区域
    ngx_pool_data_t       d;
    // 最大每次可分配内存
    size_t                max;
    // 指向当前内存池指针地址。ngx_pool_t链表上最后一个缓存池结构
    ngx_pool_t           *current;
    // 缓冲区链表
    ngx_chain_t          *chain;
    // 存储大数据的链表
    ngx_pool_large_t     *large;
    // 可自定义回调函数，清除内存块分配的内存
    ngx_pool_cleanup_t   *cleanup;
    // 日志
    ngx_log_t            *log;
};


/**
 * 在进行内存池销毁操作的时候能对文件描述符进行关闭操作
 */
typedef struct {
    ngx_fd_t              fd;
    u_char               *name;
    ngx_log_t            *log;
} ngx_pool_cleanup_file_t;


ngx_pool_t *ngx_create_pool(size_t size, ngx_log_t *log);
void ngx_destroy_pool(ngx_pool_t *pool);
void ngx_reset_pool(ngx_pool_t *pool);

void *ngx_palloc(ngx_pool_t *pool, size_t size);
void *ngx_pnalloc(ngx_pool_t *pool, size_t size);
void *ngx_pcalloc(ngx_pool_t *pool, size_t size);
void *ngx_pmemalign(ngx_pool_t *pool, size_t size, size_t alignment);
ngx_int_t ngx_pfree(ngx_pool_t *pool, void *p);


ngx_pool_cleanup_t *ngx_pool_cleanup_add(ngx_pool_t *p, size_t size);
void ngx_pool_run_cleanup_file(ngx_pool_t *p, ngx_fd_t fd);
void ngx_pool_cleanup_file(void *data);
void ngx_pool_delete_file(void *data);


#endif /* _NGX_PALLOC_H_INCLUDED_ */
