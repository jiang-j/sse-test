package com.jiangj.sse.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Jiang Jian
 * @since 2018/7/23
 */
@Component
public class Subscribe {

    @Autowired
    RedisService redisService;

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    /**
     * 订阅频道
     *
     * @param channel          频道
     * @param redisMsgPubSubListener
     */
    public void subscribeChannel(final String channel, final RedisMsgPubSubListener redisMsgPubSubListener) {

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                redisService.subscribe(redisMsgPubSubListener, channel);
            }
        });
    }
}
