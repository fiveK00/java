package utils;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 */
public class ReflectionUtils {

    public static List<Field> fieldsByName(Object obj, List<String> names){
        List<Field> result = new ArrayList<>();

        Class<?> clazz = obj.getClass();
        try {
            for(String name : names){
                result.add(clazz.getDeclaredField(name));
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
