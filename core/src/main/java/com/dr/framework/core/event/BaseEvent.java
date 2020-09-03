package com.dr.framework.core.event;

import java.util.EventObject;

/**
 * 事件处理入口，所有平台相关的事件都需要继承本类
 * 后续可以扩招到jms和event
 * <p>
 * <p>
 * TODO 暂时只是一个入口，占个坑，没想到具体的实现方式
 *
 * @author dr
 */
public class BaseEvent<T> extends EventObject {

    public BaseEvent(T source) {
        super(source);
    }

    @Override
    public T getSource() {
        return (T) super.getSource();
    }
}
