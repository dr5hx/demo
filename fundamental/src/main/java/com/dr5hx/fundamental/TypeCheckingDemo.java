package com.dr5hx.fundamental;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * TypeCheckingDemo
 * Desc: Demonstrates various Java type checking methods and instanceof operator
 * Date:2024/5/17 15:29
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class TypeCheckingDemo {
    public static void main(String[] args) {
        System.out.println(List.class.isAssignableFrom(List.class));
        System.out.println(List.class.isInstance(new HashSet<>()));
        System.out.println(List.class.isInstance(new ArrayList<>()));
        System.out.println(new String[]{"1", "2", "22"} instanceof String[]);
//        args = null;
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println();
    }
}