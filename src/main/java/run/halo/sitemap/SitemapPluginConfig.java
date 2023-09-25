package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
@AllArgsConstructor
public class SitemapPluginConfig {

    private final ExternalUrlSupplier externalUrlSupplier;

    private final ReactiveSettingFetcher reactiveSettingFetcher;

    private final String defaultRule = "User-agent: *\nDisallow: /console";

    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(CachedSitemapGetter cachedSitemapGetter) {
        return RouterFunctions.route(GET("/sitemap.xml")
                .and(accept(MediaType.TEXT_XML)), request -> {
                var url = externalUrlSupplier.getRaw();
                if (url == null) {
                    url = externalUrlSupplier.getURL(request.exchange().getRequest());
                }
                var options = SitemapGeneratorOptions.builder()
                    .siteUrl(url)
                    .build();
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
                var sitemapURL = "Sitemap: "
                    + StringUtils.appendIfMissing(uri.toString(), "/")
                    + "sitemap.xml";
                return ServerResponse.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(setting.get("rules").asText() + "\n" + sitemapURL);
            })
        );
    }
}
