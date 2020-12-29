package com.sly.rocketmq.controller;

import com.alibaba.fastjson.JSON;
import com.sly.rocketmq.constant.RocketMqConstant;
import com.sly.rocketmq.model.DataModel;
import com.sly.rocketmq.producer.DelayProducer;
import com.sly.rocketmq.producer.DemoProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * demo controller
 *
 * @author SLY
 * @date 2020/10/29
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    private static Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Resource
    private DemoProducer demoProducer;
    @Resource
    private DelayProducer delayProducer;

    /**
     * 发送消息
     *
     * @param text   内容
     * @param userId 用户ID
     * @return java.lang.Object
     * @author SLY
     * @date 2020/10/29
     */
    @RequestMapping("/sendMessage")
    public Object sendMessage(String text, String userId) throws Exception {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(userId)) {
            return "参数错误！";
        }
        // 创建消息:主题、二级分类、消息内容好的字节数组
        Message message = new Message(RocketMqConstant.DEMO_TOPIC, userId, ("hello rocketMQ " + text + JSON.toJSONString(new Date())).getBytes());

        SendResult result = demoProducer.getProducer().send(message, 1000);
        logger.info("发送返回:" + JSON.toJSONString(result));
        return "发送成功！";
    }

    /**
     * 发送消息
     *
     * @param text   内容
     * @param userId 用户ID
     * @return java.lang.Object
     * @author SLY
     * @date 2020/10/29
     */
    @RequestMapping("/sendDelayMessage")
    public Object sendDelayMessage(String text, String userId, Long delaySecond) throws Exception {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(userId) || delaySecond == null) {
            return "参数错误！";
        }
        DataModel dataModel = new DataModel();
        dataModel.setMessage(text);
        // 延时1:40
        Date delayDate = new Date(System.currentTimeMillis() + delaySecond * 1000);
        dataModel.setDelayDate(delayDate);
        dataModel.setTags(userId);
        dataModel.setTopic(RocketMqConstant.DEMO_TOPIC);
        delayProducer.sendDelay(dataModel, delayDate);
        return "发送成功！";
    }
}
