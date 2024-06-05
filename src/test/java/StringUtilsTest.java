import utils.StringUtils;

import java.util.*;
import java.util.function.Function;

public class StringUtilsTest {

    public static void main(String[] args) {
        class C {
            String name = "C name";
        }

        class B {
            String name = "B name";
        }

        class A {
            String name = "A name";

            List<B> bList = List.of(new B());

            Map<String, C> cMap = Map.of("C", new C());
        }

        Map<Class<?>, Function<Object, String>> functionMap = new HashMap<>();
        functionMap.put(B.class, b -> "new B name");

        A a = new A();
        Map<String, String> result = StringUtils.readInstance(a, Arrays.asList("name", "bList", "cMap"), functionMap);
        System.out.println(result.values());
    }

}
