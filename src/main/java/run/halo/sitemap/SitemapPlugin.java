package run.halo.sitemap;

import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;

/**
 * @author ryanwang
 * @since 2.0.0
 */
@Component
public class SitemapPlugin extends BasePlugin {

    public SitemapPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Autowired
    private CachedSitemapGetter cachedSitemapGetter;

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        cachedSitemapGetter.getCache().cleanUp();

    }
}
