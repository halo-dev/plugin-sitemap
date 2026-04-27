package run.halo.sitemap;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CachedSitemapGetter {

    private final AsyncCache<String, String> cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofSeconds(30))
        .maximumSize(8)
        .buildAsync();

    private final DefaultSitemapEntryLister lister;

    public Mono<String> get(SitemapGeneratorOptions options) {
        String key = options.getSiteUrl().toString();
        return Mono.fromFuture(() ->
            cache.get(key, (k, executor) ->
                lister.list(options)
                    .collectList()
                    .map(entries -> new SitemapBuilder().buildSitemapXml(entries))
                    .defaultIfEmpty("")
                    .toFuture()
            )
        );
    }
}
