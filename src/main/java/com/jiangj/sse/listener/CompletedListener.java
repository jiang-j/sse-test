package com.jiangj.sse.listener;

import com.jiangj.sse.SseController;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by jiangjian on 2018/7/19.
 */
@Component
public class CompletedListener {

    @Autowired
    ApplicationContext applicationContext;

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletedListener.class);

    private static ConcurrentHashMap<Long,SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    ScheduledExecutorService service = new ScheduledThreadPoolExecutor(200,
            new BasicThreadFactory.Builder().namingPattern("demo-pool-%d").daemon(true).build());

    public void addSseEmitters(Long recordId, SseEmitter sseEmitter) {
        sseEmitters.put(recordId, sseEmitter);
//        service.schedule(()->{
//            applicationContext.publishEvent(new CompletedEvent(this,recordId));
//        },3, TimeUnit.SECONDS);
    }

    @EventListener
    public void deployEventHandler(CompletedEvent completedEvent) throws IOException {
        Long recordId = completedEvent.getRecordId();
        SseEmitter sseEmitter = sseEmitters.get(recordId);

        if (null != sseEmitter){
            sseEmitters.remove(recordId);
            SseController.subOnlineCount();
            LOGGER.info("回调成功：{}",recordId);
            sseEmitter.send("成功:"+recordId);
            sseEmitter.complete();
        }
    }


}
