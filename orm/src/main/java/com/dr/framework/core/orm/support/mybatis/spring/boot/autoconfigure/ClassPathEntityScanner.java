package com.dr.framework.core.orm.support.mybatis.spring.boot.autoconfigure;

import com.dr.framework.core.orm.annotations.Table;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 扫描所有包下面的entity类，只扫描不注册
 */
class ClassPathEntityScanner extends ClassPathBeanDefinitionScanner {
    private MultiDataSourceProperties dataSourceProperties;

    ClassPathEntityScanner(BeanDefinitionRegistry registry, MultiDataSourceProperties dataSourceProperties) {
        super(registry, false);
        this.dataSourceProperties = dataSourceProperties;
    }

    List<String> scan(List<String> pkgs) {
        addIncludeFilter(new EntityModuleTypeFilter(dataSourceProperties.getIncludeModules()));
        if (!dataSourceProperties.getExcludeModules().isEmpty()) {
            addExcludeFilter(new EntityModuleTypeFilter(dataSourceProperties.getExcludeModules()));
        }
        Set<BeanDefinitionHolder> definitionHolders = doScan(pkgs.toArray(new String[0]));
        return definitionHolders.stream().map(beanDefinitionHolder -> beanDefinitionHolder.getBeanDefinition().getBeanClassName()).collect(Collectors.toList());
    }


    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        //UNDO
    }

    static class EntityModuleTypeFilter implements TypeFilter {
        List<String> modules;

        public EntityModuleTypeFilter(List<String> modules) {
            this.modules = modules;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            String tableAnnName = Table.class.getName();
            if (annotationMetadata.hasAnnotation(tableAnnName)) {
                if (!modules.isEmpty()) {
                    Map<String, Object> tableAttrs = annotationMetadata.getAnnotationAttributes(tableAnnName);
                    Object moduleObj = tableAttrs.get("module");
                    if (!ObjectUtils.isEmpty(moduleObj)) {
                        return modules.contains(moduleObj.toString().toLowerCase().trim());
                    }
                } else {
                    return true;
                }
            }
            return false;
        }
    }
}
