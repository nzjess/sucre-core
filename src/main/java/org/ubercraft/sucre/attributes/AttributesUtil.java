package org.ubercraft.sucre.attributes;

import java.io.File;
import java.util.Date;
import java.util.TimeZone;

import org.ubercraft.sucre.coercer.CoercerUtil;
import org.ubercraft.sucre.coercer.Coercer;
import org.ubercraft.sucre.coercer.CoercerFactory;
import org.ubercraft.sucre.coercer.DateCoercer;
import org.ubercraft.sucre.common.ExceptionUtil;
import org.ubercraft.sucre.reflect.Accessor;

public class AttributesUtil {

    public static final String attributeKeyFor(Accessor accessor) {
        AttributeKey key = accessor.getAnnotation(AttributeKey.class);
        if (key != null) {
            return key.value();
        }
        return accessor.getName();
    }

    public static Object defaultValueFor(Accessor accessor) {
        AttributeDefaultString stringDefault = accessor.getAnnotation(AttributeDefaultString.class);
        if (stringDefault != null) {
            if (accessor.getType() != String.class && accessor.getType() != File.class && accessor.getType() != Class.class //
                    && accessor.getType() != Date.class && !accessor.getType().isEnum()) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected String)");
            }
            return stringDefault.value();
        }
        AttributeDefaultBoolean booleanDefault = accessor.getAnnotation(AttributeDefaultBoolean.class);
        if (booleanDefault != null) {
            if (accessor.getType() != Boolean.class && accessor.getType() != boolean.class) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected boolean)");
            }
            return booleanDefault.value();
        }
        AttributeDefaultInt intDefault = accessor.getAnnotation(AttributeDefaultInt.class);
        if (intDefault != null) {
            if (accessor.getType() != Integer.class && accessor.getType() != int.class) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected int)");
            }
            return intDefault.value();
        }
        AttributeDefaultDouble doubleDefault = accessor.getAnnotation(AttributeDefaultDouble.class);
        if (doubleDefault != null) {
            if (accessor.getType() != Double.class && accessor.getType() != double.class) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected double)");
            }
            return doubleDefault.value();
        }
        AttributeDefaultLong longDefault = accessor.getAnnotation(AttributeDefaultLong.class);
        if (longDefault != null) {
            if (accessor.getType() != Long.class && accessor.getType() != long.class) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected long)");
            }
            return longDefault.value();
        }
        AttributeDefaultClass classDefault = accessor.getAnnotation(AttributeDefaultClass.class);
        if (classDefault != null) {
            if (accessor.getType() != Class.class) {
                throw new IllegalArgumentException("default type mismatch for: " + accessor + " (expected Class)");
            }
            return classDefault.value();
        }
        return null;
    }

    public static Coercer coercerFor(Accessor accessor) {
        AttributeCoercer ac = accessor.getAnnotation(AttributeCoercer.class);

        if (ac != null) {
            try {
                Class<? extends CoercerFactory> factoryType = ac.value();
                CoercerFactory factory = factoryType.newInstance();
                return factory.createCoercer(accessor);
            }
            catch (Exception e) {
                throw ExceptionUtil.asUnchecked(e);
            }
        }

        // java.util.Date gets some built-in treatment
        if (accessor.getType() == Date.class) {
            AttributeDateFormats adf = accessor.getAnnotation(AttributeDateFormats.class);
            if (adf != null) {
                TimeZone tz = null;
                AttributeDateTimeZone adtz = accessor.getAnnotation(AttributeDateTimeZone.class);
                if (adtz != null) {
                    String value = adtz.value();
                    if (!"DEFAULT".equals(value)) {
                        tz = TimeZone.getTimeZone(value);
                    }
                }
                if (tz == null) {
                    tz = TimeZone.getDefault();
                }
                return new DateCoercer(tz, adf.value());
            }
        }

        return CoercerUtil.DEFAULT_COERCER;
    }

    public static boolean isCoerceStrictFor(Accessor accessor) {
        return (accessor.getAnnotation(AttributeCoerceStrict.class) != null);
    }
}
