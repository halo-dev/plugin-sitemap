package run.halo.sitemap;

import reactor.core.publisher.Flux;

public interface SitemapEntryLister {
    Flux<SitemapEntry> list(SitemapGeneratorOptions options);
}
