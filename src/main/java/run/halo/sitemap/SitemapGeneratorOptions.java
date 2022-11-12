package run.halo.sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriUtils;
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
    private DateTimeFormatter dateTimeFormatter = W3cDatetimeFormat.MILLISECOND_FORMATTER;

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

    @Builder.Default
    private double priority = 0.7;

    /**
     * Add &lt;lastmod/&gt; property. Default true
     */
    @Builder.Default
    private boolean autoLastmod = true;

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

    public SitemapEntry transform(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        String escapedUrl = UrlUtils.escapeSitemapUrl(url);
        String loc = UrlUtils.toURI(escapedUrl).normalize().toASCIIString();
        if (!PathUtils.isAbsoluteUri(loc)) {
            loc = getSiteUri().resolve(escapedUrl).normalize().toASCIIString();
        }

        SitemapEntry.SitemapEntryBuilder builder = SitemapEntry.builder()
            .loc(loc)
            .changefreq(changefreq)
            .priority(priority);

        if (dateTimeFormatter != null && autoLastmod) {
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
