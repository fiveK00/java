package custom;

import java.time.Duration;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 延时任务执行器
 */
public class DelayTaskExecutor {

    protected static class DelayTask implements Delayed, Runnable {

        private final long executeTime;

        private final Runnable task;

        public DelayTask(Runnable task, long delayTime, TimeUnit unit) {
            this.executeTime = TimeUnit.MILLISECONDS.convert(delayTime, unit) + System.currentTimeMillis();
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return executeTime - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }


    private final AtomicBoolean running;
    private final DelayQueue<DelayTask> delayTaskQueue;

    /**
     * 单线程执行器
     */
    protected static class SingleThreadExecutor extends Thread {

        private final DelayQueue<DelayTask> delayTaskQueue;

        public SingleThreadExecutor(DelayQueue<DelayTask> delayTaskQueue) {
            this.delayTaskQueue = delayTaskQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = delayTaskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private final SingleThreadExecutor singleThreadExecutor;

    public DelayTaskExecutor() {
        this.running = new AtomicBoolean(false);
        this.delayTaskQueue = new DelayQueue<>();
        this.singleThreadExecutor = new SingleThreadExecutor(delayTaskQueue);
    }

    public DelayTaskExecutor(String name) {
        this();
        this.singleThreadExecutor.setName(name);
    }

    public void delayExecute(Runnable task, Duration duration) {
        this.delayTaskQueue.add(new DelayTask(task, duration.toMillis(), TimeUnit.MILLISECONDS));
        this.doStart();
    }

    private void doStart() {
        if (running.compareAndSet(false, true)) {
            this.singleThreadExecutor.start();
        }
    }
}
