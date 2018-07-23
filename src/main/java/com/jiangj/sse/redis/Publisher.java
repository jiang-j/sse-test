package com.jiangj.sse.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Jiang Jian
 * @since 2018/7/23
 */
@Component
public class Publisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);

    @Autowired
    RedisService redisService;

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 信息
     */
    public void sendMessage(final String channel, final String message) {
        cachedThreadPool.execute(() -> {
            Long publish = redisService.publish(channel, message);
            LOGGER.info("服务器在: {} 频道发布消息{} - {}", channel, message, publish);
        });
        LOGGER.info("发布线程启动:");
    }
}
