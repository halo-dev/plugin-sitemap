package run.halo.sitemap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SitemapBuilder}.
 *
 * @author guqing
 * @since 1.0.0
 */
class SitemapBuilderTest {

    @Test
    void buildSitemapXml() {
        List<SitemapEntry> entries = new ArrayList<>();
        entries.add(SitemapEntry.builder()
            .loc("https://halo.run/about")
            .lastmod("2022-11-12T13:57:43.898+0800")
            .changefreq(ChangeFreqEnum.DAILY)
            .priority(0.7)
            .build());
        entries.add(SitemapEntry.builder()
            .loc("https://halo.run/categories")
            .lastmod("2022-11-12T13:57:43.898+0800")
            .changefreq(ChangeFreqEnum.DAILY)
            .priority(0.7)
            .build());
        String s = new SitemapBuilder().buildSitemapXml(entries);
        assertEquals("""
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                    xmlns:news="http://www.google.com/schemas/sitemap-news/0.9"
                    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                    xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd"
                    xmlns:mobile="http://www.google.com/schemas/sitemap-mobile/1.0"
                    xmlns:image="http://www.google.com/schemas/sitemap-image/1.1"
                    xmlns:video="http://www.google.com/schemas/sitemap-video/1.1">
            <url><loc>https://halo.run/about</loc>
            <lastmod>2022-11-12T13:57:43.898+0800</lastmod>
            <changefreq>daily</changefreq>
            <priority>0.7</priority>
            </url>
            <url><loc>https://halo.run/categories</loc>
            <lastmod>2022-11-12T13:57:43.898+0800</lastmod>
            <changefreq>daily</changefreq>
            <priority>0.7</priority>
            </url>

            </urlset>
            """, s);
    }
}