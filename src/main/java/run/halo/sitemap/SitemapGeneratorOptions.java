package run.halo.sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
    private DateTimeFormatter dateTimeFormatter = W3cDatetimeFormat.SECOND_FORMATTER;

    /**
     * <p>Array of relative paths (wildcard pattern supported) to exclude from listing on sitemap
     * .xml or sitemap-*.xml.</p>
     *
     * <p>e.g.: ['/page-0', '/page-*', '/private/*'].</p>
     */
    private Set<String> exclude;

    public SitemapEntry transform(UrlEntryMeta context) {
        String escapedUrl = UrlUtils.escapeSitemapUrl(context.getUrl());
        String loc = UrlUtils.toURI(escapedUrl).normalize().toASCIIString();
        if (!PathUtils.isAbsoluteUri(loc)) {
            loc = getSiteUri().resolve(escapedUrl).normalize().toASCIIString();
        }

        var builder = SitemapEntry.builder().loc(loc);

        if (context.getLastModifiedTime() != null) {
            builder.lastmod(
                W3cDatetimeFormat.format(context.getLastModifiedTime(), dateTimeFormatter));
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
