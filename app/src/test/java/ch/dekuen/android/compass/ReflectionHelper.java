package ch.dekuen.android.compass;

import java.lang.reflect.Field;

public abstract class ReflectionHelper {
    private ReflectionHelper() {
    }

    public static Object getFieldValue(Object o, String fieldName) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(o);
            field.setAccessible(accessible);
            return value;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFieldValue(Object o, String fieldName, Object value) {
        try {
            Field field = o.getClass().getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(o, value);
            field.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
