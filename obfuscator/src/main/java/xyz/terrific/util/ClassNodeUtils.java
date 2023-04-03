package xyz.terrific.util;

public class ClassNodeUtils {
    public static String getClassName(String name) {
        if (name.contains("/")) {
            return name.substring(name.lastIndexOf("/"));
        }
        return name;
    }
}
