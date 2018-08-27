工程目的：
演示如何书写注解处理器

直接编译出现错误：
Error:java: 服务配置文件不正确, 或构造处理程序对象javax.annotation.processing.Processor: Provider processor.BuilderProcessor not found时抛出异常错误

本工程需要以jar形式被引用，不能直接编译。

MyBuilder是源码级别存在的，只能用jar方式引入一起编译时期使用,工程之间依赖都不行。或者用maven依赖jar方式也可以


编译时获取注解
编译时 Annotation(@Retention 为 CLASS 的 Annotation)是由 APT(Annotation Processing Tool) 自动解析的。
APT在编译时根据resources资源文件夹下的META-INF/services/javax.annotation.processing.Processor自动查找所有继承自
AbstractProcessor 的类，然后调用他们的 process 方法去处理。
编译时注解可以将一些固定的代码隐藏起来(自动生成)，只保留核心逻辑

注解处理器是（Annotation Processor）是javac内置的一个工具，用来在编译时扫描、编译和处理注解（Annotation）。
比较常用的用法就是在编译期间获取相关注解数据，然后动态生成.java源文件，通常是自动产生一些有规律性的重复代码，
每次Java编译器（编译）生成class时，如果有发现使用了特定注解就会执行自定义注解处理器，这个注解处理器必须能够在classpath中找到。

MDE(Model-Driven Engineering)，模型驱动工程设计
MDE的一个重要支柱就是它抽象的结构。我们将想要创建的软件系统在不同的细节层面使用不同的方法进行建模。
当对一个抽象层建模之后，我们就可以对下一个和再下一个层面继续建模，直到一个可部署的产品被完整地建立起来。
注解处理器无疑是一种定义元模型和创建模型的优秀方法。注解类型扮演的是元模型角色，而一段代码块中所有注解的集合扮演的则是一个模型的角色。

插件化注解处理(Pluggable Annotation Processing)
Annotation Processor相当于编译器的一个插件,所以称为插入式注解处理.
如果Annotation Processor处理Annotation时(执行process方法)产生了新的Java代码，编译器会再调用一次Annotation Processor，
如果第二次处理还有新代码产生，就会接着调用Annotation Processor，直到没有新代码产生为止。
每执行一次process()方法被称为一个"round"，这样整个Annotation processing过程可以看作是一个round的序列。
JSR 269主要被设计成为针对Tools或者容器的API。

注意，与运行时注解RetentionPolicy.RUNTIME不同，注解处理器只会处理编译期注解，也就是RetentionPolicy.SOURCE的注解类型，
处理的阶段位于Java代码编译期间。

javac -processor com.wolf.processor.MyAnnotationProcessor MainActivity.java。

使用APT主要的目的是简化开发者的工作量，因为APT可以编译程序源代码的同时，
生成一些附属文件(比如源文件，类文件，程序发布描述文件等)，这些附属文件的内容也都是与源代码相关的，
使用APT可以代替传统的对代码信息和附属文件的维护工作。(hibernate生成*.hbm.xml的文件)
也可以用于检查源代码是否有问题