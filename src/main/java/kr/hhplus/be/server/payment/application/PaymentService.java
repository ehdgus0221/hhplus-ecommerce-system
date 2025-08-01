package kr.hhplus.be.server.payment.application;

import kr.hhplus.be.server.balance.application.BalanceService;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentDomainService paymentDomainService;

    /**
     * 잔액 차감 및 결제 처리
     */
    @Transactional
    public Payment processPayment(Long userId, long amount) {
        return paymentDomainService.createAndSavePayment(userId, amount);
    }
}
