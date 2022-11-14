package run.halo.sitemap;

import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author guqing
 * @since 2.0.0
 */
@Component
public class DefaultSitemapXmlSupplier implements Supplier<Mono<String>> {
    private final SitemapEntryLister sitemapEntryLister;

    public DefaultSitemapXmlSupplier(SitemapEntryLister sitemapEntryLister) {
        this.sitemapEntryLister = sitemapEntryLister;
    }

    @Override
    public Mono<String> get() {
        return sitemapEntryLister.list()
            .collectList()
            .map(sitemapEntries -> new SitemapBuilder()
                .buildSitemapXml(sitemapEntries));
    }
}
