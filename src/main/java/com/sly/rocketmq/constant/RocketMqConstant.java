package com.sly.rocketmq.constant;

/**
 * rocket 常量
 *
 * @author SLY
 * @date 2020/10/29
 */
public class RocketMqConstant {
    /**
     * NameServer地址,个地址以;隔开
     */
    public static final String NAME_SERVER = "192.168.100.101:9876";
    /**
     * DEMO生产者的组名
     */
    public static final String DEMO_PRODUCER_GROUP = "demo_producer_group";
    /**
     * DEMO消费者的组名
     */
    public static final String DEMO_CUSTOMER_GROUP = "demo_customer_group";

    /**
     * 会话名称
     */
    public static final String DEMO_TOPIC = "demo_topic";


    /**
     * DEMO生产者的组名
     */
    public static final String DELAY_PRODUCER_GROUP = "delay_producer_group";
    /**
     * 延迟会话名称
     */
    public static final String DELAY_TOPIC = "delay_topic";

    /**
     * 最小延时等级
     */
    public static final int MINI_DELAY_LEVEL = 3;

}
