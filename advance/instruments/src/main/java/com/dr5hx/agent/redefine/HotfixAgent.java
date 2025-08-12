package com.dr5hx.agent.redefine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 热修复Agent，用于动态更新已加载类的实现
 */
public class HotfixAgent {

    // 已处理条目的时间戳缓存（key 是绝对路径或 jar!entry，value 是来源文件的 lastModified）
    private static final Map<String, Long> processedEntries = new HashMap<>();

    /**
     * Agent入口点 - JVM启动时
     */
    public static void premain(String args, Instrumentation inst) {
        System.out.println("======== 热修复Agent已启动 ========");
        setupHotfixMonitor(args, inst);
    }

    /**
     * Agent入口点 - 动态加载时
     */
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("======== 热修复Agent已动态加载 ========");
        setupHotfixMonitor(args, inst);
    }

    /**
     * 设置热修复监控器
     */
    private static void setupHotfixMonitor(String args, Instrumentation inst) {
        System.out.println("热修复Agent参数: " + args);

        // 从参数中获取热修复目录
        String hotfixDir = "hotfix";
        if (args != null && !args.isEmpty()) {
            for (String arg : args.split(",")) {
                if (arg.startsWith("dir=")) {
                    hotfixDir = arg.substring("dir=".length());
                    break;
                }
            }
        }

        // 创建热修复目录
        Path dirPath = Paths.get(hotfixDir);
        try {
            String canonicalPath = dirPath.normalize().toAbsolutePath().toString();
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("创建热修复目录: " + canonicalPath);
            } else {
                System.out.println("使用热修复目录: " + canonicalPath);
            }
        } catch (IOException e) {
            System.err.println("创建热修复目录失败: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (!inst.isRedefineClassesSupported()) {
            System.err.println("当前 JVM 不支持类重定义（isRedefineClassesSupported=false），无法进行热修复。");
            return;
        }

        // 启动定时检查
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkForHotfixes(dirPath, inst);
                } catch (Throwable t) {
                    System.err.println("检查热修复时出错: " + t.getMessage());
                    t.printStackTrace();
                }
            }
        }, 1000, 2000); // 每2秒检查一次

        String canonicalPath = dirPath.normalize().toAbsolutePath().toString();
        System.out.println("热修复监视器已启动，监视目录: " + canonicalPath);
        System.out.println("支持两种投放方式：");
        System.out.println("1) 直接放置已编译的 .class（需按包路径放置，例如 com/example/Foo.class）");
        System.out.println("2) 放置包含修复类的 .jar（例如 app-fix.jar）");
    }

    /**
     * 扫描目录中的 .class（递归）与 .jar 并批量应用热修复
     */
    private static void checkForHotfixes(Path dir, Instrumentation inst) {
        if (!Files.isDirectory(dir)) {
            return;
        }

        // 获取已加载类的索引，加速匹配
        Map<String, Class<?>> loadedByName = mapLoadedClasses(inst);

        // 1) 先处理 jar（按 jar 为单位批量）
        List<Path> jarFiles = listImmediateChildren(dir, ".jar");
        for (Path jar : jarFiles) {
            applyJarHotfixIfChanged(jar, inst, loadedByName);
        }

        // 2) 再处理散落的 .class（递归）
        List<Path> classFiles = listAllClassesRecursively(dir);
        if (!classFiles.isEmpty()) {
            applyClassFilesHotfix(dir, classFiles, inst, loadedByName);
        }
    }

    /**
     * 将已加载类映射为 name->Class
     */
    private static Map<String, Class<?>> mapLoadedClasses(Instrumentation inst) {
        Class<?>[] loaded = inst.getAllLoadedClasses();
        Map<String, Class<?>> map = new HashMap<>(loaded.length * 2);
        for (Class<?> c : loaded) {
            String n = c.getName();
            // 过滤一些核心包和 Agent 自身，避免误伤
            if (n.startsWith("java.") || n.startsWith("javax.") || n.startsWith("sun.") || n.startsWith("jdk.")
                || n.startsWith("com.dr5hx.agent")) {
                continue;
            }
            map.put(n, c);
        }
        return map;
    }

    /**
     * 仅列出当前目录下的指定后缀文件（不递归）
     */
    private static List<Path> listImmediateChildren(Path dir, String suffix) {
        try {
            List<Path> result = new ArrayList<>();
            Files.list(dir).forEach(p -> {
                if (Files.isRegularFile(p) && p.getFileName().toString().endsWith(suffix)) {
                    result.add(p);
                }
            });
            return result;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 递归列出所有 .class
     */
    private static List<Path> listAllClassesRecursively(Path dir) {
        List<Path> result = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".class")) {
                        result.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) { }
        return result;
    }

    /**
     * 若 jar 文件有更新，则读取其中的 .class，批量重定义
     */
    private static void applyJarHotfixIfChanged(Path jarPath, Instrumentation inst, Map<String, Class<?>> loadedByName) {
        File jarFile = jarPath.toFile();
        long jarLastModified = jarFile.lastModified();

        String jarKey = jarPath.normalize().toAbsolutePath().toString();
        Long last = processedEntries.get(jarKey);
        if (last != null && last >= jarLastModified) {
            // jar 未变化，跳过
            return;
        }

        Map<Class<?>, byte[]> redefineMap = new HashMap<>();
        Set<String> foundClassNames = new HashSet<>();
        try (JarFile jf = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                String name = entry.getName();
                if (!name.endsWith(".class")) continue;

                String className = name.substring(0, name.length() - 6).replace('/', '.');
                foundClassNames.add(className);

                Class<?> target = loadedByName.get(className);
                if (target == null) {
                    // 未加载，跳过（只对已加载类进行热替换）
                    continue;
                }

                byte[] bytes;
                try (InputStream in = jf.getInputStream(entry)) {
                    bytes = readAllBytes(in);
                }

                redefineMap.put(target, bytes);
            }
        } catch (IOException e) {
            System.err.println("读取 JAR 失败: " + jarPath + ", 原因: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (redefineMap.isEmpty()) {
            System.out.println("JAR 中无可热修复类或均未加载: " + jarPath.getFileName());
            processedEntries.put(jarKey, jarLastModified); // 仍然标记，避免重复扫描
            return;
        }

        // 批量重定义
        applyBatchRedefine("JAR热修复: " + jarPath.getFileName(), redefineMap, inst);

        // 标记 jar 已处理
        processedEntries.put(jarKey, jarLastModified);
        // 可选：将已应用的 jar 重命名，避免反复处理
        try {
            Path applied = jarPath.resolveSibling(jarPath.getFileName().toString() + ".applied");
            Files.move(jarPath, applied);
        } catch (IOException moveEx) {
            // 非关键操作，忽略
        }
    }

    /**
     * 对散落的 .class 批量热修复（从根目录相对路径推导类名）
     */
    private static void applyClassFilesHotfix(Path root, List<Path> classFiles, Instrumentation inst,
                                              Map<String, Class<?>> loadedByName) {
        Map<Class<?>, byte[]> redefineMap = new HashMap<>();

        for (Path file : classFiles) {
            String abs = file.normalize().toAbsolutePath().toString();
            long lm = file.toFile().lastModified();

            Long last = processedEntries.get(abs);
            if (last != null && last >= lm) {
                continue;
            }

            String className = classNameFromPath(root, file);
            if (className == null) {
                continue;
            }

            Class<?> target = loadedByName.get(className);
            if (target == null) {
                // 未加载则跳过
                continue;
            }

            try {
                byte[] bytes = Files.readAllBytes(file);
                redefineMap.put(target, bytes);
                processedEntries.put(abs, lm);
            } catch (IOException e) {
                System.err.println("读取类文件失败: " + file + ", 原因: " + e.getMessage());
            }
        }

        if (!redefineMap.isEmpty()) {
            applyBatchRedefine("Class热修复(目录)", redefineMap, inst);

            // 重命名已处理的 .class，避免重复
            for (Path file : classFiles) {
                String abs = file.normalize().toAbsolutePath().toString();
                if (!processedEntries.containsKey(abs)) continue;
                try {
                    Path applied = file.resolveSibling(file.getFileName().toString() + ".applied");
                    Files.move(file, applied);
                } catch (IOException ignored) { }
            }
        }
    }

    /**
     * 从 root 到 file 的相对路径推导类名：com/example/Foo.class -> com.example.Foo
     */
    private static String classNameFromPath(Path root, Path file) {
        try {
            Path rel = root.relativize(file);
            String p = rel.toString();
            if (!p.endsWith(".class")) return null;
            String noExt = p.substring(0, p.length() - 6);
            // 兼容不同平台分隔符
            String cls = noExt.replace(File.separatorChar, '.').replace('/', '.').replace('\\', '.');
            // 规整可能出现的多余点
            while (cls.contains("..")) {
                cls = cls.replace("..", ".");
            }
            if (cls.startsWith(".")) cls = cls.substring(1);
            if (cls.endsWith(".")) cls = cls.substring(0, cls.length() - 1);
            return cls;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 批量应用重定义
     */
    private static void applyBatchRedefine(String title, Map<Class<?>, byte[]> redefineMap, Instrumentation inst) {
        List<ClassDefinition> defs = new ArrayList<>(redefineMap.size());
        for (Map.Entry<Class<?>, byte[]> e : redefineMap.entrySet()) {
            defs.add(new ClassDefinition(e.getKey(), e.getValue()));
        }
        try {
            inst.redefineClasses(defs.toArray(new ClassDefinition[0]));
            System.out.println(title + " 成功，重定义类数: " + defs.size());
        } catch (Throwable t) {
            System.err.println(title + " 失败: " + t.getMessage());
            t.printStackTrace();
        }
    }

    /**
     * 读取输入流所有字节
     */
    private static byte[] readAllBytes(InputStream in) throws IOException {
        // Java 8 兼容实现
        byte[] buffer = new byte[8192];
        int read;
        List<byte[]> chunks = new ArrayList<>();
        int total = 0;
        while ((read = in.read(buffer)) != -1) {
            byte[] chunk = new byte[read];
            System.arraycopy(buffer, 0, chunk, 0, read);
            chunks.add(chunk);
            total += read;
        }
        byte[] all = new byte[total];
        int pos = 0;
        for (byte[] c : chunks) {
            System.arraycopy(c, 0, all, pos, c.length);
            pos += c.length;
        }
        return all;
    }
}
