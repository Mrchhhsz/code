#ifndef __CLUSTER_H
#define __CLUSTER_H

/*-----------------------------------------------------------------------------
 * Redis cluster data structures, defines, exported API.
 *----------------------------------------------------------------------------*/
// 槽数量
#define CLUSTER_SLOTS 16384
// 集群在线
#define CLUSTER_OK 0          /* Everything looks ok */
// 集群挂了
#define CLUSTER_FAIL 1        /* The cluster can't work */
// 节点名字的长度
#define CLUSTER_NAMELEN 40    /* sha1 hex length */
// 集群实际端口 = 用户指定端口 + CLUSTER_PORT_INCR
#define CLUSTER_PORT_INCR 10000 /* Cluster port = baseport + PORT_INCR */

/* The following defines are amount of time, sometimes expressed as
 * multiplicators of the node timeout value (when ending with MULT). */
/* 一下是和时间有关的一些常量
 * 以 _MULT结尾的常量会作为时间值的乘法因子来使用*/
// 检验下线报告的乘法因子
#define CLUSTER_FAIL_REPORT_VALIDITY_MULT 2 /* Fail report validity. */
// 撤销主节点FAIL状态的乘法因子
#define CLUSTER_FAIL_UNDO_TIME_MULT 2 /* Undo fail if master is back. */
// 撤销主节点FAIL状态的假发因子
#define CLUSTER_FAIL_UNDO_TIME_ADD 10 /* Some additional time. */
#define CLUSTER_FAILOVER_DELAY 5 /* Seconds */
/* 在进行手动的故障转移之前需要等待的超时时间 */
#define CLUSTER_MF_TIMEOUT 5000 /* Milliseconds to do a manual failover. */
#define CLUSTER_MF_PAUSE_MULT 2 /* Master pause manual failover mult. */
#define CLUSTER_SLAVE_MIGRATION_DELAY 5000 /* Delay for slave migration. */

/* Redirection errors returned by getNodeByQuery(). */
// 节点可以处理这个命令
#define CLUSTER_REDIR_NONE 0          /* Node can serve the request. */
// 键在其他槽
#define CLUSTER_REDIR_CROSS_SLOT 1    /* -CROSSSLOT request. */
// 键所处的槽正在进行rehash
#define CLUSTER_REDIR_UNSTABLE 2      /* -TRYAGAIN redirection required */
// 需要进行ASK转向
#define CLUSTER_REDIR_ASK 3           /* -ASK redirection required. */
// 需要进行MOVED转向
#define CLUSTER_REDIR_MOVED 4         /* -MOVED redirection required. */
// 如果集群不是OK状态，返回 —CLUSTERDOWN
#define CLUSTER_REDIR_DOWN_STATE 5    /* -CLUSTERDOWN, global state. */
// 如果集群不存在节点，则报错，返回 —CLUSTERDOEN
#define CLUSTER_REDIR_DOWN_UNBOUND 6  /* -CLUSTERDOWN, unbound slot. */
//
#define CLUSTER_REDIR_DOWN_RO_STATE 7 /* -CLUSTERDOWN, allow reads. */

struct clusterNode;

/* clusterLink encapsulates everything needed to talk with a remote node. */
/* clusterLink 包含和其他节点通信的所有信息 */
typedef struct clusterLink {
    // 连接创建时间
    mstime_t ctime;             /* Link creation time */
    // 和远程节点的连接
    connection *conn;           /* Connection to remote node */
    // 输出缓冲区，保存着等待发送给其他节点的消息
    sds sndbuf;                 /* Packet send buffer */
    // 输入缓冲区
    char *rcvbuf;               /* Packet reception buffer */
    size_t rcvbuf_len;          /* Used size of rcvbuf */
    size_t rcvbuf_alloc;        /* Allocated size of rcvbuf */
    // 与这个连接相关的节点
    struct clusterNode *node;   /* Node related to this link if any, or NULL */
} clusterLink;

/* Cluster node flags and macros. */
#define CLUSTER_NODE_MASTER 1     /* The node is a master */
#define CLUSTER_NODE_SLAVE 2      /* The node is a slave */
// 该节点疑似下线，需要对它的状态进行确认
#define CLUSTER_NODE_PFAIL 4      /* Failure? Need acknowledge */
#define CLUSTER_NODE_FAIL 8       /* The node is believed to be malfunctioning */
#define CLUSTER_NODE_MYSELF 16    /* This node is myself */
// 该节点还没有与当前节点进行一个ping通信
#define CLUSTER_NODE_HANDSHAKE 32 /* We have still to exchange the first ping */
// 该节点没有地址
#define CLUSTER_NODE_NOADDR   64  /* We don't know the address of this node */
#define CLUSTER_NODE_MEET 128     /* Send a MEET message to this node */
#define CLUSTER_NODE_MIGRATE_TO 256 /* Master eligible for replica migration. */
#define CLUSTER_NODE_NOFAILOVER 512 /* Slave will not try to failover. */
#define CLUSTER_NODE_NULL_NAME "\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000"

#define nodeIsMaster(n) ((n)->flags & CLUSTER_NODE_MASTER)
#define nodeIsSlave(n) ((n)->flags & CLUSTER_NODE_SLAVE)
#define nodeInHandshake(n) ((n)->flags & CLUSTER_NODE_HANDSHAKE)
#define nodeHasAddr(n) (!((n)->flags & CLUSTER_NODE_NOADDR))
#define nodeWithoutAddr(n) ((n)->flags & CLUSTER_NODE_NOADDR)
#define nodeTimedOut(n) ((n)->flags & CLUSTER_NODE_PFAIL)
#define nodeFailed(n) ((n)->flags & CLUSTER_NODE_FAIL)
#define nodeCantFailover(n) ((n)->flags & CLUSTER_NODE_NOFAILOVER)

/* Reasons why a slave is not able to failover. */
#define CLUSTER_CANT_FAILOVER_NONE 0
#define CLUSTER_CANT_FAILOVER_DATA_AGE 1
#define CLUSTER_CANT_FAILOVER_WAITING_DELAY 2
#define CLUSTER_CANT_FAILOVER_EXPIRED 3
#define CLUSTER_CANT_FAILOVER_WAITING_VOTES 4
#define CLUSTER_CANT_FAILOVER_RELOG_PERIOD (60*5) /* seconds. */

/* clusterState todo_before_sleep flags. */
#define CLUSTER_TODO_HANDLE_FAILOVER (1<<0)
#define CLUSTER_TODO_UPDATE_STATE (1<<1)
#define CLUSTER_TODO_SAVE_CONFIG (1<<2)
#define CLUSTER_TODO_FSYNC_CONFIG (1<<3)
#define CLUSTER_TODO_HANDLE_MANUALFAILOVER (1<<4)

/* Message types.
 *
 * Note that the PING, PONG and MEET messages are actually the same exact
 * kind of packet. PONG is the reply to ping, in the exact format as a PING,
 * while MEET is a special PING that forces the receiver to add the sender
 * as a node (if it is not already in the list). */
#define CLUSTERMSG_TYPE_PING 0          /* Ping */
#define CLUSTERMSG_TYPE_PONG 1          /* Pong (reply to Ping) */
#define CLUSTERMSG_TYPE_MEET 2          /* Meet "let's join" message */
#define CLUSTERMSG_TYPE_FAIL 3          /* Mark node xxx as failing */
#define CLUSTERMSG_TYPE_PUBLISH 4       /* Pub/Sub Publish propagation */
#define CLUSTERMSG_TYPE_FAILOVER_AUTH_REQUEST 5 /* May I failover? */
#define CLUSTERMSG_TYPE_FAILOVER_AUTH_ACK 6     /* Yes, you have my vote */
#define CLUSTERMSG_TYPE_UPDATE 7        /* Another node slots configuration */
#define CLUSTERMSG_TYPE_MFSTART 8       /* Pause clients for manual failover */
#define CLUSTERMSG_TYPE_MODULE 9        /* Module cluster API message. */
#define CLUSTERMSG_TYPE_COUNT 10        /* Total number of message types. */

/* Flags that a module can set in order to prevent certain Redis Cluster
 * features to be enabled. Useful when implementing a different distributed
 * system on top of Redis Cluster message bus, using modules. */
#define CLUSTER_MODULE_FLAG_NONE 0
#define CLUSTER_MODULE_FLAG_NO_FAILOVER (1<<1)
#define CLUSTER_MODULE_FLAG_NO_REDIRECTION (1<<2)

/* This structure represent elements of node->fail_reports. */
/* 每个节点都保存着一条对其他节点的下线报告 */
typedef struct clusterNodeFailReport {
    // 报告目标节点已经下线
    struct clusterNode *node;  /* Node reporting the failure condition. */
    // 最后一次和目标节点通信的时间，程序员用这个时间来判断节点是否下线
    mstime_t time;             /* Time of the last report from this node. */
} clusterNodeFailReport;

typedef struct clusterNode {
    mstime_t ctime; /* Node object creation time. */
    char name[CLUSTER_NAMELEN]; /* Node name, hex string, sha1-size */
    /* 使用不同的标识记录着节点的角色，以及目前所处的状态 */
    int flags;      /* CLUSTER_NODE_... */
    /* 节点当前的配置纪元，用于实现节点的故障转移 */
    uint64_t configEpoch; /* Last configEpoch observed for this node */
    /* 由这个节点负责处理的槽
     * 一共有CLUSTER_SLOTS / 8 个字节长 （一个字节有 8 bits）
     * 每个字节的每个位记录了一个槽的保存状态
     * 位的值为1,表示槽正由本节点处理，值为0则表示槽并非本节点处理*/
    unsigned char slots[CLUSTER_SLOTS/8]; /* slots handled by this node */
    sds slots_info; /* Slots info represented by string. */
    /* 该节点负责处理的槽数量 */
    int numslots;   /* Number of slots handled by this node */
    /* 如果本节点是主节点，用这个节点记录从节点的数量 */
    int numslaves;  /* Number of slave nodes, if this is a master */
    // 从节点指针数组
    struct clusterNode **slaves; /* pointers to slave nodes */
    // 指向主节点，如果没有主节点则该值可以为NULL
    struct clusterNode *slaveof; /* pointer to the master node. Note that it
                                    may be NULL even if the node is a slave
                                    if we don't have the master node in our
                                    tables. */
    mstime_t ping_sent;      /* Unix time we sent latest ping */
    mstime_t pong_received;  /* Unix time we received the pong */
    mstime_t data_received;  /* Unix time we received any data */
    mstime_t fail_time;      /* Unix time when FAIL flag was set */
    mstime_t voted_time;     /* Last time we voted for a slave of this master */
    mstime_t repl_offset_time;  /* Unix time we received offset for this node */
    mstime_t orphaned_time;     /* Starting time of orphaned master condition */
    // 该节点已知的复制偏移量
    long long repl_offset;      /* Last known repl offset for this node. */
    // ip地址
    char ip[NET_IP_STR_LEN];  /* Latest known IP address of this node */
    // 节点的端口号
    int port;                   /* Latest known clients port of this node */
    int cport;                  /* Latest known cluster port of this node. */
    // 与该节点关联的连接对象
    clusterLink *link;          /* TCP/IP link with this node */
    // 保存下线报告的链表
    list *fail_reports;         /* List of nodes signaling this as failing */
} clusterNode;

/* 集群状态，每个节点都保存着一个这样的状态，记录了它们眼中的集群的样子*/
typedef struct clusterState {
    /* 指向当前节点的指针 */
    clusterNode *myself;  /* This node */
    /* 集群当前配置纪元，用于实现故障转移 */
    uint64_t currentEpoch;
    int state;            /* CLUSTER_OK, CLUSTER_FAIL, ... */
    /* 集群中至少处理一个槽的节点的数量 */
    int size;             /* Num of master nodes with at least one slot */
    // 保存集群节点的字典，键时节点名称，值是clusterNode结构的指针
    dict *nodes;          /* Hash table of name -> clusterNode structures */
    /* 集群节点黑名单（包括myself）
     * 防止被forget的命令重新添加到集群里面*/
    dict *nodes_black_list; /* Nodes we don't re-add for a few seconds. */
    /* 记录要从当前节点迁移到目标节点的槽，以及迁移的目标节点 */
    clusterNode *migrating_slots_to[CLUSTER_SLOTS];
    clusterNode *importing_slots_from[CLUSTER_SLOTS];
    /* 负责处理各个槽的节点 */
    clusterNode *slots[CLUSTER_SLOTS];
    uint64_t slots_keys_count[CLUSTER_SLOTS];
    // 槽映射到键的radix结构
    rax *slots_to_keys;
    /* The following fields are used to take the slave state on elections. */
    // 之前或下一次选举的时间
    mstime_t failover_auth_time; /* Time of previous or next election. */
    // 节点获得支持的票数
    int failover_auth_count;    /* Number of votes received so far. */
    // 如果为真，表示本节点已经向其他节点发送了投票请求
    int failover_auth_sent;     /* True if we already asked for votes. */
    // 该从节点在当前请求中的排名
    int failover_auth_rank;     /* This slave rank for current auth request. */
    // 当前选举的纪元
    uint64_t failover_auth_epoch; /* Epoch of the current election. */
    // 从节点不能执行故障转移的原因
    int cant_failover_reason;   /* Why a slave is currently not able to
                                   failover. See the CANT_FAILOVER_* macros. */
    /* Manual failover state in common. */
    // 为0表示没有正在进行手动的故障转移，否则表示手动故障转移时间限制
    mstime_t mf_end;            /* Manual failover time limit (ms unixtime).
                                   It is zero if there is no MF in progress. */
    /* Manual failover state of master. */
    // 执行手动转移的从节点
    clusterNode *mf_slave;      /* Slave performing the manual failover. */
    /* Manual failover state of slave. */
    // 从节点记录手动故障转移是的主节点偏移量
    long long mf_master_offset; /* Master offset the slave needs to start MF
                                   or zero if still not received. */
    // 非零表示手动故障转移能开始
    int mf_can_start;           /* If non-zero signal that the manual failover
                                   can start requesting masters vote. */
    /* The following fields are used by masters to take state on elections. */
    // 集群最近一次投票的纪元
    uint64_t lastVoteEpoch;     /* Epoch of the last vote granted. */
    // 调用clusterBeforeSleep()所做的一些事
    int todo_before_sleep; /* Things to do in clusterBeforeSleep(). */
    /* Messages received and sent by type. */
    // 发送的字节数
    long long stats_bus_messages_sent[CLUSTERMSG_TYPE_COUNT];
    // 通过cluster接收到的消息数量
    long long stats_bus_messages_received[CLUSTERMSG_TYPE_COUNT];
    long long stats_pfail_nodes;    /* Number of nodes in PFAIL status,
                                       excluding nodes without address. */
} clusterState;

/* Redis cluster messages header */

/* Initially we don't know our "name", but we'll find it once we connect
 * to the first node, using the getsockname() function. Then we'll use this
 * address for all the next messages. */
typedef struct {
    char nodename[CLUSTER_NAMELEN];
    uint32_t ping_sent;
    uint32_t pong_received;
    char ip[NET_IP_STR_LEN];  /* IP address last time it was seen */
    uint16_t port;              /* base port last time it was seen */
    uint16_t cport;             /* cluster port last time it was seen */
    uint16_t flags;             /* node->flags copy */
    uint32_t notused1;
} clusterMsgDataGossip;

typedef struct {
    char nodename[CLUSTER_NAMELEN];
} clusterMsgDataFail;

typedef struct {
    uint32_t channel_len;
    uint32_t message_len;
    unsigned char bulk_data[8]; /* 8 bytes just as placeholder. */
} clusterMsgDataPublish;

typedef struct {
    uint64_t configEpoch; /* Config epoch of the specified instance. */
    char nodename[CLUSTER_NAMELEN]; /* Name of the slots owner. */
    unsigned char slots[CLUSTER_SLOTS/8]; /* Slots bitmap. */
} clusterMsgDataUpdate;

typedef struct {
    uint64_t module_id;     /* ID of the sender module. */
    uint32_t len;           /* ID of the sender module. */
    uint8_t type;           /* Type from 0 to 255. */
    unsigned char bulk_data[3]; /* 3 bytes just as placeholder. */
} clusterMsgModule;

union clusterMsgData {
    /* PING, MEET and PONG */
    struct {
        /* Array of N clusterMsgDataGossip structures */
        clusterMsgDataGossip gossip[1];
    } ping;

    /* FAIL */
    struct {
        clusterMsgDataFail about;
    } fail;

    /* PUBLISH */
    struct {
        clusterMsgDataPublish msg;
    } publish;

    /* UPDATE */
    struct {
        clusterMsgDataUpdate nodecfg;
    } update;

    /* MODULE */
    struct {
        clusterMsgModule msg;
    } module;
};

#define CLUSTER_PROTO_VER 1 /* Cluster bus protocol version. */

typedef struct {
    char sig[4];        /* Signature "RCmb" (Redis Cluster message bus). */
    uint32_t totlen;    /* Total length of this message */
    uint16_t ver;       /* Protocol version, currently set to 1. */
    uint16_t port;      /* TCP base port number. */
    uint16_t type;      /* Message type */
    uint16_t count;     /* Only used for some kind of messages. */
    uint64_t currentEpoch;  /* The epoch accordingly to the sending node. */
    uint64_t configEpoch;   /* The config epoch if it's a master, or the last
                               epoch advertised by its master if it is a
                               slave. */
    uint64_t offset;    /* Master replication offset if node is a master or
                           processed replication offset if node is a slave. */
    char sender[CLUSTER_NAMELEN]; /* Name of the sender node */
    unsigned char myslots[CLUSTER_SLOTS/8];
    char slaveof[CLUSTER_NAMELEN];
    char myip[NET_IP_STR_LEN];    /* Sender IP, if not all zeroed. */
    char notused1[34];  /* 34 bytes reserved for future usage. */
    uint16_t cport;      /* Sender TCP cluster bus port */
    uint16_t flags;      /* Sender node flags */
    unsigned char state; /* Cluster state from the POV of the sender */
    unsigned char mflags[3]; /* Message flags: CLUSTERMSG_FLAG[012]_... */
    union clusterMsgData data;
} clusterMsg;

#define CLUSTERMSG_MIN_LEN (sizeof(clusterMsg)-sizeof(union clusterMsgData))

/* Message flags better specify the packet content or are used to
 * provide some information about the node state. */
#define CLUSTERMSG_FLAG0_PAUSED (1<<0) /* Master paused for manual failover. */
#define CLUSTERMSG_FLAG0_FORCEACK (1<<1) /* Give ACK to AUTH_REQUEST even if
                                            master is up. */

/* ---------------------- API exported outside cluster.c -------------------- */
clusterNode *getNodeByQuery(client *c, struct redisCommand *cmd, robj **argv, int argc, int *hashslot, int *ask);
int clusterRedirectBlockedClientIfNeeded(client *c);
void clusterRedirectClient(client *c, clusterNode *n, int hashslot, int error_code);
unsigned long getClusterConnectionsCount(void);

#endif /* __CLUSTER_H */
