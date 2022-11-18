package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.app.infra.utils.JsonUtils;
import run.halo.app.plugin.SettingFetcher;

@Component
@AllArgsConstructor
public class SitemapPluginConfig {
    private final ExternalUrlSupplier externalUrlSupplier;

    @Bean
    public SitemapGeneratorOptions sitemapGeneratorOptions()
        throws MalformedURLException {
        URI siteUri = externalUrlSupplier.get();
        return SitemapGeneratorOptions.builder()
            .siteUrl(siteUri.toURL())
            .build();
    }

    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(
        CachedSitemapGetter cachedSitemapGetter) {
        return RouterFunctions.route(GET("/sitemap.xml")
            .and(accept(MediaType.TEXT_XML)), request -> cachedSitemapGetter.get()
            .flatMap(sitemap -> ServerResponse.ok()
                .contentType(MediaType.TEXT_XML).bodyValue(sitemap))
        );
    }
}
