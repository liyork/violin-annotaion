package com.wolf;


import com.wolf.annotation.MyBuilder;

/**
 * 生成的Builder类
 */
public class AnnotationProcessorDemo {
    private String name;

    public String getName() {
        return name;
    }

    @MyBuilder
    public void setName(String name) {
        this.name = name;
    }


    public static void main(String[] args) {
//        AnnotationProcessorDemoMyBuilder自动生成的？嗯。需要先生成后打开下面代码重新reimport

//        AnnotationProcessorDemoMyBuilder builder = new AnnotationProcessorDemoMyBuilder();
//        builder.setName("123");
//        System.out.println(builder.build().getName());
    }
}

