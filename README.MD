注解分成编译时处理和运行时处理
由于不想嵌套太深
编译处理：apt-demo/apt-processor
运行处理：run-annotation




注解（Annotation），也叫元数据。一种代码级别的【说明】。它是JDK1.5及以后版本引入的一个特性，与类、接口、枚举是在同一个层次。
它可以声明在包、类、字段、方法、局部变量、方法参数等的前面，用来对这些元素进行说明，注释。
你可以通过注解来设置任何类型的元数据和配置，并且注解有非常容易定义的语法，以及多种不同的类型可以使用。
Annotations, a form of metadata, provide data about a program that is not part of the program itself. 
Annotations have no direct effect on the operation of the code they annotate.

用途：
在编译时对标注对象进行某些分析、处理。原生的@Override、@Deprecated、@SuppressWarnings都是如此,都用于Java编译期
可以为编译器提供额外信息，以便于检测错误，抑制警告等。
可以为源代码添加元数据信息，包括编译或部署信息、配置属性、编译行为或者代码质量检查。
可以通过处理Annotation信息来生成原代码，xml文件等等
在运行时对标注对象进行某些分析、处理。
通过在源代码中使用注解，这些库和框架都简化甚至移除了所需要的配置文件。
Information for the compiler — Annotations can be used by the compiler to detect errors or suppress * warnings.
Compile-time and deployment-time processing — Software tools can process annotation information to generate code, XML files, and so forth.
Runtime processing — Some annotations are available to be examined at runtime.
编写文档：通过代码里标识的元数据生成文档
代码分析：通过代码里标识的元数据对代码进行分析
编译检查：通过代码里标识的元数据让编译器能实现基本的编译检查。lambda的函数式接口就是一个校验

与Javadoc不同，注解属于强类型，用到的注解在类路径下都能找到对应的注解类型定义。因此，定义的注解可以在运行时访问到，而Javadoc是不可能的。

用于定义注解的注解,JDK自带了一些注解，用于修改我们自定义的注解的默认行为。
@Documented 被注解的元素连同注解都记录到Javadoc中。
@Inherited 继承到它的子类上  只适用于类继承，不适用于接口实现。
@Retention 注解类型的生命周期 CLASS（默认值——能被包含到class文件中，但是在运行时访问不到），SOURCE（创建class文件的时候会被编译器丢弃），RUNTIME（能够在运行过程中访问到）。


内部属性语法等：

@Target 指明注解对象放置位置，取值
ElementType.ANNOTATION_TYPE：注解
ElementType.CONSTRUCTOR：构造方法
ElementType.FIELD：成员字段、枚举常量
ElementType.LOCAL_VARIABLE：局部变量
ElementType.METHOD：方法
ElementType.PACKAGE：包
ElementType.PARAMETER：方法参数
ElementType.TYPE：类型（接口、类、枚举）

Retention：指明注解存在的生命周期
RetentionPolicy.SOURCE：源码时。注解仅存在于源码中，在class字节码文件中不包含。
RetentionPolicy.CLASS：编译时（默认的保留策略）。注解会在class字节码文件中存在，但运行时无法获得。
RetentionPolicy.RUNTIME：运行时。注解会在class字节码文件中存在，在运行时可以通过反射获取到

当注解内部只有一个元素，并且这个元素名为value的时候，元素名可以省略：

只允许原生数据类型、String、枚举、class，以及这些类型的数组
注解元素的定义与定义方法的语法很类似，但是不允许有修饰符和参数。


