package com.dr5hx.fundemental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fundemental.GenericOverrideDemo
 * Desc: Demonstrates Java generics and method overriding concepts
 * Date:2023/12/8 15:50
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
abstract class AbstractGenericClass {
    public abstract <V extends List, S extends Map> void a(V a, S r);
}

class ConcreteGenericClass extends AbstractGenericClass {

    public void a(ArrayList a, HashMap r) {
        // 这里实现你的代码
        System.out.println("ArrayList is: " + a);
        System.out.println("HashMap is: " + r);
    }

    @Override
    public <V extends List, S extends Map> void a(V a, S r) {

    }
}

public class GenericOverrideDemo {
    public static void main(String[] args) {
        ConcreteGenericClass concreteClass = new ConcreteGenericClass();
        //invoke method a
//        concreteClass.a(List.of(1), Map.of(1, 2));
    }
}
