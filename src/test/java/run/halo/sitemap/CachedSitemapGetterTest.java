package run.halo.sitemap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
public class CachedSitemapGetterTest {
    @Mock
    private DefaultSitemapEntryLister lister;
    private CachedSitemapGetter getter;

    @BeforeEach
    void setUp() {
        when(lister.list(any())).thenReturn(
            Flux.just(SitemapEntry.builder().loc("http://localhost:8090/about").build()));
        getter = new CachedSitemapGetter(lister);
    }

    @Test
    void get() throws InterruptedException, ExecutionException {
        var options = mock(SitemapGeneratorOptions.class);

        getter.get(options).block();
        verify(lister).list(options);

        var executorService = Executors.newCachedThreadPool();

        List<? extends Future<?>> futures = IntStream.range(0, 10)
            .mapToObj(i -> executorService.submit(() -> {
                getter.get(options).block();
            }))
            .toList();

        for (Future<?> future : futures) {
            future.get();
        }

        verify(lister).list(options);
    }
}
