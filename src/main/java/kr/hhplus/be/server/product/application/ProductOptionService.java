package kr.hhplus.be.server.product.application;

import kr.hhplus.be.server.product.domain.service.ProductOptionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductOptionService {
    // 재고 차감
    private final ProductOptionDomainService productOptionDomainService;

    @Transactional
    public void decreaseStock(Long productOptionId, int stock) {
        productOptionDomainService.decreaseStock(productOptionId, stock);
    }
}
