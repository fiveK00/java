package base.version17;

public class RecordClass {

    record A(String name) {
        A {
            name = "A:" + name;
        }
    }

    public static void main(String[] args) {
        A a = new A("record name");
    }
}
