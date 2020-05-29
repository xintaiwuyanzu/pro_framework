package com.dr.framework.common.entity;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Table;

@Table(name = "common_file", comment = "通用附件", module = "common")
public class File extends BaseStatusEntity<Integer> {
    @Column(name = "file_length", comment = "文件长度", length = 50)
    private Long length;
    @Column(name = "file_name", comment = "文件原始名称", length = 100)
    private String name;
    @Column(name = "suffix", comment = "文件后缀", length = 10)
    private String suffix;
    @Column(name = "file_type", comment = "文件类型，用来前台显示", simple = "文件类型", length = 10)
    private String type;
    @Column(name = "parent_file", comment = "文件保存相对父路径", length = 100)
    private String parentFile;
    @Column(name = "file_description", comment = "文件描述", length = 500)
    private String description;

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentFile() {
        return parentFile;
    }

    public void setParentFile(String parentFile) {
        this.parentFile = parentFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
