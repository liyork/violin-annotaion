package com.wolf.lombok;

import lombok.Builder;

import java.io.IOException;

/**
 * Description:添加注解，编译后的class文件中带有get/set方法
 * 前置：安装lombok插件，引入pom
 * <br/> Created on 2018/7/26 11:41
 *
 * @author 李超
 * @since 1.0.0
 */
//用在类上
//@Getter
//@Setter

//@Data//会提供getter、setter、equals、canEqual、hashCode、toString方法。

//includeFieldNames是否输出名称  exclude排除属性    callSuper输出super.toString()
//@ToString(includeFieldNames=true,exclude={"age"},callSuper=true)
//@EqualsAndHashCode  //同时生成equals和hashCode。  存在继承关系需要设置callSuper参数为true。
//@AllArgsConstructor
//@NoArgsConstructor
//@RequiredArgsConstructor  所有带有 @NonNull 注解的或者带有 final 修饰的成员变量生成对应的构造方法。
//@Value  值对象。生成含所有参数的构造方法，get 方法，此外还提供了equals、hashCode、toString 方法。

@Builder//生成LombokTestBuilder，构造器使用所有参数
public class LombokTest extends ParentTest {
    //会默认生成一个无参构造

    //@Getter//方法上
    private Integer age;

    //一起使用,将生成一个空检查，如果为空，则抛出NullPointerException。
//    @Setter
//    @NonNull
    private String name;

    //get生成：getIsMan
    private Boolean isMan;

    //get生成：isStrong
    private boolean isStrong;


    public static void main(String[] args) {
//        LombokTest lombokTest = new LombokTest();
//        System.out.println(lombokTest.name);

//        LombokTestBuilder lombokTestBuilder = LombokTest.builder().age(1).name("xx");
//        LombokTest build = lombokTestBuilder.build();
    }

    public void readFile() throws IOException {

        //默认close方法
        //@Cleanup FileInputStream fileInputStream = new FileInputStream("a.txt");
        //fileInputStream.read();

        //指定自定义方法
        //@Cleanup(value ="delete") File file = new File("b.txt");
        //file.canRead();

    }


//    @Synchronized(value = "age")//使用test的属性age
    public void testSync(LombokTest lombokTest) throws IOException {
        System.out.println("testSync...");
    }
}
