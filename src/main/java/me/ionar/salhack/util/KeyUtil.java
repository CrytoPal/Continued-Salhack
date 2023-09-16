package me.ionar.salhack.util;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class KeyUtil {
    static Map<String, String> replacements = Map.ofEntries(Map.entry("PAGE", "PG"), Map.entry("LEFT", "L"), Map.entry("RIGHT", "R"));
    static Map<Integer, String> keyNameCache = new HashMap<>();
    public static String getKeyName(int key) {
        String keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName != null) return keyName;
        else if (keyNameCache.containsKey(key)) return keyNameCache.get(key);
        else {
            for (Field field : GLFW.class.getDeclaredFields()) {
                try {
                    if (field.getType() == int.class && Modifier.isPublic(field.getModifiers())) {
                        if (field.getInt(null) == key && field.getName().startsWith("GLFW_KEY_")) {
                            String[] names = field.getName().replaceFirst("GLFW_KEY_", "").split("_");
                            String name;
                            if (names.length == 2) {
                                if (replacements.containsKey(names[0])) {
                                    name = replacements.get(names[0]) + StringUtils.capitalize(names[1].toLowerCase());
                                    keyNameCache.put(key, name);
                                } else {
                                    name = StringUtils.capitalize(names[0].toLowerCase())+" "+StringUtils.capitalize(names[1].toLowerCase());
                                    keyNameCache.put(key, name);
                                }
                                return keyNameCache.get(key);
                            } else return StringUtils.capitalize(names[0].toLowerCase());
                        }
                    }
                } catch (IllegalAccessException ignored) {}
            }
            return "Unknown";
        }
    }
}
