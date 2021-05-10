package de.novatec.http11.delegate.support.reflection;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public final class ReflectionSupport {

    private ReflectionSupport() {
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean doIfValueIsPresent(Object obj, String fieldName, Class<T> fieldType, Consumer<T> consumer) {
        final Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
        if (field != null) {
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            final Object value = ReflectionUtils.getField(field, obj);
            if(value != null && fieldType.isAssignableFrom(value.getClass())) {
                consumer.accept((T) value);
            }
            return true;
        }
        return false;
    }
}
