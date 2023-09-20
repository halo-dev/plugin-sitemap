package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.plugin.ReactiveSettingFetcher;

@Component
public class SitemapPluginConfig {

    private final ExternalUrlSupplier externalUrlSupplier;


    public SitemapPluginConfig(ExternalUrlSupplier externalUrlSupplier,
        ReactiveSettingFetcher reactiveSettingFetcher) {
        this.externalUrlSupplier = externalUrlSupplier;
        this.reactiveSettingFetcher = reactiveSettingFetcher;
    }

    private final ReactiveSettingFetcher reactiveSettingFetcher;

    private final String defaultRule = "User-agent: *\nDisallow: /console";

    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(CachedSitemapGetter cachedSitemapGetter) {
        return RouterFunctions.route(GET("/sitemap.xml").and(accept(MediaType.TEXT_XML)),
            request -> reactiveSettingFetcher.get("basic").flatMap(setting -> {
                SitemapGeneratorOptions options;
                try {
                    options = createOptions(request, setting);
                } catch (MalformedURLException e) {
                    return reactor.core.publisher.Mono.error(new RuntimeException(e));
                }
                return cachedSitemapGetter.get(options)
                    .flatMap(sitemap -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_XML).bodyValue(sitemap));
            }));
    }

    private SitemapGeneratorOptions createOptions(ServerRequest request,
        JsonNode setting) throws MalformedURLException {
        SitemapGeneratorOptions options;
        URL url = null;
        double priority = 1.0;
        if (!setting.get("enable").asText().equals("true")) {
            var uri = externalUrlSupplier.get();
            if (!uri.isAbsolute()) {
                uri = request.exchange().getRequest().getURI().resolve(uri);
            }
            url = uri.toURL();
        } else {
            String siteUrl = setting.get("siteUrl").textValue().trim();
            String prioritySetting = setting.get("priority").textValue().trim();
            if (StringUtils.isNotEmpty(siteUrl)) {
                url = new URL(siteUrl);
            }
            if (StringUtils.isNotEmpty(prioritySetting)) {
                priority = Double.parseDouble(prioritySetting);
            }
        }
        options = SitemapGeneratorOptions.builder()
            .siteUrl(url)
            .priority(priority)
            .build();
        return options;
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
