package org.ubercraft.sucre.coercer;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.ubercraft.sucre.common.ObjectUtil;

public final class CoercerUtil {

    public static final Coercer DEFAULT_COERCER = new Coercer() {
        @Override
        public Object coerce(Object value, Class<?> type, boolean strict) {
            return coerceValue(value, type, strict);
        }
    };

    public static String coerceToString(Object object) {
        return coerceToString(object, null);
    }

    public static String coerceToString(Object object, String defaultValue) {
        return (object != null) ? object.toString() : defaultValue;
    }

    public static Boolean coerceToBoolean(Object object, boolean strict) {
        return coerceToBoolean(object, strict, null);
    }

    public static Boolean coerceToBoolean(Object object, boolean strict, Boolean defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Boolean) {
            return ((Boolean)object);
        }
        String string = coerceToString(object).trim();
        if ("true".equalsIgnoreCase(string) || "t".equalsIgnoreCase(string) //
                || "yes".equalsIgnoreCase(string) || "y".equalsIgnoreCase(string) || "1".equals(string)) {
            return true;
        }
        if ("false".equalsIgnoreCase(string) || "f".equalsIgnoreCase(string) //
                || "no".equalsIgnoreCase(string) || "n".equalsIgnoreCase(string) || "0".equals(string)) {
            return false;
        }
        if (strict) {
            throw new CoercerException("could not coerce to boolean: " + string);
        }
        return defaultValue;
    }

    public static Integer coerceToInteger(Object object, boolean strict) {
        return coerceToInteger(object, strict, null);
    }

    public static Integer coerceToInteger(Object object, boolean strict, Integer defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Integer) {
            return ((Integer)object);
        }
        if (object instanceof Number) {
            return ((Number)object).intValue();
        }
        try {
            return Double.valueOf(coerceToString(object).trim()).intValue();
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static Double coerceToDouble(Object object, boolean strict) {
        return coerceToDouble(object, strict, null);
    }

    public static Double coerceToDouble(Object object, boolean strict, Double defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Double) {
            return ((Double)object);
        }
        if (object instanceof Number) {
            return ((Number)object).doubleValue();
        }
        try {
            return Double.valueOf(coerceToString(object).trim());
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static Long coerceToLong(Object object, boolean strict) {
        return coerceToLong(object, strict, null);
    }

    public static Long coerceToLong(Object object, boolean strict, Long defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Long) {
            return ((Long)object);
        }
        if (object instanceof Number) {
            return ((Number)object).longValue();
        }
        try {
            return Double.valueOf(coerceToString(object).trim()).longValue();
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static Byte coerceToByte(Object object, boolean strict) {
        return coerceToByte(object, strict, null);
    }

    public static Byte coerceToByte(Object object, boolean strict, Byte defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Byte) {
            return ((Byte)object);
        }
        if (object instanceof Number) {
            return ((Number)object).byteValue();
        }
        try {
            return Double.valueOf(coerceToString(object).trim()).byteValue();
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static Short coerceToShort(Object object, boolean strict) {
        return coerceToShort(object, strict, null);
    }

    public static Short coerceToShort(Object object, boolean strict, Short defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Short) {
            return ((Short)object);
        }
        if (object instanceof Number) {
            return ((Number)object).shortValue();
        }
        try {
            return Double.valueOf(coerceToString(object).trim()).shortValue();
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static Character coerceToChar(Object object, boolean strict) {
        return coerceToChar(object, strict, null);
    }

    public static Character coerceToChar(Object object, boolean strict, Character defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof Character) {
            return ((Character)object);
        }
        if (object instanceof Number) {
            return (char)((Number)object).shortValue();
        }
        try {
            return (char)Double.valueOf(coerceToString(object).trim()).shortValue();
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static BigInteger coerceToBigInteger(Object object, boolean strict) {
        return coerceToBigInteger(object, strict);
    }

    public static BigInteger coerceToBigInteger(Object object, boolean strict, BigInteger defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof BigInteger) {
            return (BigInteger)object;
        }
        if (object instanceof Number) {
            return BigInteger.valueOf(((Number)object).longValue());
        }
        try {
            return new BigInteger(coerceToString(object).trim());
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    public static BigDecimal coerceToBigDecimal(Object object, boolean strict) {
        return coerceToBigDecimal(object, strict);
    }

    public static BigDecimal coerceToBigDecimal(Object object, boolean strict, BigDecimal defaultValue) {
        if (object == null) {
            return defaultValue;
        }
        if (object instanceof BigDecimal) {
            return (BigDecimal)object;
        }
        if (object instanceof Long || object instanceof Integer || object instanceof Short) {
            return BigDecimal.valueOf(((Number)object).longValue());
        }
        else if (object instanceof Number) {
            return BigDecimal.valueOf(((Number)object).doubleValue());
        }
        try {
            return new BigDecimal(coerceToString(object).trim());
        }
        catch (NumberFormatException e) {
            if (strict) {
                throw new CoercerException(e);
            }
            return defaultValue;
        }
    }

    @SuppressWarnings({
        "rawtypes"
    })
    public static Enum<?> coerceToEnum(Class<? extends Enum> type, Object object, boolean strict) {
        return coerceToEnum(type, object, strict, null);
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static Enum<?> coerceToEnum(Class<? extends Enum> type, Object object, boolean strict, Enum<?> defaultValue) {
        if (object == null) return defaultValue;
        String string = coerceToString(object).trim();
        try {
            return Enum.valueOf((Class<? extends Enum>)type, string);
        }
        catch (IllegalArgumentException e1) {
            string = string.toUpperCase();
            try {
                return Enum.valueOf((Class<? extends Enum>)type, string);
            }
            catch (IllegalArgumentException e2) {
                string = string.toLowerCase();
                try {
                    return Enum.valueOf((Class<? extends Enum>)type, string);
                }
                catch (IllegalArgumentException e3) {
                    if (strict) {
                        throw new CoercerException(e1);
                    }
                    return defaultValue;
                }
            }
        }
    }

    public static <T> T coerceValue(Object value, Class<? extends T> type, boolean strict) {
        return coerceValue(value, type, strict, null);
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public static <T> T coerceValue(Object value, Class<? extends T> type, boolean strict, T defaultValue) {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
            if (type == String.class) {
                value = coerceToString(value, (String)defaultValue);
            }
            else if (type == Boolean.class || type == boolean.class) {
                value = coerceToBoolean(value, strict, (Boolean)defaultValue);
            }
            else if (type == Integer.class || type == int.class) {
                value = coerceToInteger(value, strict, (Integer)defaultValue);
            }
            else if (type == Double.class || type == double.class) {
                value = coerceToDouble(value, strict, (Double)defaultValue);
            }
            else if (type == Long.class || type == long.class) {
                value = coerceToLong(value, strict, (Long)defaultValue);
            }
            else if (type == Byte.class || type == byte.class) {
                value = coerceToByte(value, strict, (Byte)defaultValue);
            }
            else if (type == Short.class || type == short.class) {
                value = coerceToShort(value, strict, (Short)defaultValue);
            }
            else if (type.isEnum()) {
                value = coerceToEnum((Class<? extends Enum>)type, value, strict, (Enum)defaultValue);
            }
            else if (type == File.class) {
                String string = coerceToString(value);
                value = (string != null) ? new File(string.trim()) : (File)defaultValue;
            }
            else if (type == BigInteger.class) {
                value = coerceToBigInteger(value, strict, (BigInteger)defaultValue);
            }
            else if (type == BigDecimal.class) {
                value = coerceToBigDecimal(value, strict, (BigDecimal)defaultValue);
            }
            else if (type == Class.class) {
                try {
                    String string = coerceToString(value);
                    value = (string != null) ? Class.forName(string.trim()) : (Class)defaultValue;
                }
                catch (ClassNotFoundException e) {
                    throw new CoercerException(e);
                }
            }
            else {
                throw new CoercerException("failed to coerce <" + value + "> from " + value.getClass().getName() + " to " + type.getName());
            }
        }
        if (value == null && type.isPrimitive()) {
            value = ObjectUtil.getPrimitiveDefault(type);
        }
        return (T)value;
    }
}
