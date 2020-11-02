package com.sly.rocketmq.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 延时等级计算
 *
 * @author SLY
 * @date 2020/10/30
 */
public class DelayLevelCalculate {

    private static List<Integer> defaultLevel;

    // 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h rocketMQ自动支持18个级别 全部转换为秒
    static {
        defaultLevel = new ArrayList<>();
        defaultLevel.add(1);
        defaultLevel.add(5);
        defaultLevel.add(10);
        defaultLevel.add(30);
        defaultLevel.add(60);
        defaultLevel.add(120);
        defaultLevel.add(180);
        defaultLevel.add(240);
        defaultLevel.add(300);
        defaultLevel.add(360);
        defaultLevel.add(420);
        defaultLevel.add(480);
        defaultLevel.add(540);
        defaultLevel.add(600);
        defaultLevel.add(1200);
        defaultLevel.add(1800);
        defaultLevel.add(3600);
        defaultLevel.add(7200);
    }

    /**
     * 计算等级
     *
     * @param second 数秒
     * @return java.lang.Integer
     * @author SLY
     * @date 2020/10/30
     */
    public static Integer calculateDefault(long second) {
        if(second <= 0){
            return 0;
        }
        for (int i = defaultLevel.size() - 1; i >= 0; i--) {
            long level = second / defaultLevel.get(i);
            if (level > 0) {
                return i + 1;
            }
        }
        return 0;
    }
}
