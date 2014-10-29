package org.cf.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

public class SmaliClassUtils {

    private static Map<String, Class<?>> PrimitiveTypes;
    static {
        PrimitiveTypes = new HashMap<String, Class<?>>(8);
        PrimitiveTypes.put("I", Integer.TYPE);
        PrimitiveTypes.put("S", Short.TYPE);
        PrimitiveTypes.put("J", Long.TYPE);
        PrimitiveTypes.put("B", Byte.TYPE);
        PrimitiveTypes.put("D", Double.TYPE);
        PrimitiveTypes.put("F", Float.TYPE);
        PrimitiveTypes.put("Z", Boolean.TYPE);
        PrimitiveTypes.put("C", Character.TYPE);
    }

    public static boolean isPrimitiveType(String type) {
        return PrimitiveTypes.containsKey(getBaseClass(type));
    }

    public static String javaClassToSmali(Class<?> klazz) {
        return javaClassToSmali(klazz.getName());
    }

    public static String javaClassToSmali(String className) {
        if (className.endsWith(";") || (className.length() == 1)) {
            // Already Smali format
            return className;
        }
        if (className.startsWith("[")) {
            return className;
        }

        for (String key : PrimitiveTypes.keySet()) {
            String typeName = PrimitiveTypes.get(key).getName();
            if (typeName.equals(className)) {
                return key;
            }
        }

        return "L" + className.replaceAll("\\.", "/") + ";";
    }

    public static String smaliClassToJava(String className) {
        Class<?> type = PrimitiveTypes.get(className);
        if (type != null) {
            return type.getName();
        }

        if (className.equals("?")) {
            // Probably lazy and didn't determine type.
            // This will probably result in a Class.forName exception.
            return className;
        }

        // Java doesn't like Lcom/package/class;, it wants "com.package.class"
        return className.substring(1, className.length() - 1).replaceAll("/", ".");
    }

    public static String smaliPrimitiveToJavaWrapper(String className) {
        Class<?> primitiveClass = PrimitiveTypes.get(getBaseClass(className));

        return ClassUtils.primitiveToWrapper(primitiveClass).getName();
    }

    public static String getPackageName(String smaliType) {
        String packageName = smaliType.substring(1, smaliType.lastIndexOf("/"));
        packageName = packageName.replaceAll("/", ".");

        return packageName;
    }

    private static String getBaseClass(String className) {
        // Remove any array qualifiers, e.g. [[B (2d byte array) becomes B
        return className.replace("[", "");
    }

}