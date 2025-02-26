package external.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

public class CaffeineFactory {

    public static LoadingCache<Integer, Integer> COMMON_CACHE = Caffeine.newBuilder()
            .maximumSize(2)
            // 过期间隔，调用时触发删除
            .expireAfterWrite(Duration.ofSeconds(3))
            // 刷新间隔，调用是触发刷新
            .refreshAfterWrite(Duration.ofSeconds(1))
            // 驱逐监听回调
            .evictionListener((k, v, removalCause) -> System.out.printf("evict %s : %s, removal cause %s%n", k, v, removalCause))
            // build cache
            .build(i -> {
                System.out.println("reload cache");
                return i + 1;
            });

    public static void main(String[] args) throws InterruptedException {
        Integer result = COMMON_CACHE.get(1);
        Thread.sleep(5000);
        result = COMMON_CACHE.get(2);
        Thread.sleep(5000);
        System.out.println(result);
    }
}
