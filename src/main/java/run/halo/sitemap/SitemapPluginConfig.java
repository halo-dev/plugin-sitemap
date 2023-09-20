package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.net.MalformedURLException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.net.URL;

@Component
public class SitemapPluginConfig {

    private final ExternalUrlSupplier externalUrlSupplier;

    private final SettingFetcher settingFetcher;

    private final BaseSetting baseSetting;


    public SitemapPluginConfig(ExternalUrlSupplier externalUrlSupplier,
        SettingFetcher settingFetcher, ReactiveSettingFetcher reactiveSettingFetcher) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.settingFetcher = settingFetcher;
        this.reactiveSettingFetcher = reactiveSettingFetcher;
        this.baseSetting = this.settingFetcher.fetch(BaseSetting.GROUP, BaseSetting.class)
            .orElseGet(BaseSetting::new);
    }

    private final ReactiveSettingFetcher reactiveSettingFetcher;

    private final String defaultRule = "User-agent: *\nDisallow: /console";

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
                    double priority = 1.0;
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

    @Bean
    RouterFunction<ServerResponse> robotsTextFunction() {
        return RouterFunctions.route(GET("/robots.txt"),
            request -> reactiveSettingFetcher.get("robots").flatMap(setting -> {
                if (!setting.get("enable").asText().equals("true")) {
                    return ServerResponse.ok()
                        .cacheControl(CacheControl.noCache())
                        .contentType(MediaType.TEXT_PLAIN)
                        .bodyValue(defaultRule);
                }
                var uri = externalUrlSupplier.getURL(request.exchange().getRequest());
                var sitemapURL = "Sitemap: " + uri + "sitemap.xml";
                return ServerResponse.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(setting.get("rules").asText() + "\n" + sitemapURL);
            })
        );
    }
}
