package run.halo.sitemap;

import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.Category;
import run.halo.app.core.extension.Post;
import run.halo.app.core.extension.SinglePage;
import run.halo.app.core.extension.Tag;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ExtensionOperator;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;

@Component
@AllArgsConstructor
public class DefaultSitemapEntryLister implements SitemapEntryLister {

    private final ReactiveExtensionClient client;
    private final SitemapGeneratorOptions options;

    @Override
    public Flux<SitemapEntry> list() {
        return Flux.mergeSequential(listPostUrls(), listCategoryUrls(), listTagUrls(),
                listSinglePageUrls())
            .concatWith(urlsForListPages())
            .distinct()
            .map(options::transform);
    }

    private Flux<String> listPostUrls() {
        return client.list(Post.class, post -> post.isPublished() && !post.isDeleted(), null)
            .map(post -> post.getStatusOrDefault().getPermalink());
    }

    private Flux<String> listSinglePageUrls() {
        return client.list(SinglePage.class, singlePage -> singlePage.isPublished()
                    && Objects.equals(false, singlePage.getSpec().getDeleted())
                    && ExtensionOperator.isNotDeleted().test(singlePage),
                null)
            .map(post -> post.getStatusOrDefault().getPermalink());
    }

    private Flux<String> listCategoryUrls() {
        return client.list(Category.class,
                category -> category.getMetadata().getDeletionTimestamp() == null, null)
            .map(category -> category.getStatusOrDefault().getPermalink());
    }

    private Flux<String> listTagUrls() {
        return client.list(Tag.class,
                tag -> tag.getMetadata().getDeletionTimestamp() == null, null)
            .map(tag -> tag.getStatusOrDefault().getPermalink());
    }

    private Flux<String> urlsForListPages() {
        // TODO 优化系统其他路由获取
        return client.fetch(ConfigMap.class, "system")
            .mapNotNull(ConfigMap::getData)
            .map(data -> {
                String routeRuleConfig = data.get(ThemeRouteRules.GROUP);
                ThemeRouteRules themeRouteRules =
                    JsonUtils.jsonToObject(routeRuleConfig, ThemeRouteRules.class);
                return List.of(StringUtils.prependIfMissing(themeRouteRules.getTags(), "/"),
                    StringUtils.prependIfMissing(themeRouteRules.getCategories(), "/"),
                    StringUtils.prependIfMissing(themeRouteRules.getArchives(), "/")
                );
            })
            .flatMapMany(Flux::fromIterable);
    }

    @Data
    public static class ThemeRouteRules {
        public static final String GROUP = "routeRules";

        private String categories;
        private String archives;
        private String post;
        private String tags;
    }
}
