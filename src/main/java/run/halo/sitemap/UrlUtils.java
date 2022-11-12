package run.halo.sitemap;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

/**
 * @author guqing
 * @since 1.0.0
 */
@UtilityClass
public final class UrlUtils {

    /**
     * <p>Your Sitemap file must be UTF-8 encoded (you can generally do this when you save the
     * file).</p>
     * As with all XML files, any data values (including URLs) must use entity escape codes for
     * the characters listed in the table below.
     */
    public static String escapeSitemapUrl(String url) {
        Assert.notNull(url, "The url must not be null");
        return url.replaceAll("&", "&amp;")
            .replaceAll("'", "&apos;")
            .replaceAll("\"", "&quot;")
            .replaceAll(">", "&gt;")
            .replaceAll("<", "&lt;");
    }

    public static URI toURI(String s) {
        if (s == null) {
            return null;
        }
        try {
            return new URI(escapeSitemapUrl(s));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
