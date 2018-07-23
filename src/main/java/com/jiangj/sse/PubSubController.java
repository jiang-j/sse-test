package com.jiangj.sse;

import com.jiangj.sse.listener.CompletedListener;
import com.jiangj.sse.redis.Publisher;
import com.jiangj.sse.redis.RedisMsgPubSubListener;
import com.jiangj.sse.redis.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author Jiang Jian
 * @since 2018/7/23
 */
@RestController
public class PubSubController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PubSubController.class);

    @Autowired
    RedisMsgPubSubListener redisMsgPubSubListener;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    CompletedListener completedListener;

    @Autowired
    Publisher publisher;

    @Autowired
    Subscribe subscribe;

    @GetMapping("/sub")
    public SseEmitter sub(@RequestParam Long recordId){
        final SseEmitter emitter = new SseEmitter(0L);//timeout设置为0表示不超时
        try {
            taskExecutor.execute(() ->{
                LOGGER.info("当前查询：{}",recordId);
                subscribe.subscribeChannel("test",redisMsgPubSubListener);
                completedListener.addSseEmitters(recordId,emitter);
            });
        }catch (Exception e){
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @GetMapping("/pub")
    public String pub(@RequestParam Long recordId){
        publisher.sendMessage("test",String.valueOf(recordId));
        return "请到监听处查看消息";
    }
}
