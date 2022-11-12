package run.halo.sitemap;

import java.util.List;
import reactor.core.publisher.Flux;

public interface SitemapEntryLister {
    Flux<SitemapEntry> list();
}
