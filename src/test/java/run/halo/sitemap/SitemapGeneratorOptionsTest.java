package run.halo.sitemap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SitemapGeneratorOptions}.
 *
 * @author guqing
 * @since 1.0.0
 */
class SitemapGeneratorOptionsTest {

    private final double priority = 1.0;

    @Test
    void transform() throws MalformedURLException {
        SitemapGeneratorOptions options = SitemapGeneratorOptions.builder()
            .siteUrl(new URL("https://halo.run"))
            .build();
        SitemapEntry entry = options.transform("/about");
        assertEquals("https://halo.run/about", entry.getLoc());
        assertEquals(ChangeFreqEnum.DAILY, entry.getChangefreq());
        assertEquals(priority, entry.getPriority());

        entry = options.transform("/archives");
        assertEquals("https://halo.run/archives", entry.getLoc());
        assertEquals(ChangeFreqEnum.DAILY, entry.getChangefreq());
        assertEquals(priority, entry.getPriority());

        entry = options.transform("/categories/ümlat/>&>中");
        assertEquals("https://halo.run/categories/%C3%BCmlat/&gt;&amp;&gt;%E4%B8%AD",
            entry.getLoc());
        assertEquals(ChangeFreqEnum.DAILY, entry.getChangefreq());
        assertEquals(priority, entry.getPriority());

        entry = options.transform("https://guqing.xyz/hello-中国<>&");
        assertEquals("https://guqing.xyz/hello-%E4%B8%AD%E5%9B%BD&amp;lt;&amp;gt;&amp;amp;",
            entry.getLoc());

        assertEquals(ChangeFreqEnum.DAILY, entry.getChangefreq());
        assertEquals(priority, entry.getPriority());
    }
}