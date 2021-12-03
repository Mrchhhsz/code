### 生产者发送消息整体流程

1. KafkaProducer 是生产者的入口，也是主线程，它还维护Sender子线程
2. 在主线程中，不断往RecordAccumulator中追加消息
3. RecordAccumulator 是一个消息的仓库，当有消息Batch封箱完成时，KafkaProducer会唤醒Sender线程做消息的发送处理
4. sender首先把batch按照要发往的Node分组，生成ClientRequest请求对象
5. Sender 再通过NetworkClient的send方法，把ClientRequest需要的资源准备好，如Channel，数据等
6. Sender 最后通过NetworkClient的poll方法，底层通过nio把准备好的请求最终发送出去
7. Sender再统一处理Response，进行重试或者回调