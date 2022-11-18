package run.halo.sitemap;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
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
        when(lister.list()).thenReturn(
            Flux.just(SitemapEntry.builder().loc("http://localhost:8090/about").build()));
        getter = new CachedSitemapGetter(lister);
    }

    @Test
    void get() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < countDownLatch.getCount(); i++) {
            new Thread(() -> {
                getter.get().block();
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        verify(lister, times(1)).list();
    }
}
