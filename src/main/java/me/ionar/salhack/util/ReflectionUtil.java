package me.ionar.salhack.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.ionar.salhack.main.SalHack;

// https://github.com/seppukudevelopment/seppuku/blob/558636579c2c2df5941525d9af1f6e5a4ef658cc/src/main/java/me/rigamortis/seppuku/api/util/ReflectionUtil.java
/**
 * Author Seth 4/7/2019 @ 9:13 PM.
 */
@SuppressWarnings("resource")
public final class ReflectionUtil {
    public static List<Class<?>> getClassesEx(String path) {
        final List<Class<?>> classes = new ArrayList<>();
        try {
            final File[] files = new File(path).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                        final ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{ file.toURI().toURL() }, SalHack.class.getClassLoader());
                        final ZipFile zip = new ZipFile(file);
                        for (Enumeration<? extends ZipEntry> list = zip.entries(); list.hasMoreElements();) {
                            final ZipEntry entry = list.nextElement();
                            if (entry.getName().contains(".class") && !entry.getName().contains(".classpath")) {
                                classes.add(classLoader.loadClass(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.')));
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return classes;
    }
}