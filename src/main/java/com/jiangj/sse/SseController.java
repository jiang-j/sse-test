package com.jiangj.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created by jiangjian on 2018/7/19.
 */
@Controller
public class SseController {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CompletedListener completedListener;

    @GetMapping("/push")
    public SseEmitter push(@RequestParam Long recordId){
        final SseEmitter emitter = new SseEmitter();
        try {
            completedListener.addSseEmitters(recordId,emitter);
        }catch (Exception e){
            emitter.completeWithError(e);
        }
        return emitter;
    }

    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam Long recordId){
        applicationContext.publishEvent(new CompletedEvent(this,recordId));
        return "请到监听处查看消息";
    }
}
