package com.dr5hx.fundamental;

import java.util.HashMap;
import java.util.Map;

/**
 * TestMapDemo
 * desc:
 *
 * @date:2025/12/24 16:43
 * @author:zhouchang
 * @email:zhouchang@asiainfo.com
 */
public class TestMapDemo {
    private static Object map = new HashMap() {{
        put("12c", "12ddd");
    }};

    public static void main(String[] args) {
        Map<Integer, Integer> map1 = (Map<Integer, Integer>) map;
        System.out.println(map1);
    }
}
