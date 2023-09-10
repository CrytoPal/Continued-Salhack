package me.ionar.salhack.util;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class KeyUtil {
    private static final Map<String, String> replacements = new HashMap<>();
    private static final Map<Integer, String> keyNamesCache = new HashMap<>();

    static {
        replacements.put("PAGE", "PG");
        replacements.put("LEFT", "L");
        replacements.put("RIGHT", "R");
    }
    public static String getKeyName(int key) {
        String keyName = GLFW.glfwGetKeyName(key, 0);
        if (keyName == null) {
            if (keyNamesCache.containsKey(key)) return keyNamesCache.get(key);
            for (Field field : GLFW.class.getDeclaredFields()) {
                try {
                    if (field.getType() == int.class && Modifier.isPublic(field.getModifiers()) && field.getInt(null) == key && field.getName().startsWith("GLFW_KEY_")) {
                        String[] names = field.getName().replaceFirst("GLFW_KEY_", "").split("_");
                        if (names.length == 2) {
                            if (replacements.containsKey(names[0])) keyNamesCache.put(key, replacements.get(names[0]) + StringUtils.capitalize(names[1].toLowerCase()));
                            else keyNamesCache.put(key, StringUtils.capitalize(names[0].toLowerCase())+" "+StringUtils.capitalize(names[1].toLowerCase()));
                        } else keyNamesCache.put(key, StringUtils.capitalize(names[0].toLowerCase()));
                        return keyNamesCache.get(key);
                    }
                } catch (IllegalAccessException ignored) {}
            } return "Unknown";
        } else return keyName;
    }
}
