package kr.hhplus.be.server.order;


import kr.hhplus.be.server.order.api.dto.request.OrderRequestDto;
import kr.hhplus.be.server.order.application.OrderFacade;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.model.ProductOptionStatus;
import kr.hhplus.be.server.product.domain.model.ProductStatus;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderServiceConcurrencyTest {


    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ApplicationContext context;


    private void executeConcurrency(int threadCount, Runnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(runnable, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }

    private void executeConcurrency(List<Runnable> runnables) {
        ExecutorService executorService = Executors.newFixedThreadPool(runnables.size());

        List<CompletableFuture<Void>> futures = runnables.stream()
                .map(runnable -> CompletableFuture.runAsync(runnable, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executorService.shutdown();
    }



    @Test
    @DisplayName("동시에 주문이 들어올 때, 재고 수량을 초과하여 주문되지 않도록 보장한다.")
    void decreaseStockWithPessimisticLock() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;


        Product product = Product.create("1번 상품", 5000, "첫 번째 상품");
        ProductOption productOption = ProductOption.create(product, "파란색 티셔츠", 5500, 5);

        productRepository.save(product);
        productOptionRepository.save(productOption);


        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        OrderRequestDto.Create request1 = new OrderRequestDto.Create(
                product.getId(),
                productOption.getId(),
                1,
                userId1,
                null
        );

        OrderRequestDto.Create request2 = new OrderRequestDto.Create(
                product.getId(),
                productOption.getId(),
                1,
                userId2,
                null
        );

        executeConcurrency(List.of(
                () -> {
                    OrderFacade facadeBean = context.getBean(OrderFacade.class);
                    try {
                        facadeBean.placeOrder(request1);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                },
                () -> {
                    OrderFacade facadeBean = context.getBean(OrderFacade.class);
                    try {
                        facadeBean.placeOrder(request2);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    }
                }
        ));

        // then
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(failCount.get()).isEqualTo(0);
        assertThat(successCount.get() + failCount.get()).isEqualTo(2);

        ProductOption updated = productOptionRepository.findById(productOption.getId()).orElseThrow();
        assertThat(updated.getStock()).isEqualTo(3); // 재고 5 → 3
    }

}
