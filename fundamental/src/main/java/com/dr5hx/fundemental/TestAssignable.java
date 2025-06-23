package com.dr5hx.fundemental;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * fundemental.TestAssignable
 * Desc:
 * Date:2024/6/28 11:28
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class TestAssignable {
    public static void main(String[] args) {
        System.out.println(ArrayList.class.isAssignableFrom(List.class));
        System.out.println(List.class.isAssignableFrom(ArrayList.class));
        System.out.println(Object.class.isAssignableFrom(ArrayList.class));
        System.out.println(Set.class.isInstance(new ArrayList<>()));
    }
}
