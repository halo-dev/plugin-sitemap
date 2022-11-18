package run.halo.sitemap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@AllArgsConstructor
public class CachedSitemapGetter {

    private final Cache<String, String> cache = CacheBuilder.newBuilder()
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .initialCapacity(8)
        .maximumSize(8)
        .expireAfterWrite(Duration.ofSeconds(30))
        .build();

    private final DefaultSitemapEntryLister lister;

    public Mono<String> get() {
        String cacheKey = "sitemap";
        return Mono.fromCallable(() -> cache.get(cacheKey, () -> lister.list()
                .collectList()
                .map(entries -> {
                    String xml = new SitemapBuilder()
                        .buildSitemapXml(entries);
                    cache.put(cacheKey, xml);
                    return xml;
                })
                .defaultIfEmpty(StringUtils.EMPTY)
                .block()
            ))
            .subscribeOn(Schedulers.boundedElastic());
    }
}
