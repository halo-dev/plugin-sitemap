package run.halo.sitemap;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.HaloPluginManager;
import run.halo.app.plugin.PluginApplicationContext;

/**
 * @author ryanwang
 * @since 2.0.0
 */
@Component
public class SitemapPlugin extends BasePlugin {

    public SitemapPlugin(PluginWrapper wrapper, PluginApplicationContext context) {
        super(wrapper);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
