import custom.DelayTaskExecutor;

import java.time.Duration;

public class Test {

    public static void main(String[] args) {
        DelayTaskExecutor delayTaskExecutor = new DelayTaskExecutor("Delay Task Executor 1");
        delayTaskExecutor.delayExecute(() -> System.out.println("running task"), Duration.ofSeconds(5));
        Runtime.getRuntime().exit(1);
    }

}