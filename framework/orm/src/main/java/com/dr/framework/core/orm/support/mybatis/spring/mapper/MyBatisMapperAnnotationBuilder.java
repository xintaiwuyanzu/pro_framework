/**
 * Copyright 2009-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dr.framework.core.orm.support.mybatis.spring.mapper;

import com.dr.framework.common.dao.CommonMapper;
import com.dr.framework.core.orm.annotations.Mapper;
import com.dr.framework.core.orm.sql.support.SqlQuery;
import com.dr.framework.core.orm.support.mybatis.TableInfoProperties;
import com.dr.framework.core.orm.support.mybatis.page.Dialect;
import com.dr.framework.core.orm.support.mybatis.spring.MybatisConfigurationBean;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class MyBatisMapperAnnotationBuilder {

    Logger logger = LoggerFactory.getLogger(MyBatisMapperAnnotationBuilder.class);
    private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet();
    private final Set<Class<? extends Annotation>> sqlProviderAnnotationTypes = new HashSet();

    private final MybatisConfigurationBean configuration;
    private final MapperBuilderAssistant assistant;
    private final Class<?> type;

    public MyBatisMapperAnnotationBuilder(MybatisConfigurationBean configuration, Class<?> type) {
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;

        sqlAnnotationTypes.add(Select.class);
        sqlAnnotationTypes.add(Insert.class);
        sqlAnnotationTypes.add(Update.class);
        sqlAnnotationTypes.add(Delete.class);

        sqlProviderAnnotationTypes.add(SelectProvider.class);
        sqlProviderAnnotationTypes.add(InsertProvider.class);
        sqlProviderAnnotationTypes.add(UpdateProvider.class);
        sqlProviderAnnotationTypes.add(DeleteProvider.class);
    }

    public void parse() {
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            loadXmlResource();
            configuration.addLoadedResource(resource);
            assistant.setCurrentNamespace(type.getName());
            parseCache();
            parseCacheRef();
            ReflectionUtils.doWithMethods(type, method -> {
                try {
                    if (!method.isBridge() && !AbstractMapperProxy.isDefaultMethod(method)) {
                        parseStatement(method);
                    }
                } catch (IncompleteElementException e) {
                    configuration.addIncompleteMethod(new MethodResolver(this, method));
                }
            });
        }
        parsePendingMethods();
    }

    private void parsePendingMethods() {
        Collection<org.apache.ibatis.builder.annotation.MethodResolver> incompleteMethods = configuration.getIncompleteMethods();
        synchronized (incompleteMethods) {
            Iterator<org.apache.ibatis.builder.annotation.MethodResolver> iter = incompleteMethods.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolve();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadXmlResource() {
        if (!configuration.isResourceLoaded("namespace:" + type.getName())) {
            String xmlResource = type.getName().replace('.', '/') + ".xml";
            InputStream inputStream = null;
            try {
                inputStream = Resources.getResourceAsStream(type.getClassLoader(), xmlResource);
            } catch (IOException e) {
                // ignore, resource is not required
            }
            if (inputStream != null) {
                XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource, configuration.getSqlFragments(), type.getName());
                xmlParser.parse();
            }
        }
    }

    private void parseCache() {
        CacheNamespace cacheDomain = type.getAnnotation(CacheNamespace.class);
        if (cacheDomain != null) {
            Integer size = cacheDomain.size() == 0 ? null : cacheDomain.size();
            Long flushInterval = cacheDomain.flushInterval() == 0 ? null : cacheDomain.flushInterval();
            Properties props = convertToProperties(cacheDomain.properties());
            assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size, cacheDomain.readWrite(), cacheDomain.blocking(), props);
        }
    }

    private Properties convertToProperties(Property[] properties) {
        if (properties.length == 0) {
            return null;
        }
        Properties props = new Properties();
        for (Property property : properties) {
            props.setProperty(property.name(),
                    PropertyParser.parse(property.value(), configuration.getVariables()));
        }
        return props;
    }

    private void parseCacheRef() {
        CacheNamespaceRef cacheDomainRef = type.getAnnotation(CacheNamespaceRef.class);
        if (cacheDomainRef != null) {
            Class<?> refType = cacheDomainRef.value();
            String refName = cacheDomainRef.name();
            if (refType == void.class && refName.isEmpty()) {
                throw new BuilderException("Should be specified either value() or name() attribute in the @CacheNamespaceRef");
            }
            if (refType != void.class && !refName.isEmpty()) {
                throw new BuilderException("Cannot use both value() and name() attribute in the @CacheNamespaceRef");
            }
            String namespace = (refType != void.class) ? refType.getName() : refName;
            assistant.useCacheRef(namespace);
        }
    }

    private String parseResultMap(Method method, Class entityClass) {
        Class<?> returnType = getReturnType(method);
        ConstructorArgs args = method.getAnnotation(ConstructorArgs.class);
        Results results = method.getAnnotation(Results.class);
        TypeDiscriminator typeDiscriminator = method.getAnnotation(TypeDiscriminator.class);
        String resultMapId = generateResultMapName(method, entityClass);
        applyResultMap(resultMapId, returnType, entityClass, argsIf(args), resultsIf(results), typeDiscriminator);
        return resultMapId;
    }

    private String generateResultMapName(Method method, Class entityClass) {
        Results results = method.getAnnotation(Results.class);
        if (results != null && !results.id().isEmpty()) {
            return type.getName() + "." + results.id();
        }
        StringBuilder suffix = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            suffix.append("-");
            suffix.append(c.getSimpleName());
        }
        if (entityClass != null) {
            suffix.append("-");
            suffix.append(entityClass.getSimpleName());
        }
        return type.getName() + "." + method.getName() + suffix;
    }

    private void applyResultMap(String resultMapId, Class<?> returnType, Class entityClass, Arg[] args, Result[] results, TypeDiscriminator discriminator) {
        List<ResultMapping> resultMappings = new ArrayList();
        if (returnType == Object.class && entityClass != null) {
            returnType = entityClass;
        }
        applyConstructorArgs(args, returnType, resultMappings);
        applyResults(results, returnType, resultMappings);
        Discriminator disc = applyDiscriminator(resultMapId, returnType, discriminator);
        // TODO add AutoMappingBehaviour
        assistant.addResultMap(resultMapId, returnType, null, disc, resultMappings, null);
        createDiscriminatorResultMaps(resultMapId, returnType, discriminator);
    }

    private void createDiscriminatorResultMaps(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
        if (discriminator != null) {
            for (Case c : discriminator.cases()) {
                String caseResultMapId = resultMapId + "-" + c.value();
                List<ResultMapping> resultMappings = new ArrayList();
                // issue #136
                applyConstructorArgs(c.constructArgs(), resultType, resultMappings);
                applyResults(c.results(), resultType, resultMappings);
                // TODO add AutoMappingBehaviour
                assistant.addResultMap(caseResultMapId, c.type(), resultMapId, null, resultMappings, null);
            }
        }
    }

    private Discriminator applyDiscriminator(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
        if (discriminator != null) {
            String column = discriminator.column();
            Class<?> javaType = discriminator.javaType() == void.class ? String.class : discriminator.javaType();
            JdbcType jdbcType = discriminator.jdbcType() == JdbcType.UNDEFINED ? null : discriminator.jdbcType();
            @SuppressWarnings("unchecked")
            Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>)
                    (discriminator.typeHandler() == UnknownTypeHandler.class ? null : discriminator.typeHandler());
            Case[] cases = discriminator.cases();
            Map<String, String> discriminatorMap = new HashMap();
            for (Case c : cases) {
                String value = c.value();
                String caseResultMapId = resultMapId + "-" + value;
                discriminatorMap.put(value, caseResultMapId);
            }
            return assistant.buildDiscriminator(resultType, column, javaType, jdbcType, typeHandler, discriminatorMap);
        }
        return null;
    }

    void parseStatement(Method method) {
        List<Class> entityClass = readMethodEntityClasss(method);
        if (entityClass.size() == 0) {
            logger.debug("找不到方法{}能够处理的实体类，不做处理", method);
            doParseStatement(method, null);
        } else {
            for (Class ec : entityClass) {
                doParseStatement(method, ec);
            }
        }
    }


    public static void main(String[] args) {
        Class c = CommonMapper.class;
        for (Method method : c.getDeclaredMethods()) {
            for (Type type : method.getGenericParameterTypes()) {
                if (type instanceof ParameterizedType) {
                    System.out.println(((ParameterizedType) type).getRawType());
                    for (Type type1 : ((ParameterizedType) type).getActualTypeArguments()) {
                        System.out.println(type1);
                    }
                } else if (type instanceof TypeVariable) {
                }
            }
        }
    }

    /**
     * 读取该方法能够处理的所有实体类的列表
     *
     * @param method
     * @return
     */
    private List<Class> readMethodEntityClasss(Method method) {
        List<Class> entityClass = new ArrayList<>();
        //先从参数中获取
        for (Type type : method.getGenericParameterTypes()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (rawType.equals(SqlQuery.class) || rawType.equals(Class.class)) {
                    for (Type actType : parameterizedType.getActualTypeArguments()) {
                        if (actType instanceof TypeVariable) {
                            entityClass.addAll(filterBounds((Class) ((TypeVariable) actType).getBounds()[0]));
                        } else if (actType instanceof WildcardType) {
                            entityClass.addAll(filterBounds((Class) ((WildcardType) actType).getUpperBounds()[0]));
                        }
                    }
                }
            } else if (type instanceof TypeVariable) {
                //如果方法声明了泛型
                if (((TypeVariable) type).getGenericDeclaration().equals(method)) {
                    entityClass.addAll(filterBounds((Class) ((TypeVariable) type).getBounds()[0]));
                }
            } else if (type instanceof GenericArrayType) {
                //如果方法参数有list之类的泛型参数
                Type genericArrayType = ((GenericArrayType) type).getGenericComponentType();
                // TODO
            } else if (type instanceof Class) {
                if (type.equals(SqlQuery.class) || type.equals(Class.class)) {
                    entityClass.addAll(filterBounds(Object.class));
                    //entityClass.add(configuration.getEntityClass().get(0));
                }
            }
        }
        //再从返回类型中获取
        if (entityClass.size() == 0) {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof Class) {
                entityClass.addAll(filterBounds((Class) returnType));
            } else if (returnType instanceof GenericArrayType) {
                //TODO
            } else if (returnType instanceof WildcardType) {
                //TODO
            }
        }
        //再从mapp而注解中获取
        if (type.isAnnotationPresent(Mapper.class)) {
            Mapper mapper = type.getAnnotation(Mapper.class);
            for (Class c : mapper.value()) {
                entityClass.addAll(filterBounds(c));
            }
        }
        //实在没有从config中获取所有实体类
        return entityClass;
    }

    private Collection<? extends Class> filterBounds(Class bounds) {
        return configuration.getEntityClass().stream().filter(bounds::isAssignableFrom).collect(Collectors.toList());
    }

    private void doParseStatement(Method method, Class entityClass) {
        final String mappedStatementId = buildStatementId(method, entityClass);
        Class<?> parameterTypeClass = getParameterType(method);
        LanguageDriver languageDriver = getLanguageDriver(method);
        SqlSource sqlSource = getSqlSourceFromAnnotations(method, entityClass, parameterTypeClass, languageDriver);
        if (sqlSource != null) {
            Options options = method.getAnnotation(Options.class);
            Integer fetchSize = null;
            Integer timeout = null;
            StatementType statementType = StatementType.PREPARED;
            ResultSetType resultSetType = ResultSetType.FORWARD_ONLY;
            SqlCommandType sqlCommandType = getSqlCommandType(method);
            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
            boolean flushCache = !isSelect;
            boolean useCache = isSelect;

            KeyGenerator keyGenerator;
            String keyProperty = "id";
            String keyColumn = null;
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                // first check for SelectKey annotation - that overrides everything else
                SelectKey selectKey = method.getAnnotation(SelectKey.class);
                if (selectKey != null) {
                    keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, method, entityClass, getParameterType(method), languageDriver);
                    keyProperty = selectKey.keyProperty();
                } else if (options == null) {
                    keyGenerator = configuration.isUseGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
                } else {
                    keyGenerator = options.useGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
                    keyProperty = options.keyProperty();
                    keyColumn = options.keyColumn();
                }
            } else {
                keyGenerator = NoKeyGenerator.INSTANCE;
            }

            if (options != null) {
                if (FlushCachePolicy.TRUE.equals(options.flushCache())) {
                    flushCache = true;
                } else if (FlushCachePolicy.FALSE.equals(options.flushCache())) {
                    flushCache = false;
                }
                useCache = options.useCache();
                fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize() : null; //issue #348
                timeout = options.timeout() > -1 ? options.timeout() : null;
                statementType = options.statementType();
                resultSetType = options.resultSetType();
            }

            String resultMapId = null;
            ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
            if (resultMapAnnotation != null) {
                String[] resultMaps = resultMapAnnotation.value();
                StringBuilder sb = new StringBuilder();
                for (String resultMap : resultMaps) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(resultMap);
                }
                resultMapId = sb.toString();
            } else if (isSelect) {
                resultMapId = parseResultMap(method, entityClass);
            }
            assistant.addMappedStatement(
                    mappedStatementId,
                    sqlSource,
                    statementType,
                    sqlCommandType,
                    fetchSize,
                    timeout,
                    // ParameterMapID
                    null,
                    parameterTypeClass,
                    resultMapId,
                    getReturnType(method),
                    resultSetType,
                    flushCache,
                    useCache,
                    // TODO gcode issue #577
                    false,
                    keyGenerator,
                    keyProperty,
                    keyColumn,
                    // DatabaseID
                    null,
                    languageDriver,
                    // ResultSets
                    options != null ? nullOrEmpty(options.resultSets()) : null);
        }
    }

    private String buildStatementId(Method method, Class entityClass) {
        List<String> list = new ArrayList<>();
        list.add(type.getName());
        list.add(method.getName());
        for (Class c : method.getParameterTypes()) {
            list.add(c.getSimpleName());
        }
        if (entityClass != null) {
            list.add(entityClass.getSimpleName());
        }
        return list.stream().collect(Collectors.joining("."));
    }

    private LanguageDriver getLanguageDriver(Method method) {
        Lang lang = method.getAnnotation(Lang.class);
        Class<?> langClass = null;
        if (lang != null) {
            langClass = lang.value();
        }
        return assistant.getLanguageDriver(langClass);
    }

    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType) && !Class.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null) {
                    parameterType = currentParameterType;
                } else {
                    // issue #135
                    parameterType = ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            // gcode issue #508
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        } else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue #443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    } else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // (gcode issue #525) support List<byte[]>
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            } else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                // (gcode issue 504) Do not look into Maps if there is not MapKey annotation
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    Type returnTypeParameter = actualTypeArguments[1];
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    } else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue 443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            }
        }

        return returnType;
    }

    private SqlSource getSqlSourceFromAnnotations(Method method, Class entityClass, Class<?> parameterType, LanguageDriver languageDriver) {
        try {
            Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
            Class<? extends Annotation> sqlProviderAnnotationType = getSqlProviderAnnotationType(method);
            if (sqlAnnotationType != null) {
                Assert.isNull(sqlProviderAnnotationType, "不能同时使用SqlProvider和Sql相关的注解");
                Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
                final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
                return buildSqlSourceFromStrings(strings, method, entityClass, parameterType, languageDriver);
            } else if (sqlProviderAnnotationType != null) {
                Annotation sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
                return new ProviderSqlSource(assistant.getConfiguration(), sqlProviderAnnotation, type, method);
            }
            return null;
        } catch (Exception e) {
            logger.error("解析{}的方法{}失败，请检查sql是否正确！", parameterType.getName(), method.getName());
            throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + e, e);
        }
    }

    private SqlSource buildSqlSourceFromStrings(String[] strings, Method method, Class entityClass, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        String sql = String.join(" ", strings);
        sql = Dialect.parseSql(configuration, sql);
        if (entityClass != null) {
            sql = PropertyParser.parse(sql, new TableInfoProperties(entityClass, method));
        }
        if (!sql.startsWith("<script>")) {
            sql = String.join("", "<script>", sql, "</script>");
        }
        return languageDriver.createSqlSource(configuration, sql.trim(), parameterTypeClass);
    }

    private SqlCommandType getSqlCommandType(Method method) {
        Class<? extends Annotation> type = getSqlAnnotationType(method);

        if (type == null) {
            type = getSqlProviderAnnotationType(method);

            if (type == null) {
                return SqlCommandType.UNKNOWN;
            }

            if (type == SelectProvider.class) {
                type = Select.class;
            } else if (type == InsertProvider.class) {
                type = Insert.class;
            } else if (type == UpdateProvider.class) {
                type = Update.class;
            } else if (type == DeleteProvider.class) {
                type = Delete.class;
            }
        }

        return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
    }

    private Class<? extends Annotation> getSqlAnnotationType(Method method) {
        return chooseAnnotationType(method, sqlAnnotationTypes);
    }

    private Class<? extends Annotation> getSqlProviderAnnotationType(Method method) {
        return chooseAnnotationType(method, sqlProviderAnnotationTypes);
    }

    private Class<? extends Annotation> chooseAnnotationType(Method method, Set<Class<? extends Annotation>> types) {
        for (Class<? extends Annotation> type : types) {
            Annotation annotation = method.getAnnotation(type);
            if (annotation != null) {
                return type;
            }
        }
        return null;
    }

    private void applyResults(Result[] results, Class<?> resultType, List<ResultMapping> resultMappings) {
        for (Result result : results) {
            List<ResultFlag> flags = new ArrayList();
            if (result.id()) {
                flags.add(ResultFlag.ID);
            }
            @SuppressWarnings("unchecked")
            Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>)
                    ((result.typeHandler() == UnknownTypeHandler.class) ? null : result.typeHandler());
            ResultMapping resultMapping = assistant.buildResultMapping(
                    resultType,
                    nullOrEmpty(result.property()),
                    nullOrEmpty(result.column()),
                    result.javaType() == void.class ? null : result.javaType(),
                    result.jdbcType() == JdbcType.UNDEFINED ? null : result.jdbcType(),
                    hasNestedSelect(result) ? nestedSelectId(result) : null,
                    null,
                    null,
                    null,
                    typeHandler,
                    flags,
                    null,
                    null,
                    isLazy(result));
            resultMappings.add(resultMapping);
        }
    }

    private String nestedSelectId(Result result) {
        String nestedSelect = result.one().select();
        if (nestedSelect.length() < 1) {
            nestedSelect = result.many().select();
        }
        if (!nestedSelect.contains(".")) {
            nestedSelect = type.getName() + "." + nestedSelect;
        }
        return nestedSelect;
    }

    private boolean isLazy(Result result) {
        boolean isLazy = configuration.isLazyLoadingEnabled();
        if (result.one().select().length() > 0 && FetchType.DEFAULT != result.one().fetchType()) {
            isLazy = result.one().fetchType() == FetchType.LAZY;
        } else if (result.many().select().length() > 0 && FetchType.DEFAULT != result.many().fetchType()) {
            isLazy = result.many().fetchType() == FetchType.LAZY;
        }
        return isLazy;
    }

    private boolean hasNestedSelect(Result result) {
        if (result.one().select().length() > 0 && result.many().select().length() > 0) {
            throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
        }
        return result.one().select().length() > 0 || result.many().select().length() > 0;
    }

    private void applyConstructorArgs(Arg[] args, Class<?> resultType, List<ResultMapping> resultMappings) {
        for (Arg arg : args) {
            List<ResultFlag> flags = new ArrayList();
            flags.add(ResultFlag.CONSTRUCTOR);
            if (arg.id()) {
                flags.add(ResultFlag.ID);
            }
            @SuppressWarnings("unchecked")
            Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>)
                    (arg.typeHandler() == UnknownTypeHandler.class ? null : arg.typeHandler());
            ResultMapping resultMapping = assistant.buildResultMapping(
                    resultType,
                    nullOrEmpty(arg.name()),
                    nullOrEmpty(arg.column()),
                    arg.javaType() == void.class ? null : arg.javaType(),
                    arg.jdbcType() == JdbcType.UNDEFINED ? null : arg.jdbcType(),
                    nullOrEmpty(arg.select()),
                    nullOrEmpty(arg.resultMap()),
                    null,
                    null,
                    typeHandler,
                    flags,
                    null,
                    null,
                    false);
            resultMappings.add(resultMapping);
        }
    }

    private String nullOrEmpty(String value) {
        return value == null || value.trim().length() == 0 ? null : value;
    }

    private Result[] resultsIf(Results results) {
        return results == null ? new Result[0] : results.value();
    }

    private Arg[] argsIf(ConstructorArgs args) {
        return args == null ? new Arg[0] : args.value();
    }

    private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId, Method method, Class<?> entityClass, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        Class<?> resultTypeClass = selectKeyAnnotation.resultType();
        StatementType statementType = selectKeyAnnotation.statementType();
        String keyProperty = selectKeyAnnotation.keyProperty();
        String keyColumn = selectKeyAnnotation.keyColumn();
        boolean executeBefore = selectKeyAnnotation.before();

        // defaults
        boolean useCache = false;
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        Integer fetchSize = null;
        Integer timeout = null;
        boolean flushCache = false;
        String parameterMap = null;
        String resultMap = null;
        ResultSetType resultSetTypeEnum = null;

        SqlSource sqlSource = buildSqlSourceFromStrings(selectKeyAnnotation.statement(), method, entityClass, parameterTypeClass, languageDriver);
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum,
                flushCache, useCache, false,
                keyGenerator, keyProperty, keyColumn, null, languageDriver, null);

        id = assistant.applyCurrentNamespace(id, false);

        MappedStatement keyStatement = configuration.getMappedStatement(id, false);
        SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
        configuration.addKeyGenerator(id, answer);
        return answer;
    }

}
