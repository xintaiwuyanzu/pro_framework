package com.dr.framework.common.page;

import java.util.List;

/**
 * 分页工具类
 *
 * @author dr-hls
 */
public class Page<T> {
    /**
     * 默认分页大小
     */
    public static final long DEFAULT_PAGE_SIZE = 15;

    /**
     * 默认最大分页数量 为10w
     */
    private static volatile int DEFAULT_MAX_PAGE_SIZE = 100000;

    public static void setMaxPageSize(int size) {
        DEFAULT_MAX_PAGE_SIZE = size;
    }

    public static int getMaxPageSize() {
        return DEFAULT_MAX_PAGE_SIZE;
    }

    /**
     * 起始页，每页分页大小，总共几页
     */
    private long start,
            size = DEFAULT_PAGE_SIZE,
            total;
    private List<T> data;
    private Object other;

    public Page(long start, long size, long total) {
        this.start = start;
        this.size = size;
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Object getOther() {
        return other;
    }

    public void setOther(Object other) {
        this.other = other;
    }
}
