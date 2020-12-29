package com.sly.rocketmq.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据model
 *
 * @author SLY
 * @date 2020/10/30
 */
public class DataModel {
    private String tags;
    private String topic;
    private Date delayDate;
    private String message;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getDelayDate() {
        return delayDate;
    }

    public void setDelayDate(Date delayDate) {
        this.delayDate = delayDate;
    }

    public void setDelayDate(String delayDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.delayDate = simpleDateFormat.parse(delayDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
