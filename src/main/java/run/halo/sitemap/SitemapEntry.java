package run.halo.sitemap;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author guqing
 * @since 1.0.0
 */
@Data
@Builder
public class SitemapEntry {
    /**
     * <p>Parent tag for each URL entry. The remaining tags are children of this tag.</p>
     * required.
     */
    private String loc;

    private String lastmod;

    private ChangeFreqEnum changefreq;

    private Double priority;
}
