package external.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.time.Duration;

public class CaffeineTemplate<K, V> {

    static LoadingCache<Integer, Integer> caffeineCache = Caffeine.newBuilder()
            .maximumSize(1)
//            .expireAfterWrite(Duration.ofSeconds(3))
            .refreshAfterWrite(Duration.ofSeconds(3))
            .evictionListener((k, v, removalCause) -> System.out.printf("evict %s : %s, removal cause %s%n", k, v, removalCause))
            .build(i -> {
                System.out.println("reload cache");
                return i + 1;
            });

    public static void main(String[] args) throws InterruptedException {
        Integer result = caffeineCache.get(1);

        Thread.sleep(5000);
        result = caffeineCache.get(1);
    }
}
