package com.dr.support.codegen;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.support.codegen.db.SqlReservedWords;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.helpers.NOPLogger;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.dr.framework.core.orm.sql.support.SqlQuery.QUERY_CLASS_SUFFIX;
import static com.dr.support.codegen.CodeGenProcessor.CHECK_WORDS_OPTION;

/**
 * @author dr
 */
@SupportedAnnotationTypes("com.dr.framework.core.orm.annotations.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(CHECK_WORDS_OPTION)
public class CodeGenProcessor extends AbstractProcessor {
    static final String CHECK_WORDS_OPTION = "checkWords";
    protected VelocityEngine velocityEngine;
    protected Messager messager;
    private Filer filer;
    private List<String> tables = new ArrayList<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element : annotations) {
            for (Element element1 : roundEnv.getElementsAnnotatedWith(element)) {
                if (element1.getKind() == ElementKind.CLASS) {
                    parseEntity((TypeElement) element1);
                }
            }
        }
        return true;
    }

    private void parseEntity(TypeElement typeElement) {
        Diagnostic.Kind checkWordKind = Diagnostic.Kind.ERROR;
        Map<String, String> options = processingEnv.getOptions();
        if (options.containsKey(CHECK_WORDS_OPTION) && "false".equalsIgnoreCase(options.get(CHECK_WORDS_OPTION).trim())) {
            checkWordKind = Diagnostic.Kind.WARNING;
        }
        String className = typeElement.getQualifiedName().toString();
        Table table = typeElement.getAnnotation(Table.class);
        if (!table.genInfo()) {
            return;
        }
        String tableName = table.name();
        if (StringUtils.isEmpty(tableName)) {
            tableName = typeElement.getSimpleName().toString();
        }
        if (SqlReservedWords.containsWord(tableName)) {
            messager.printMessage(checkWordKind, String.format("表%s【%s】包含数据库关键字，请更正！", tableName, table.comment()));
        }
        if (tables.contains(tableName.toUpperCase() + table.module().toUpperCase())) {
            messager.printMessage(Diagnostic.Kind.WARNING, String.format("发现重复表%s【%s】，请更正！", tableName, table.comment()));
        } else {
            tables.add(tableName.toUpperCase() + table.module().toUpperCase());
        }
        try {
            JavaFileObject jfo = filer.createSourceFile(className + QUERY_CLASS_SUFFIX);
            messager.printMessage(Diagnostic.Kind.NOTE, "正在创建TableInfo文件: " + jfo.toUri());
            VelocityContext vc = new VelocityContext();
            String modify = typeElement.getModifiers().stream().map(m -> m.name().toLowerCase()).collect(Collectors.joining(" "));
            if (!modify.isEmpty()) {
                modify += " ";
            }
            vc.put("modify", modify);
            vc.put("pkg", ((PackageElement) typeElement.getEnclosingElement()).getQualifiedName().toString());
            vc.put("className", typeElement.getSimpleName() + QUERY_CLASS_SUFFIX);
            vc.put("entityName", typeElement.getSimpleName());
            vc.put("tableName", tableName);
            vc.put("moudle", table.module());
            List<String> colNames = new ArrayList<>();

            String comment = table.comment();
            if (StringUtils.isEmpty(comment)) {
                comment = tableName;
            }
            Map<String, Map<String, String>> colNameMap = new HashMap<>();
            readCols(typeElement, typeElement, checkWordKind, colNameMap, colNames, tableName, comment, vc);
            vc.put("cols", colNameMap.values());
            Writer writer = jfo.openWriter();
            getVelocityEngine().getTemplate("entityHelper.vm").merge(vc, writer);
            writer.close();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void readCols(Element implace, Element typeElement, Diagnostic.Kind checkWordKind, Map<String, Map<String, String>> cols, List<String> colNames, String tableName, String comment, VelocityContext vc) {
        if (typeElement instanceof TypeElement) {
            TypeMirror typeMirror = ((TypeElement) typeElement).getSuperclass();
            if (typeMirror != null) {
                if (typeMirror instanceof DeclaredType) {
                    Element element = ((DeclaredType) typeMirror).asElement();
                    readCols(implace, element, checkWordKind, cols, colNames, tableName, comment, vc);
                }
            }
        }
        Types typeUtils = processingEnv.getTypeUtils();
        typeElement.getEnclosedElements().stream()
                .filter(element2 -> element2.getKind() == ElementKind.FIELD && element2.getAnnotation(Column.class) != null)
                .forEach(element2 -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("key", element2.getSimpleName().toString());
                    Column column = element2.getAnnotation(Column.class);
                    String value = column.name();
                    if (StringUtils.isEmpty(value)) {
                        value = element2.getSimpleName().toString();
                    }
                    if (SqlReservedWords.containsWord(value)) {
                        messager.printMessage(checkWordKind, String.format("表%s【%s】列字段%s【%s】包含数据库关键字，请更正！", tableName, comment, value, column.comment()));
                    }
                    if (colNames.contains(value.toUpperCase())) {
                        messager.printMessage(Diagnostic.Kind.ERROR, String.format("发现重复列：表%s【%s】列%s【%s】，请更正！", tableName, comment, value, column.comment()));
                    } else {
                        colNames.add(value.toUpperCase());
                    }
                    map.put("value", value);
                    TypeMirror typeMirror = readType(element2.asType(), implace.asType(), typeUtils);
                    map.put("type", typeMirror.toString().replace("java.lang.", ""));
                    if (column.jdbcType() != java.sql.Types.NULL) {
                        map.put("jdbcType", JdbcType.forCode(column.jdbcType()).name());
                    } else {
                        switch (map.get("type")) {
                            case "String":
                                map.put("jdbcType", "VARCHAR");
                                break;
                            case "int":
                            case "Integer":
                            case "double":
                            case "long":
                            case "Long":
                            case "float":
                            case "boolean":
                            case "Boolean":
                                map.put("jdbcType", "NUMERIC");
                                break;
                            default:
                                break;
                        }
                    }
                    if (element2.getAnnotation(Id.class) != null) {
                        vc.put("pk", element2.getSimpleName().toString());
                    }
                    if (cols.containsKey(map.get("key"))) {
                        messager.printMessage(Diagnostic.Kind.ERROR,
                                String.format("实体类【%s】存在重复的字段定义【%s】"
                                        , typeElement
                                        , element2.getSimpleName()
                                )
                        );
                    } else {
                        cols.put(map.get("key"), map);
                    }
                });
    }

    private TypeMirror readType(TypeMirror typeMirror, TypeMirror implace, Types typeUtils) {
        if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
            List<? extends TypeMirror> typeMirrors = typeUtils.directSupertypes(implace);
            if (!typeMirrors.isEmpty()) {
                TypeMirror typeMirror12 = typeMirrors.get(0);
                if (typeMirror12 instanceof DeclaredType) {
                    List<? extends TypeMirror> typeMirrors1 = ((DeclaredType) typeMirror12).getTypeArguments();
                    if (!typeMirrors1.isEmpty()) {
                        return typeMirrors1.get(0);
                    } else {
                        return readType(typeMirror, typeMirror12, typeUtils);
                    }
                }
            }
        }
        return typeMirror;
    }


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    public VelocityEngine getVelocityEngine() {
        if (velocityEngine == null) {
            try {
                Properties props = new Properties();
                URL url = this.getClass().getClassLoader().getResource("velocity.properties");
                props.load(url.openStream());
                props.put("runtime.log.instance", NOPLogger.NOP_LOGGER);
                velocityEngine = new VelocityEngine(props);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return velocityEngine;
    }
}
