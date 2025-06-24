package com.dr5hx.fundemental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * fundemental.Test
 * Desc:
 * Date:2023/12/8 15:50
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
abstract class AbstractClass {
    public abstract <V extends List, S extends Map> void a(V a, S r);
}

class ConcreteClass extends AbstractClass {

    public void a(ArrayList a, HashMap r) {
        // 这里实现你的代码
        System.out.println("ArrayList is: " + a);
        System.out.println("HashMap is: " + r);
    }

    @Override
    public <V extends List, S extends Map> void a(V a, S r) {

    }
}

public class Test {
    public static void main(String[] args) {
        ConcreteClass concreteClass = new ConcreteClass();
        //invoke method a
//        concreteClass.a(List.of(1), Map.of(1, 2));
    }
}