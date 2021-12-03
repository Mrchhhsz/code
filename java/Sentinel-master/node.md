## sentinel
![https://www.cnblogs.com/starcrm/p/12919456.html]
![https://github.com/all4you/sentinel-tutorial]
1. 隔离策略：信号量隔离（线程并发数控制），当某个资源堆积的请求 
2. 熔断降级策略：基于响应时间，异常比率，异常数
3. 实时统计实现：基于滑动窗口 LeapArray
4. 动态规则配置：支持多数据源
5. 限流：基于QPS，支持基于调用关系的限流
6. 流量整形：支持预热模式，匀速器模式，预热排队模式
7. 系统自适应保护：
8. 控制台：
9. 扩展性：


Field	说明	默认值
resource	资源名，资源名是限流规则的作用对象	 
count	限流阈值	 
grade	限流阈值类型，QPS 或线程数模式	QPS 模式
limitApp	流控针对的调用来源	default，代表不区分调用来源
strategy	调用关系限流策略：直接、链路、关联	根据资源本身（直接）
controlBehavior	流控效果（直接拒绝 / 排队等待 / 慢启动模式），不支持按调用关系限流	直接拒绝