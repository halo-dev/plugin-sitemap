package run.halo.sitemap;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.SettingFetcher;

/**
 * run.halo.sitemap
 *
 * @author Carol, 2023/9/1
 */
@Component
@AllArgsConstructor
public class SitemapUrlSetting {

    @Autowired
    private SettingFetcher settingFetcher;

    public BaseSetting getSettingConfig() {
        return settingFetcher.fetch(BaseSetting.GROUP, BaseSetting.class)
            .orElseGet(BaseSetting::new);
    }

}
