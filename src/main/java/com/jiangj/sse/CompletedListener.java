package com.jiangj.sse;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by jiangjian on 2018/7/19.
 */
@Component
public class CompletedListener {

    private static Map<Long,SseEmitter> sseEmitters = new Hashtable<>();

    public void addSseEmitters(Long recordId, SseEmitter sseEmitter) {
        sseEmitters.put(recordId, sseEmitter);
    }

    @EventListener
    public void deployEventHandler(CompletedEvent completedEvent) throws IOException {
        Long recordId = completedEvent.getRecordId();
        SseEmitter sseEmitter = sseEmitters.get(recordId);
        sseEmitter.send("回调成功");
        sseEmitter.complete();

    }
}
