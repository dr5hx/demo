package com.dr5hx.fundamental.path;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PathTest
 * Desc:
 * Date:2025/6/26 16:49
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class PathTest {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:/s/.dir");
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        System.out.println(path.getRoot());
        System.out.println(path.getParent());
        System.out.println(path.getFileSystem());
        System.out.println(path.resolve("/test.txt"));
        System.out.println(path.resolve("test.txt"));
        System.out.println(path.resolveSibling("test.txt"));
    }
}
