package org.ubercraft.sucre.reflect;

/**
 * Package-private static helper functions.
 */
class ReflectHelper {

    static String accessorMethodNameToFieldName(String accessorName) {
        String fieldName = null;
        if (accessorName.startsWith("get")) {
            fieldName = accessorName.substring(3);
        }
        else if (accessorName.startsWith("is")) {
            fieldName = accessorName.substring(2);
        }
        return forFieldName(fieldName);
    }

    static String mutatorMethodNameToFieldName(String methodName) {
        String fieldName = null;
        if (methodName.startsWith("set")) {
            fieldName = methodName.substring(3);
        }
        return forFieldName(fieldName);
    }

    private static String forFieldName(String fieldName) {
        if (fieldName != null) {
            if (fieldName.length() == 0) {
                fieldName = null;
            }
            else {
                fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
            }
        }
        return fieldName;
    }
}
