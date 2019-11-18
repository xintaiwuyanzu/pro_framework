package com.dr.framework.core.event;

/**
 * 基础的创建、更新，删除事件
 *
 * @param <T>
 */
public class BaseCRUDEvent<T> extends BaseEvent<T> {


    public enum EventType {
        CREATE,
        UPDATE,
        DELETE
    }

    private EventType eventType;

    private T old;

    public BaseCRUDEvent(T source, T old, EventType eventType) {
        super(source);
        this.eventType = eventType;
        this.old = old;
    }

    public BaseCRUDEvent(T source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public T getOld() {
        return old;
    }

    public void setOld(T old) {
        this.old = old;
    }


}
