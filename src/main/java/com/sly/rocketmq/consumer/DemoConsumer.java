package com.sly.rocketmq.consumer;

import com.sly.rocketmq.constant.RocketMqConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * demo 消费者
 *
 * @author SLY
 * @date 2020/10/29
 */
@Component
public class DemoConsumer {

    private static Logger logger = LoggerFactory.getLogger(DemoConsumer.class);

    public DemoConsumer() throws MQClientException {
        //消费者的组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RocketMqConstant.DEMO_CUSTOMER_GROUP);
        //指定NameServer地址
        consumer.setNamesrvAddr(RocketMqConstant.NAME_SERVER);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        //订阅PushTopic下Tag为push的消息
        consumer.subscribe(RocketMqConstant.DEMO_TOPIC, "*");
        //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        //如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            try {
                for (MessageExt messageExt : list) {
                    //输出消息内容
                    String messageExtTags = messageExt.getTags();
                    String messageBody = new String(messageExt.getBody());
                    logger.info("消费响应：msgId : " + messageExt.getMsgId() + ",  userId : " + messageExtTags + ",  msgBody : " + messageBody);//输出消息内容
                }
            } catch (Exception e) {
                logger.info("异常:", e);
                //稍后再试
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            //消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();

    }
}
