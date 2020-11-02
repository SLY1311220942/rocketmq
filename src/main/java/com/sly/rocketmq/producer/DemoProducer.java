package com.sly.rocketmq.producer;

import com.sly.rocketmq.constant.RocketMqConstant;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

/**
 * demo 生产者
 *
 * @author SLY
 * @date 2020/10/27
 */
@Component
public class DemoProducer {

    private DefaultMQProducer producer;

    public DemoProducer() {
        producer = new DefaultMQProducer(RocketMqConstant.DEMO_PRODUCER_GROUP);
        // 指定nameServer地址,多个地址之间以 ; 隔开
        producer.setNamesrvAddr(RocketMqConstant.NAME_SERVER);
        start();
    }

    /**
     * 对象在使用之前必须调用一次,并且只能初始化一次
     */
    public void start() {
        try {
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public DefaultMQProducer getProducer() {
        return producer;
    }

    /**
     * 一般在应用上下文,使用上下文监听器,进行关闭
     */
    public void shutdown() {
        producer.shutdown();
    }
}
