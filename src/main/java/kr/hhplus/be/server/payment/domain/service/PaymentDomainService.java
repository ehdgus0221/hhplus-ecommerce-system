package kr.hhplus.be.server.payment.domain.service;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createAndSavePayment(Long userId, int amount) {
        Payment payment = Payment.create(userId, amount);
        return paymentRepository.save(payment);
    }
}