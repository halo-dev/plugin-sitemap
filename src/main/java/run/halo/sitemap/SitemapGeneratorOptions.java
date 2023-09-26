package run.halo.sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import run.halo.app.infra.utils.PathUtils;

/**
 * Sitemap generator options.
 *
 * @author guqing
 * @since 1.0.0
 */
@Data
@Builder
public class SitemapGeneratorOptions {
    /**
     * All URLs in the generated sitemap(s) should appear under this base URL
     */
    @NonNull
    private URL siteUrl;

    @Builder.Default
    private String fileNamePrefix = "sitemap";

    @Builder.Default
    private boolean allowEmptySitemap = false;

    @Builder.Default
    private boolean allowMultipleSitemaps = true;

    @Builder.Default
    private DateTimeFormatter dateTimeFormatter = W3cDatetimeFormat.SECOND_FORMATTER;

    /**
     * Split large sitemap into multiple files by specifying sitemap size. Default 5000.
     */
    @Builder.Default
    private int sitemapSize = 5000;

    @Builder.Default
    private boolean autoValidate = false;

    @Builder.Default
    private boolean gzip = false;

    @Builder.Default
    private ChangeFreqEnum changefreq = ChangeFreqEnum.DAILY;

    /**
     * How to assign sitemap priorities:
     * <pre>
     * 1.0-0.8
     * Homepage, product information, landing pages.
     *
     * 0.7-0.4
     * News articles, some weather services, blog posts, category pages, pages that no site would be complete without.
     *
     * 0.3-0.0
     * FAQs, outdated info, old press releases, completely static pages that are still relevant enough to keep from deleting entirely.
     * </pre>
     *
     * @see
     * <a href="https://www.contentpowered.com/blog/xml-sitemap-priority-changefreq/">xml-sitemap-priority-changefreq</a>
     * @see
     * <a href="https://slickplan.com/blog/xml-sitemap-priority-changefreq">xml-sitemap-priority-changefreq</a>
     */
    @Builder.Default
    private double priority = 0.7;

    /**
     * <p>Array of relative paths (wildcard pattern supported) to exclude from listing on sitemap
     * .xml or sitemap-*.xml.</p>
     *
     * <p>e.g.: ['/page-0', '/page-*', '/private/*'].</p>
     * Apart from this option next-sitemap also offers a custom transform option which could be
     * used to exclude urls that match specific patterns
     */
    private Set<String> exclude;

    /**
     * Generate index sitemaps. Default true.
     */
    @Builder.Default
    private boolean generateIndexSitemap = true;

    public SitemapEntry transform(UrlEntryMeta context) {
        String escapedUrl = UrlUtils.escapeSitemapUrl(context.getUrl());
        String loc = UrlUtils.toURI(escapedUrl).normalize().toASCIIString();
        if (!PathUtils.isAbsoluteUri(loc)) {
            loc = getSiteUri().resolve(escapedUrl).normalize().toASCIIString();
        }

        var builder = SitemapEntry.builder()
            .loc(loc)
            .changefreq(changefreq);

        if (context.getPriority() != null) {
            builder.priority(context.getPriority());
        } else {
            builder.priority(priority);
        }

        if (context.getLastModifiedTime() != null) {
            builder.lastmod(
                W3cDatetimeFormat.format(context.getLastModifiedTime(), dateTimeFormatter));
        } else {
            builder.lastmod(W3cDatetimeFormat.format(Instant.now(), dateTimeFormatter));
        }

        return builder.build();
    }

    private URI getSiteUri() {
        try {
            return siteUrl.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
