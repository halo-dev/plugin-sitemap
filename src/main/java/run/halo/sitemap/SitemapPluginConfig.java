package run.halo.sitemap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.SettingFetcher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
public class SitemapPluginConfig {

    private final ExternalUrlSupplier externalUrlSupplier;

    private final SettingFetcher settingFetcher;

    private final BaseSetting baseSetting;


    public SitemapPluginConfig(ExternalUrlSupplier externalUrlSupplier,
        SettingFetcher settingFetcher) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.settingFetcher = settingFetcher;
        this.baseSetting = this.settingFetcher.fetch(BaseSetting.GROUP, BaseSetting.class)
            .orElseGet(BaseSetting::new);
    }


    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(CachedSitemapGetter cachedSitemapGetter) {
        return RouterFunctions.route(GET("/sitemap.xml")
                .and(accept(MediaType.TEXT_XML)), request -> {
                String siteUrl = baseSetting.getSiteUrl();

                SitemapGeneratorOptions options;
                try {
                    URL url;
                    if (StringUtils.isNotEmpty(siteUrl)) {
                        url = new URL(siteUrl);
                    } else {
                        var uri = externalUrlSupplier.get();
                        if (!uri.isAbsolute()) {
                            uri = request.exchange().getRequest().getURI().resolve(uri);
                        }
                        url = uri.toURL();
                    }
                    String prioritySetting = baseSetting.getPriority().trim();
                    Double priority = 1.0;
                    if (StringUtils.isNotEmpty(prioritySetting)) {
                        priority = Double.parseDouble(prioritySetting);
                    }
                    options = SitemapGeneratorOptions.builder()
                        .siteUrl(url)
                        .priority(priority)
                        .build();
                } catch (MalformedURLException e) {
                    throw Exceptions.propagate(e);
                }
                return cachedSitemapGetter.get(options)
                    .flatMap(sitemap -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_XML).bodyValue(sitemap));
            }
        );
    }

    private URL toUrl(String siteUrl) {
        try {
            String url = siteUrl.trim();
            String[] protocols = url.split(":");
            String host = protocols[1].split("//")[1];
            int port = 80;
            if (2 < protocols.length) {
                port = Integer.parseInt(protocols[2]);
            }
            return new URL(protocols[0], host, port, "/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
