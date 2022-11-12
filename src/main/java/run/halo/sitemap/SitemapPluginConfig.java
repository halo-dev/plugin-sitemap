package run.halo.sitemap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.infra.ExternalUrlSupplier;

@Component
@AllArgsConstructor
public class SitemapPluginConfig {

    @Bean
    public SitemapGeneratorOptions sitemapGeneratorOptions()
        throws MalformedURLException {
        URI siteUrl = UrlUtils.toURI("http://localhost:8090");
        return SitemapGeneratorOptions.builder()
            .siteUrl(siteUrl.toURL())
            .build();
    }

    @Bean
    RouterFunction<ServerResponse> sitemapRouterFunction(SitemapEntryLister sitemapEntryLister) {
        return RouterFunctions.route(GET("/sitemap.xml")
            .and(accept(MediaType.TEXT_XML)), request -> sitemapEntryLister.list()
            .collectList()
            .flatMap(sitemapEntries -> {
                String content = new SitemapBuilder().buildSitemapXml(sitemapEntries);
                return ServerResponse.ok().bodyValue(content);
            })
        );
    }
}
