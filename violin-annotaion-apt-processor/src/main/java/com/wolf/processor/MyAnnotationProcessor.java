package com.wolf.processor;

import com.wolf.annotation.BindView;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:每个Annotation Processor必须有一个空的构造函数
 * <p>
 * <br/> Created on 2018/7/26 18:37
 *
 * @author 李超
 * @since 1.0.0
 */
@SupportedAnnotationTypes({"com.wolf.annotation.BindView"})  //与下面getSupportedAnnotationTypes方式一样  java7
public class MyAnnotationProcessor extends AbstractProcessor {

    //文件相关的辅助类
    private Filer mFiler;
    //日志相关的辅助类
    private Messager mMessager;
    //元素相关的辅助类
    private Elements mElementUtils;

    //自动被注解处理工具调用，并传入ProcessingEnviroment参数，通过该参数可以获取到很多有用的工具类: Elements , Types , Filer **等等
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    //指定该自定义注解处理器(Annotation Processor)是注册给哪些注解的(Annotation),
    // 注解(Annotation)指定必须是完整的包名+类名(eg:com.example.MyAnnotation)
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> annotations = new LinkedHashSet<>();
//        annotations.add(BindView.class.getCanonicalName());
//        return annotations;
//    }

    //用于指定你的java版本，一般返回：SourceVersion.latestSupported()。也可以指定具体java版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //todo 可以使用Velocity解耦，优化
    //set:处理注解的过程要经过一个或者多个回合才能完成。每个回合中注解处理器都会被调用，并且接收到一个以在当前回合中已经处理过的注解类型的Type为元素的Set集合。
    //roundEnvironment：通过这个对象可以访问到当前或者之前的回合中处理的Element元素，只有被注解处理器注册的注解类型注解过的元素才会被处理。

    //Annotation Processor扫描出的结果会存储进env中，注意,process()函数中不能直接进行异常抛出,
    // 否则的话,运行Annotation Processor的进程会异常崩溃,然后弹出一大堆让人捉摸不清的堆栈调用日志显示.
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (TypeElement te : set) {
            note("Printing:getKind " + te.getKind()+",te.getKind() == ElementKind.CLASS:"+(te.getKind() == ElementKind.CLASS));
            note("Printing:te.class " + te.getClass());
            note("Printing:BindView.class " + BindView.class);
            note("Printing:te.toString " + te.toString());
            List<? extends Element> enclosedElements = te.getEnclosedElements();
            note("Printing:getEnclosedElements " + enclosedElements);//获取成员，value()
            List<ExecutableElement> executableElements = ElementFilter.methodsIn(enclosedElements);
            note("Printing:ElementFilter.methodsIn " + executableElements);//方法成员
            for (Element enclosedElement : executableElements) {
                List<? extends AnnotationMirror> annotationMirrors = enclosedElement.getAnnotationMirrors();//获取方法的Annotations
                for (AnnotationMirror annotationMirror : annotationMirrors) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotationMirror.getElementValues();
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
                        note("Printing:key_value: " + entry.getKey()+"_"+entry.getValue());
                    }
                }
            }
            note("Printing:getEnclosingElement " + te.getEnclosingElement());//com.wolf.annotation，似乎是包名
            note("Printing:getEnclosingElement.getQualifiedName " + ((PackageElement)te.getEnclosingElement()).getQualifiedName());
            note("Printing:getInterfaces " + te.getInterfaces());
            note("Printing:getQualifiedName " + te.getQualifiedName());//带包的类名
            note("Printing:getSimpleName " + te.getSimpleName());//简单名
            note("Printing:getSuperclass " + te.getSuperclass());
            note("Printing:getTypeParameters " + te.getTypeParameters());
            note("Printing:getAnnotationMirrors " + te.getAnnotationMirrors());//获取类的Annotations
            note("Printing:asType " + te.asType());

            note("Printing:getElementsAnnotatedWith: " + roundEnvironment.getElementsAnnotatedWith(te));


            //te是te.class class com.sun.tools.javac.code.Symbol$ClassSymbol，并不是PrintMe的子类
            //是java.lang.annotation.Annotation的子类
            if (te instanceof BindView) {
                for (Element e : roundEnvironment.getElementsAnnotatedWith(te)) {
                    note("\t**Printing: " + e.toString());
                }
            } else {
                for (Element e : roundEnvironment.getElementsAnnotatedWith(te)) {
                    note("\t--Printing: " + e.toString());
                }
            }

            Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BindView.class);
            note("Printing:elementsAnnotatedWith " + elementsAnnotatedWith);

        }




        //被BindView注解的元素
        Set<? extends Element> bindViewElements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element : bindViewElements) {

            note(String.format("MyAnnotationProcessor getClass = %s", element.getClass()));

            //获取包名
            PackageElement packageElement = mElementUtils.getPackageOf(element);
            String pkName = packageElement.getQualifiedName().toString();
            note(String.format("MyAnnotationProcessor package = %s", pkName));

            //获取元素所在类
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            String enclosingName = enclosingElement.getQualifiedName().toString();
            note(String.format("MyAnnotationProcessor enclosindClass = %s", enclosingName));


            //因为BindView只作用于filed，所以这里可直接进行强转
            VariableElement bindViewElement = (VariableElement) element;
            //获取被注解的成员变量名
            String bindViewFiledName = bindViewElement.getSimpleName().toString();
            //获取被注解的成员变量类型
            String bindViewFiledClassType = bindViewElement.asType().toString();

            //获取注解元数据
            BindView bindView = element.getAnnotation(BindView.class);
            int id = bindView.value();
            note(String.format("MyAnnotationProcessor %s %s = %d", bindViewFiledClassType, bindViewFiledName, id));

            //4.生成文件
            createFile(enclosingElement, bindViewFiledClassType, bindViewFiledName, id);
            return true;
        }
        return false;
    }


    private void createFile(TypeElement enclosingElement, String bindViewFiledClassType, String bindViewFiledName, int id) {
        String pkName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
        try {
            JavaFileObject jfo = mFiler.createSourceFile(pkName + ".ViewBinding", new Element[]{});
            Writer writer = jfo.openWriter();
            writer.write(brewCode(pkName, bindViewFiledClassType, bindViewFiledName, id));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String brewCode(String pkName, String bindViewFiledClassType, String bindViewFiledName, int id) {
        StringBuilder builder = new StringBuilder();
        builder.append("package " + pkName + ";\n\n");
        builder.append("//Auto generated by apt,do not modify!!\n\n");
        builder.append("public class ViewBinding { \n\n");
        builder.append("public static void main(String[] args){ \n");
        String info = String.format("%s %s = %d", bindViewFiledClassType, bindViewFiledName, id);
        builder.append("System.out.println(\"" + info + "\");\n");
        builder.append("}\n");
        builder.append("}");
        return builder.toString();
    }


    private void note(String msg) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void note(String format, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(format, args));
    }

}
