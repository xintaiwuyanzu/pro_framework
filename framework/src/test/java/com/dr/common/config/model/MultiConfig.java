package com.dr.common.config.model;

import com.dr.framework.common.config.annotations.Config;
import com.dr.framework.common.config.annotations.Value;

@Config(type = "aaa")
public class MultiConfig {
    String wxId;
    @Value(name = Value.Name.type)
    String type;
    @Value(code = "alpay")
    String alPayId;
    String altype;
    @Value(code = "baidu")
    String baidu;
}

