package kr.hhplus.be.server.product.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductOptionDomainService {
    private final ProductOptionRepository productOptionRepository;

    public void decreaseStock(Long productOptionId, int stock) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new EntityNotFoundException("상품 옵션이 존재하지 않습니다."));
        option.decreaseStock(stock); // 도메인 로직
    }
}
