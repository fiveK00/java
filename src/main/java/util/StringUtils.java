package util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringUtils {

    private static final String DELIMITER = ",";

    private static final String DEFAULT_VALUE = "--";

    private static String serialize(Object value, Map<Class<?>, Function<Object, String>> customSerializer) {
        if(Objects.isNull(value)){
            return DEFAULT_VALUE;
        }

        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).stream()
                    .map(e -> serialize(e, customSerializer)).collect(Collectors.joining(DELIMITER));
        } else if (value instanceof Map<?,?>) {
            return ((Map<?, ?>) value).entrySet().stream()
                    .map(e -> serialize(e.getKey(), customSerializer) + ":" + serialize(e.getValue(), customSerializer))
                    .collect(Collectors.joining(DELIMITER));
        } else {
            return Optional.ofNullable(customSerializer.get(value.getClass()))
                    .map(f -> f.apply(value)).orElseGet(value::toString);
        }
    }

    public static Map<String, String> readInstance(Object obj, List<String> fieldNames, Map<Class<?>, Function<Object, String>> customReaders){
        List<Field> fields = ReflectionUtils.fieldsByName(obj, fieldNames);

        return readFields(obj, fields, customReaders);
    }

    private static Map<String, String> readFields(Object obj, List<Field> fields, Map<Class<?>, Function<Object, String>> customReaders){
        Map<String, String> result = new HashMap<>();
        try {
            for(Field field : fields){
                if(!field.canAccess(obj)){
                    field.setAccessible(true);
                }
                result.put(field.getName(), serialize(field.get(obj), customReaders));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static List<Map<String, String>> batchReadInstance(List<Object> objs, List<String> fieldNames, Map<Class<?>, Function<Object, String>> customReaders){
        List<Field> fields = ReflectionUtils.fieldsByName(objs.get(0), fieldNames);

        List<Map<String, String>> result = new ArrayList<>();
        for(Object obj : objs){
            result.add(readFields(obj, fields, customReaders));
        }

        return result;
    }
}
