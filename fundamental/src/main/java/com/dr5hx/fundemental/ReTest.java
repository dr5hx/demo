package com.dr5hx.fundemental;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

/**
 * fundemental.ReTest
 * Desc:
 * Date:2023/11/21 16:17
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ReTest {
    public static void main(String[] args) {
        Class<Demo> demoClass = Demo.class;
        for (Annotation annotation : demoClass.getAnnotations()) {
            System.out.println(annotation.getClass().getName());
        }
        for (AnnotatedType annotation : demoClass.getAnnotatedInterfaces()) {
            System.out.println(annotation.getClass().getName());
        }
    }
}
