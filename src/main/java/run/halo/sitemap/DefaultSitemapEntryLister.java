package run.halo.sitemap;

import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.halo.app.core.extension.Category;
import run.halo.app.core.extension.Post;
import run.halo.app.core.extension.SinglePage;
import run.halo.app.core.extension.Tag;
import run.halo.app.extension.ExtensionOperator;
import run.halo.app.extension.ReactiveExtensionClient;

@Component
@AllArgsConstructor
public class DefaultSitemapEntryLister implements SitemapEntryLister {

    private final ReactiveExtensionClient client;
    private final SitemapGeneratorOptions options;

    @Override
    public Flux<SitemapEntry> list() {
        return Flux.mergeSequential(listPostUrls(), listCategoryUrls(), listTagUrls(), listSinglePageUrls())
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
}
