package run.halo.sitemap;

import io.micrometer.common.util.StringUtils;
import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UrlEntryMeta {
    private String url;

    /**
     * see also {@link SitemapGeneratorOptions#getPriority()}.
     */
    private Double priority;

    private Instant lastModifiedTime;

    public UrlEntryMeta(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }
        this.url = url;
    }
}
