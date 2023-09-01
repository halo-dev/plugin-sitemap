package run.halo.sitemap;

import lombok.Data;
import lombok.ToString;

/**
 * run.halo.sitemap
 *
 * @author Carol, 2023/9/1
 */
@Data
@ToString
public class BaseSetting {

    public static final String CONFIG_MAP_NAME = "plugin-sitemap-config";
    public static final String GROUP = "basic";

    private Boolean enable = Boolean.FALSE;

    private String siteUrl = "";

}
