package com.jiangj.sse;

import com.jiangj.sse.listener.CompletedEvent;
import com.jiangj.sse.listener.CompletedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**
 * Created by jiangjian on 2018/7/19.
 */
@RestController
public class SseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SseController.class);

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CompletedListener completedListener;

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    volatile private static int onlineCount = 0;

    volatile private static int allOnlineCount = 0;

    @GetMapping("/queryByRecordId")
    public SseEmitter push(@RequestParam Long recordId){
        final SseEmitter emitter = new SseEmitter(0L);//timeout设置为0表示不超时
        try {
            taskExecutor.execute(() ->{
                addOnlineCount();
                addAllOnlineCount();
                LOGGER.info("当前查询：{},连接数：{}",recordId,getOnlineCount());
                LOGGER.info("当前查询：{},总连接数：{}",recordId,getAllOnlineCount());
                completedListener.addSseEmitters(recordId,emitter);
            });
        }catch (Exception e){
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @GetMapping("/callback")
    public String payCallback(@RequestParam Long recordId){
        applicationContext.publishEvent(new CompletedEvent(this,recordId));
        return "请到监听处查看消息";

    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SseController.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SseController.onlineCount--;
    }

    public static synchronized void addAllOnlineCount() {
        SseController.allOnlineCount++;
    }

    public static synchronized int getAllOnlineCount() {
        return allOnlineCount;
    }

}
