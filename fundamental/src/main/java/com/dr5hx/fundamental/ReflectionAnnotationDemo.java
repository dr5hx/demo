package com.dr5hx.fundamental;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

/**
 * fundamental.ReflectionAnnotationDemo
 * Desc: Demonstrates Java reflection for examining annotations and interfaces
 * Date:2023/11/21 16:17
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ReflectionAnnotationDemo {
    public static void main(String[] args) {
        Class<IntegerOverflowDemo> demoClass = IntegerOverflowDemo.class;
        for (Annotation annotation : demoClass.getAnnotations()) {
            System.out.println(annotation.getClass().getName());
        }
        for (AnnotatedType annotation : demoClass.getAnnotatedInterfaces()) {
            System.out.println(annotation.getClass().getName());
        }
    }
}