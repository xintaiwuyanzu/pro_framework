package com.dr.framework.common.config.service.impl;

import com.dr.framework.common.config.annotations.Config;
import com.dr.framework.common.config.annotations.Value;
import com.dr.framework.common.config.entity.CommonConfig;
import com.dr.framework.common.config.service.CommonConfigBeanFactory;
import com.dr.framework.common.entity.BaseDescriptionEntity;
import com.dr.framework.common.entity.StatusEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dr
 */
public class DefaultCommonConfigBeanFactory implements CommonConfigBeanFactory {
    /**
     * 可以转换的类
     */
    List<Class> canConvertClass = Arrays.asList(String.class, Long.class, long.class, int.class, Integer.class);

    Logger logger = LoggerFactory.getLogger(DefaultCommonConfigBeanFactory.class);


    @Override
    public <T> List<CommonConfig> newConfigs(T configBean) {
        Class<T> beanClass = (Class<T>) configBean.getClass();

        //先查看有多少套code
        Set<String> codes = new HashSet<>();

        String classCode = null;
        String defaultType = null;
        if (beanClass.isAnnotationPresent(Config.class)) {
            Config config = beanClass.getAnnotation(Config.class);
            classCode = config.code();
            defaultType = config.type();
            if (StringUtils.isEmpty(defaultType)) {
                defaultType = config.value();
            }
        }

        String finalClassCode = classCode;
        ReflectionUtils.doWithFields(beanClass, field -> {
            if (field.isAnnotationPresent(Value.class)) {
                Value value = field.getAnnotation(Value.class);
                if (StringUtils.isEmpty(value.code())) {
                    codes.add(finalClassCode);
                } else {
                    codes.add(value.code());
                }
            }
        });
        //根据code初始化对象
        Map<String, CommonConfig> commonConfigCodeMap = codes.stream()
                .collect(Collectors.toMap(c -> c, c -> new CommonConfig()));
        //绑定数据
        ReflectionUtils.doWithFields(beanClass, f -> bindConfigField(commonConfigCodeMap, f, configBean));

        String finalDefaultType = defaultType;
        commonConfigCodeMap.entrySet().forEach(e -> {
            CommonConfig config = e.getValue();
            if (StringUtils.isEmpty(config.getCode())) {
                config.setCode(e.getKey());
            }
            if (StringUtils.isEmpty(config.getStatus())) {
                config.setStatus(StatusEntity.STATUS_ENABLE_STR);
            }
            if (StringUtils.isEmpty(config.getType()) && !StringUtils.isEmpty(finalDefaultType)) {
                config.setType(finalDefaultType);
            }
        });
        return new ArrayList<>(commonConfigCodeMap.values());
    }

    protected <T> void bindConfigField(Map<String, CommonConfig> commonConfigCodeMap, Field field, T bean) {
        //TODO 这里可能会把code  type  refid等信息覆盖掉
        FieldInfo info = getFieldInfo(field, bean.getClass());
        CommonConfig config = commonConfigCodeMap.get(info.code);
        if (config != null) {
            Field configField = ReflectionUtils.findField(CommonConfig.class, info.name);
            if (configField != null) {
                Object value = getFieldValue(field, bean);
                if (canConvertClass.contains(configField.getType())) {
                    bindFieldValue(configField, config, value);
                } else if (value != null && configField.getType() != CommonConfig.class) {
                    //复杂类型
                    newConfigs(value).forEach(
                            c -> {
                                if (c.getCode().equalsIgnoreCase(info.code)) {
                                    BeanUtils.copyProperties(c, config);
                                }
                            }
                    );
                }
                //TODO  字段是CommonCOnfig的类型的时候
            } else {
                logger.warn("未找到字段" + field + "对应的config的字段");
            }
        } else {
            logger.warn("未找到字段" + field + "对应的config");
        }
    }

    /**
     * ============================分割线，下面的方法都是下面用的==========================================
     *
     * @param configs     查询出来的
     * @param configClass
     * @param <T>
     * @return
     */
    @Override
    public <T> List<T> newConfigBean(List<CommonConfig> configs, Class<T> configClass) {
        if (configs == null || configs.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<T> configBeans = new ArrayList<>();
            doNewConfigBean(configs, configClass, configBeans);
            return configBeans;
        }
    }


    private <T> void doNewConfigBean(List<CommonConfig> configs, Class<T> beanClass, List<T> configBeans) {
        Set<String> codes = configs.stream()
                .map(BaseDescriptionEntity::getType)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        String configClassType = "";
        if (beanClass.isAnnotationPresent(Config.class)) {
            Config config = beanClass.getAnnotation(Config.class);
            configClassType = config.type().toUpperCase();
        }
        //需要绑定的code
        Set<String> bindTypes = new HashSet<>();
        if (codes.contains(configClassType)) {
            //如果查出来的数据种的code能够跟实体类上的注解完全相同，则是绑定一个bean
            bindTypes.add(configClassType);
        } else {
            //如果查询出来多个，则是code  like查询
            bindTypes.addAll(codes);
        }
        //开始绑定
        bindTypes.forEach(t -> {
            T bean = bindBean(
                    configs.stream()
                            .filter(commonConfig -> commonConfig.getType().equalsIgnoreCase(t))
                            .collect(Collectors.toList()),
                    beanClass
            );
            if (bean != null) {
                configBeans.add(bean);
            }
        });
    }

    /**
     * 创建bean
     *
     * @param <T>
     * @param commonConfigs
     * @param beanClass
     * @return
     */
    protected <T> T bindBean(Collection<CommonConfig> commonConfigs, Class<T> beanClass) {
        Map<String, CommonConfig> commonConfigCodeMap = commonConfigs.stream()
                .collect(Collectors.toMap(c -> c.getCode().toUpperCase(), commonConfig -> commonConfig));
        Assert.isTrue(commonConfigCodeMap.size() == commonConfigs.size(), "检测到多条相同type的数据");
        T bean = null;
        try {
            bean = beanClass.newInstance();
            T finalBean = bean;
            ReflectionUtils.doWithFields(beanClass, f -> bindField(finalBean, f, commonConfigCodeMap));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("创建" + beanClass + "对象失败", e);
        }
        return bean;
    }

    /**
     * 绑定字段
     *
     * @param bean
     * @param field
     * @param commonConfigCodeMap
     * @param <T>
     */
    protected <T> void bindField(T bean, Field field, Map<String, CommonConfig> commonConfigCodeMap) {
        FieldInfo info = getFieldInfo(field, bean.getClass());
        CommonConfig commonConfig = commonConfigCodeMap.get(info.code);
        if (commonConfig == null) {
            logger.warn("未找到字段" + field + "对应的数据");
            return;
        }
        Class fieldClass = field.getType();
        Object fieldValue = null;
        if (canConvertClass.contains(fieldClass)) {
            Field configField = ReflectionUtils.findField(CommonConfig.class, info.name);
            if (configField == null) {
                logger.warn("未找到字段" + field + "在通用配置类种对应的字段");
            } else {
                fieldValue = getFieldValue(configField, commonConfig);
            }
        } else if (fieldClass.equals(CommonConfig.class)) {
            fieldValue = commonConfig;
        } else {
            fieldValue = bindBean(commonConfigCodeMap.values(), fieldClass);
        }
        try {
            bindFieldValue(field, bean, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException("设置字段" + field + "值失败！", e);
        }
    }

    protected FieldInfo getFieldInfo(Field field, Class beanClass) {
        String fieldCode = null;
        String fieldName = null;
        if (field.isAnnotationPresent(Value.class)) {
            Value value = field.getAnnotation(Value.class);
            fieldCode = value.code().toUpperCase();

            Value.Name name1 = value.name();
            Value.Name name2 = value.value();
            fieldName = name1.equals(name2) ? name1.name() : (name1.equals(Value.Name.AUTO) ? name2.name() : name1.name());
        }
        if (StringUtils.isEmpty(fieldName) || "AUTO".equalsIgnoreCase(fieldName)) {
            fieldName = field.getName();
        }
        if (StringUtils.isEmpty(fieldCode) && beanClass.isAnnotationPresent(Config.class)) {
            //如果字段里面没有声明，则从类上面找
            Config config = (Config) beanClass.getAnnotation(Config.class);
            fieldCode = config.code().toUpperCase();
        }
        return new FieldInfo(fieldName, fieldCode);
    }

    protected Object getFieldValue(Field field, Object target) {
        field.setAccessible(true);
        return ReflectionUtils.getField(field, target);
    }

    protected void bindFieldValue(Field field, Object target, Object value) {
        if (value != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, target, value);
        }
    }


    class FieldInfo {
        String name;
        String code;

        public FieldInfo(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }

}
