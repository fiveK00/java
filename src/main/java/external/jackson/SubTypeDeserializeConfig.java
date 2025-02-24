package external.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

public class SubTypeDeserializeConfig {

    /**
     * 通过全类型名称指定反序列化子类型
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type", defaultImpl = A.class)
    public static class A {
        public int a = 1;
    }

    public static class A1 extends A {
        public int a1 = 2;
    }

    public static void testToInstanceByClassName(){
        String json = JsonUtil.toJsonString(new A1());
        System.out.println(json);
        A result = JsonUtil.jsonToInstance(json, A.class);
        System.out.println(result instanceof A1);
    }

    /**
     * 通过自定义类型名称反序列化子类型，适合非公共依赖下使用
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = B.class)
    @JsonSubTypes({@JsonSubTypes.Type(value = B1.class)})
    public static class B {
        public int b = 1;
    }

    @JsonTypeName("B1")
    public static class B1 extends B {
        public int b1 = 2;
    }

    public static void testToInstanceByTypeName(){
        String json = JsonUtil.toJsonString(new B1());
        System.out.println(json);
        B result = JsonUtil.jsonToInstance(json, B.class);
        System.out.println(result instanceof B1);
    }

    public static void main(String[] args) {
        testToInstanceByClassName();
        testToInstanceByTypeName();
    }
}
