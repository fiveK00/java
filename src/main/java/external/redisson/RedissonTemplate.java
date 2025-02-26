package external.redisson;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redisson对于基本操作的支持较差，适用于分布式锁、分布式数据相关操作，与Lettuce、Jedis配合使用
 * RLock key - map {uuid:threadId : lockTimes} set 30 sec per 1- sec
 */
public class RedissonTemplate {

    public static Map<String, RLock> lockMap = new HashMap<>();

    public static Config config = new Config();

    static {
        config.useSingleServer()
                // use "redis://" for Redis connection
                // use "valkey://" for Valkey connection
                // use "valkeys://" for Valkey SSL connection
                // use "rediss://" for Redis SSL connection
                .setAddress("redis://"+RedisConfig.address)
                .setDatabase(RedisConfig.database)
                .setUsername(RedisConfig.username)
                .setPassword(RedisConfig.password);
    }

    // Sync and Async API
    public static RedissonClient redisson = Redisson.create(config);

    // Reactive API
    public static RedissonReactiveClient redissonReactive = redisson.reactive();

    // RxJava3 API
    public static RedissonRxClient redissonRx = redisson.rxJava();

    public static Thread newThreadTryLock(RLock lock){
        Thread thread = new Thread(){
            @Override
            public void run() {
                System.out.println(getName() + ":try get lock");
                try {
                    if(lock.tryLock(2, TimeUnit.SECONDS)){
                        System.out.println(getName() + ":get lock");
                        System.out.println("run business");
                    }else {
                        System.out.println(getName() + ":abandon lock");
                    }
                } catch (InterruptedException e) {
                    System.out.println(getName() + ":exception " + e.getMessage());
                    throw new RuntimeException(e);
                }finally {
                    if(lock.isHeldByCurrentThread()){
                        lock.unlock();
                    }
                }
            }
        };

        thread.run();
        return thread;
    }

    public static void main(String[] args) {
        String key = "redis_lock_x";
        RLock rLock = redisson.getLock(key);
        rLock.tryLock();
//        Thread thread1 = newThreadTryLock(rLock);
//        Thread thread2 = newThreadTryLock(rLock);
//        thread1.join();
//        thread2.join();
        System.out.println("end");
        rLock.unlock();
        redisson.shutdown();
    }
}
