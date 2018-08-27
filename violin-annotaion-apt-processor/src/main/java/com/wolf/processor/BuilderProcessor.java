package com.wolf.processor;

import com.wolf.annotation.MyBuilder;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * MyBuilder注解处理器
 *
 */
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(MyBuilder.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotationElements = roundEnv.getElementsAnnotatedWith(MyBuilder.class);

        String className = null;
        Map<String, String> setterMap = new HashMap<>();

        for (Element annotationElement : annotationElements) {
            if (className == null) {
                className = ((TypeElement) annotationElement.getEnclosingElement()).getQualifiedName().toString();
            }
            setterMap.put(annotationElement.getSimpleName().toString(),
                    ((ExecutableType) annotationElement.asType()).getParameterTypes().get(0).toString());
        }

        try {
            if (className != null) {
                writeBuilderSourceFile(className, setterMap);
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return true;
    }

    private void writeBuilderSourceFile(String className, Map<String, String> setterMap) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf(".");
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "MyBuilder";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.printf("package %s;\n", packageName);
                out.println();
            }
            out.printf("public class %s {\n", builderSimpleClassName);
            out.println();
            out.printf("  private %s object = new %s();\n", simpleClassName, simpleClassName);
            out.println();
            out.printf("  public %s build() {\n", simpleClassName);
            out.printf("    return object;\n");
            out.printf("  }\n");
            out.println();
            setterMap.forEach((methodName, argumentType) -> {
                out.printf("  public %s %s(%s value){\n", builderSimpleClassName, methodName, argumentType);
                out.printf("    object.%s(value);\n", methodName);
                out.printf("    return this;\n");
                out.printf("  }\n");
                out.println();
            });
            out.printf("}\n");

        }
    }

}