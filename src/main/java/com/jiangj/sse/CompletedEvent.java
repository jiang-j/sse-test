package com.jiangj.sse;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Created by jiangjian on 2018/7/19.
 */
public class CompletedEvent extends ApplicationEvent {

    private Long recordId;

    public CompletedEvent(Object source, Long recordId) {
        super(source);
        this.recordId = recordId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
}
