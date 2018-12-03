package com.dr.support.codegen;

import com.dr.framework.core.orm.annotations.Column;
import com.dr.framework.core.orm.annotations.Id;
import com.dr.framework.core.orm.annotations.Table;
import com.dr.support.codegen.db.SqlReservedWords;
import liquibase.util.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

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

import static com.dr.framework.core.orm.sql.support.SqlQuery.QUERY_CLASS_SUFFIX;
import static com.dr.support.codegen.CodeGenProcessor.CHECK_WORDS_OPTION;

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
            List<Map<String, String>> cols = new ArrayList<>();
            readCols(typeElement, typeElement, checkWordKind, cols, colNames, tableName, comment, vc);
            vc.put("cols", cols);
            Writer writer = jfo.openWriter();
            getVelocityEngine().getTemplate("entityHelper.vm").merge(vc, writer);
            writer.close();
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void readCols(Element implace, Element typeElement, Diagnostic.Kind checkWordKind, List<Map<String, String>> cols, List<String> colNames, String tableName, String comment, VelocityContext vc) {
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
                    TypeMirror typeMirror = element2.asType();

                    if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
                        List<? extends TypeMirror> typeMirrors = typeUtils.directSupertypes(implace.asType());
                        if (typeMirrors.size() > 0) {
                            TypeMirror typeMirror12 = typeMirrors.get(0);
                            if (typeMirror12 instanceof DeclaredType) {
                                List<? extends TypeMirror> typeMirrors1 = ((DeclaredType) typeMirror12).getTypeArguments();
                                if (typeMirrors1.size() > 0) {
                                    typeMirror = typeMirrors1.get(0);
                                }
                            }
                        }
                    }

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
                    cols.add(map);
                });
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
                velocityEngine = new VelocityEngine(props);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return velocityEngine;
    }
}
