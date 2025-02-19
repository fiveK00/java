package base.util;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class ReflectionUtils {

    public static List<Field> classFields(Class<?> classType) {
        List<Field> fields = new ArrayList<>();
        while (!classType.isAssignableFrom(Object.class)) {
            fields.addAll(List.of(classType.getDeclaredFields()));
            classType = classType.getSuperclass();
        }
        return fields;
    }

    public static List<Field> classFields(Class<?> classType, List<String> fieldNames) {
        List<Field> fields = classFields(classType);
        return fields.stream().filter(e -> fieldNames.contains(e.getName())).collect(Collectors.toList());
    }

    public static List<List<String>> batchExtractValues(List<Object> dataList, List<String> fieldNames) {
        return batchExtractValues(dataList, fieldNames, Collections.emptyMap());
    }

    public static List<List<String>> batchExtractValues(List<Object> dataList, List<String> fieldNames, Map<Class<Object>, Function<Object, String>> convertors) {
        if (Objects.isNull(dataList)) {
            return Collections.emptyList();
        }
        if (Objects.isNull(convertors)) {
            convertors = Collections.emptyMap();
        }
        List<List<String>> result = new ArrayList<>();
        Map<Class<?>, List<Field>> classFieldsMap = new HashMap<>();
        for (Object obj : dataList) {
            Class<?> classType = obj.getClass();
            List<Field> fields = classFieldsMap.getOrDefault(classType, new ArrayList<>());
            if (!classFieldsMap.containsKey(classType)) {
                List<Field> allFields = classFields(obj.getClass());
                Map<String, Field> fieldNameMap = allFields.stream().collect(Collectors.toMap(Field::getName, Function.identity()));
                for (String fieldName : fieldNames) {
                    Field field = fieldNameMap.get(fieldName);
                    if (Objects.nonNull(field)) {
                        field.setAccessible(true);
                    }
                    fields.add(field);
                }
                classFieldsMap.put(classType, fields);
            }

            try {
                List<String> values = new ArrayList<>();
                for (Field field : fields) {
                    if (Objects.isNull(field)) {
                        values.add(null);
                        continue;
                    }
                    Object value = field.get(obj);
                    if (Objects.isNull(value)) {
                        values.add(null);
                        continue;
                    }
                    Function<Object, String> convertor = convertors.getOrDefault(classType, Object::toString);
                    values.add(convertor.apply(value));
                }
                result.add(values);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Field get value failed:" + e.getMessage());
            }
        }
        return result;
    }

}
