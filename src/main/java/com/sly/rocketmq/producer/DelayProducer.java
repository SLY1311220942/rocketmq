package com.sly.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.sly.rocketmq.constant.RocketMqConstant;
import com.sly.rocketmq.model.DataModel;
import com.sly.rocketmq.utils.DelayLevelCalculate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 延时 生产者
 *
 * @author SLY
 * @date 2020/10/30
 */
@Component
public class DelayProducer extends DefaultMQProducer {

    private static Logger logger = LoggerFactory.getLogger(DelayProducer.class);

    private DefaultMQPushConsumer consumer;

    public DelayProducer() throws MQClientException {
        super(RocketMqConstant.DELAY_PRODUCER_GROUP);
        super.setNamesrvAddr(RocketMqConstant.NAME_SERVER);
        this.start();
    }

    /**
     * 延时小于10s分钟的 不支持，可以设置强制参数来支持，但是会有误差
     */
    public SendResult sendDelay(DataModel dataModel, Date startSendTime) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        long duration = Duration.between(Instant.now(), startSendTime.toInstant()).getSeconds();
        int level = DelayLevelCalculate.calculateDefault(duration);

        Message msg = new Message(dataModel.getTopic(), dataModel.getTags(), dataModel.getMessage().getBytes());
        if (level > RocketMqConstant.MINI_DELAY_LEVEL) {
            // 如果大于最小延时等级，设置延时发送
            msg.setDelayTimeLevel(level);
            msg.setTopic(RocketMqConstant.DELAY_TOPIC);
            msg.setBody(JSON.toJSONString(dataModel).getBytes());
        }

        SendResult result = super.send(msg, 1000);
        String message = JSON.toJSONString(dataModel);
        if (level > RocketMqConstant.MINI_DELAY_LEVEL) {
            logger.info("延时请求:" + message);
        } else {
            logger.info("发送请求:" + message);
        }

        return result;
    }

    @Override
    public void start() throws MQClientException {
        super.start();
        initConsumer();
    }

    private void initConsumer() throws MQClientException {
        consumer = new DefaultMQPushConsumer(super.getProducerGroup());
        //指定NameServer地址
        consumer.setNamesrvAddr(RocketMqConstant.NAME_SERVER);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        //订阅PushTopic下Tag为push的消息
        consumer.subscribe(RocketMqConstant.DELAY_TOPIC, "*");
        //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        //如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
            try {
                for (MessageExt messageExt : list) {
                    // 获取数据
                    String message = new String(messageExt.getBody());
                    DataModel dataModel = JSON.parseObject(message, DataModel.class);

                    Date startSendTime = dataModel.getDelayDate();
                    long duration = Duration.between(Instant.now(), startSendTime.toInstant()).getSeconds();
                    int level = DelayLevelCalculate.calculateDefault(duration);

                    Message msg = new Message(dataModel.getTopic(), dataModel.getTags(), dataModel.getMessage().getBytes());
                    if (level > RocketMqConstant.MINI_DELAY_LEVEL) {
                        // 如果大于最小延时等级，设置延时发送
                        msg.setDelayTimeLevel(level);
                        msg.setTopic(RocketMqConstant.DELAY_TOPIC);
                        msg.setBody(JSON.toJSONString(dataModel).getBytes());
                    }
                    super.send(msg, 1000);

                    if (level > RocketMqConstant.MINI_DELAY_LEVEL) {
                        logger.info("延时请求:" + message);
                    } else {
                        logger.info("发送请求:" + message);
                    }

                }
            } catch (Exception e) {
                logger.info(ExceptionUtils.getStackTrace(e));
                //稍后再试
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            //消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
    }

}
