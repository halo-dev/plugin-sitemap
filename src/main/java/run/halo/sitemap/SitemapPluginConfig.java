package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.net.MalformedURLException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.Exceptions;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.ReactiveSettingFetcher;

import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Component
@AllArgsConstructor
public class SitemapPluginConfig {

    private final ExternalUrlSupplier externalUrlSupplier;

    private final DefaultSettingFetcher settingFetcher;

    private final ReactiveSettingFetcher reactiveSettingFetcher;

    private final String defaultRule = "User-agent: *\nDisallow: /console";

    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(CachedSitemapGetter cachedSitemapGetter) {
        return RouterFunctions.route(GET("/sitemap.xml")
                .and(accept(MediaType.TEXT_XML)), request -> {
                BaseSetting basePushSetting =
                    settingFetcher.fetch(BaseSetting.CONFIG_MAP_NAME, BaseSetting.GROUP,
                        BaseSetting.class).orElseGet(BaseSetting::new);

                String siteUrl = basePushSetting.getSiteUrl();
                var uri = externalUrlSupplier.get();
                if (!uri.isAbsolute()) {
                    uri = request.exchange().getRequest().getURI().resolve(uri);
                }
                SitemapGeneratorOptions options;
                try {
                    options = SitemapGeneratorOptions.builder()
                        .siteUrl(siteUrl.isEmpty() ? uri.toURL() : toUrl(siteUrl))
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
