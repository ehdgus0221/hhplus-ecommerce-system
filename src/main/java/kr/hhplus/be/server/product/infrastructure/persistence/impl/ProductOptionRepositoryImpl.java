package kr.hhplus.be.server.product.infrastructure.persistence.impl;

import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import kr.hhplus.be.server.product.infrastructure.persistence.jpa.ProductOptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProductOptionRepositoryImpl implements ProductOptionRepository {

    private final ProductOptionJpaRepository productOptionJpaRepository;

    @Override
    public Optional<ProductOption> findById(Long id){
        return productOptionJpaRepository.findById(id);
    };

    @Override
    public ProductOption findWithPessimisticLock(Long id){
        return productOptionJpaRepository.findWithPessimisticLock(id);
    };
}
