package org.ubercraft.sucre.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.ubercraft.sucre.coercer.CoercerException;

public class AttributesTest {

    private static interface TestMe {

        String getHello();

        String getNumber();

        @AttributeKey("my.key")
        String getSomeString();

        @AttributeDefaultInt(42)
        int getTheAnswer();

        void setTheAnswer(int answer);

        long getNotFussy();

        void setNotFussy(String string);
    }

    @Test
    public void testMe() {
        // start with a map
        Map<Object, Object> values = new HashMap<Object, Object>();
        values.put("hello", "World");
        values.put("number", 12345);
        values.put("my.key", "my.value");
        values.put("notFussy", (byte)3);

        // test me
        TestMe me = Attributes.backedBy(values).proxy(TestMe.class);

        // hello world
        assertEquals("World", me.getHello());

        // coerce int to String
        assertEquals("12345", me.getNumber());

        // use a non-default key
        assertEquals("my.value", me.getSomeString());

        // use a default value
        assertEquals(42, me.getTheAnswer());

        // modify a value
        me.setTheAnswer(101);
        assertEquals(101, me.getTheAnswer());

        // not fussy
        assertEquals(3L, me.getNotFussy());

        // still not
        me.setNotFussy("4");
        assertEquals(4L, me.getNotFussy());
    }

    private static enum TestEnum {
        ABC,
        XYZ
    }

    private static interface TestAPI {

        String getString();

        void setString(String value);

        boolean getBoolean();

        void setBoolean(boolean value);

        int getInt();

        void setInt(int value);

        double getDouble();

        void setDouble(double value);

        long getLong();

        void setLong(long value);

        @AttributeKey("string.key")
        String getReadOnlyString();

        TestEnum getEnum();

        void setEnum(TestEnum value);
    }

    @Test
    public void testAttributesProxy() throws Exception {
        Attributes attr = new Attributes();
        TestAPI test = attr.proxy(TestAPI.class);
        assertEquals(null, test.getString());
        assertEquals(false, test.getBoolean());
        assertEquals(0, test.getInt());
        assertEquals(0.0D, test.getDouble(), 0.0D);
        assertEquals(0L, test.getLong());
        assertEquals(null, test.getReadOnlyString());
        assertEquals(null, test.getEnum());
        test.setString("x");
        test.setBoolean(true);
        test.setInt(7);
        test.setDouble(7.7D);
        test.setLong(7L);
        test.setEnum(TestEnum.ABC);
        Map<Object, Object> values = new HashMap<Object, Object>();
        values.put("string.key", "hello");
        attr.addAll(values);
        assertEquals("x", test.getString());
        assertEquals(true, test.getBoolean());
        assertEquals(7, test.getInt());
        assertEquals(7.7D, test.getDouble(), 0.0D);
        assertEquals(7L, test.getLong());
        assertEquals("hello", test.getReadOnlyString());
        assertEquals(TestEnum.ABC, test.getEnum());
    }

    private static interface TestAPIWithDefaults {

        @AttributeDefaultString("x")
        String getString();

        void setString(String value);

        @AttributeDefaultBoolean(true)
        boolean getBoolean();

        void setBoolean(boolean value);

        @AttributeDefaultInt(7)
        int getInt();

        void setInt(int value);

        @AttributeDefaultDouble(7.7D)
        double getDouble();

        void setDouble(double value);

        @AttributeDefaultLong(7L)
        long getLong();

        void setLong(long value);

        @AttributeKey("string.key")
        @AttributeDefaultString("y")
        String getReadOnlyString();

        @AttributeDefaultString("XYZ")
        TestEnum getEnum();

        @AttributeDefaultClass(java.util.List.class)
        Class<?> getType();

        void setType(Class<?> type);

        @AttributeDefaultString("java.util.Set")
        Class<?> getReadOnlyType();
    }

    @Test
    public void testAttributesProxyWithDefaults() throws Exception {
        Attributes attr = new Attributes();
        TestAPIWithDefaults test = attr.proxy(TestAPIWithDefaults.class);
        assertEquals("x", test.getString());
        assertEquals(true, test.getBoolean());
        assertEquals(7, test.getInt());
        assertEquals(7.7D, test.getDouble(), 0.0D);
        assertEquals(7L, test.getLong());
        assertEquals("y", test.getReadOnlyString());
        assertEquals(TestEnum.XYZ, test.getEnum());
        assertSame(java.util.List.class, test.getType());
        assertSame(java.util.Set.class, test.getReadOnlyType());
        test.setString(null);
        test.setBoolean(false);
        test.setInt(0);
        test.setDouble(0.0D);
        test.setLong(0L);
        test.setType(java.util.Map.class);
        Map<Object, Object> values = new HashMap<Object, Object>();
        values.put("string.key", "world");
        attr.addAll(values);
        assertEquals(null, test.getString());
        assertEquals(false, test.getBoolean());
        assertEquals(0, test.getInt());
        assertEquals(0.0D, test.getDouble(), 0.0D);
        assertEquals(0L, test.getLong());
        assertEquals("world", test.getReadOnlyString());
        assertSame(java.util.Map.class, test.getType());
    }

    private static interface TestBigAPI {

        Boolean getBoolean();

        void setBoolean(Boolean value);

        Integer getInt();

        void setInt(Integer value);

        Double getDouble();

        void setDouble(Double value);
    }

    @Test
    public void testBigAttributesProxy() throws Exception {
        Attributes attr = new Attributes();
        TestBigAPI test = attr.proxy(TestBigAPI.class);
        assertEquals(null, test.getBoolean());
        assertEquals(null, test.getInt());
        assertEquals(null, test.getDouble());
        test.setBoolean(true);
        test.setInt(7);
        test.setDouble(7.7D);
        assertEquals(true, test.getBoolean());
        assertEquals(new Integer(7), test.getInt());
        assertEquals(new Double(7.7D), test.getDouble(), 0.0D);
    }

    private static interface TestBigAPIWithDefaults {

        @AttributeDefaultBoolean(true)
        Boolean getBoolean();

        void setBoolean(Boolean value);

        @AttributeDefaultInt(7)
        Integer getInt();

        void setInt(Integer value);

        @AttributeDefaultDouble(7.7D)
        Double getDouble();

        void setDouble(Double value);
    }

    @Test
    public void testBigAttributesProxyWithDefaults() throws Exception {
        Attributes attr = new Attributes();
        TestBigAPIWithDefaults test = attr.proxy(TestBigAPIWithDefaults.class);
        assertEquals(true, test.getBoolean());
        assertEquals(new Integer(7), test.getInt());
        assertEquals(new Double(7.7D), test.getDouble(), 0.0D);
        test.setBoolean(null);
        test.setInt(null);
        test.setDouble(null);
        assertEquals(null, test.getBoolean());
        assertEquals(null, test.getInt());
        assertEquals(null, test.getDouble());
    }

    private static interface TestAutoCoerceAPI {

        @AttributeKey("int.key")
        int getInt();

        @AttributeKey("double.key")
        double getDouble();

        @AttributeKey("bool.key")
        boolean getBoolean();
    }

    @Test
    public void testProxyAutoCoerce() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put("int.key", "5");
        values.put("double.key", "6.3");
        values.put("bool.key", "true");
        Attributes attr = new Attributes(values);
        TestAutoCoerceAPI test = attr.proxy(TestAutoCoerceAPI.class);
        assertEquals(new Integer(5), new Integer(test.getInt()));
        assertEquals(new Double(6.3D), new Double(test.getDouble()));
        assertEquals(new Boolean(true), test.getBoolean());
    }

    private static interface TestBigAutoCoerceAPI {

        @AttributeKey("int.key")
        Integer getInt();

        @AttributeKey("double.key")
        Double getDouble();

        @AttributeKey("bool.key")
        Boolean getBoolean();
    }

    @Test
    public void testBigProxyAutoCoerce() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put("int.key", "5");
        values.put("double.key", "6.3");
        values.put("bool.key", "true");
        Attributes attr = new Attributes(values);
        TestBigAutoCoerceAPI test = attr.proxy(TestBigAutoCoerceAPI.class);
        assertEquals(new Integer(5), new Integer(test.getInt()));
        assertEquals(new Double(6.3D), new Double(test.getDouble()));
        assertEquals(new Boolean(true), test.getBoolean());
    }

    public static interface TestSuperInterface {

        @AttributeKey("int.key")
        int getInt();
    }

    public static interface TestSubInterface extends TestSuperInterface {

    }

    @Test
    public void testProxySubInterface() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put("int.key", "5");
        Attributes attr = new Attributes(values);
        TestSuperInterface test = attr.proxy(TestSubInterface.class);
        assertEquals(5, test.getInt());
    }

    public static interface TestDateInterface {

        @AttributeDateFormats({
                "yyyy/MM/dd", "yyyy MMM dd"
        })
        @AttributeDateTimeZone("DEFAULT")
        Date getDate();

        Date setDate(Date date);
    }

    @Test
    public void testDateFormats() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Attributes attr = Attributes.backedBy(values);
        TestDateInterface test = attr.proxy(TestDateInterface.class);
        assertEquals(null, test.getDate());
        String dateString = "2011/06/12";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = dateFormat.parse(dateString);
        values.put("date", dateString);
        assertEquals(date, test.getDate());
        dateString = "2011 Jun 12";
        dateFormat = new SimpleDateFormat("yyyy MMM dd");
        date = dateFormat.parse(dateString);
        values.put("date", dateString);
        assertEquals(date, test.getDate());
        values.put("date", "lkjsdf");
        assertEquals(null, test.getDate());
    }

    public static interface TestStrictInterface {

        @AttributeDateFormats({
            "yyyy MMM dd"
        })
        @AttributeCoerceStrict
        Date getStrictDate();
    }

    @Test(expected = CoercerException.class)
    public void testStrictDateFormats() throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Attributes attr = Attributes.backedBy(values);
        TestStrictInterface test = attr.proxy(TestStrictInterface.class);
        values.put("strictDate", "lkjsdf");
        test.getStrictDate();
    }
}
