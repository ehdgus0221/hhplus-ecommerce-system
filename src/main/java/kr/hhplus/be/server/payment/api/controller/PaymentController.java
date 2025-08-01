package kr.hhplus.be.server.payment.api.controller;

import kr.hhplus.be.server.payment.api.dto.request.PaymentRequest;
import kr.hhplus.be.server.payment.api.dto.response.PaymentResponse;
import kr.hhplus.be.server.payment.api.spec.PaymentApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController{

    // 결제 내역 전체 조회
    @GetMapping
    public ResponseEntity<List<PaymentResponse.Details>> getAllPayments() {
        // 예시 데이터 반환
        List<PaymentResponse.Details> response = List.of(
                new PaymentResponse.Details(1L, 1L, 1L, BigDecimal.valueOf(10000.00), LocalDateTime.of(2025, 7, 17, 23, 45, 0)),
                new PaymentResponse.Details(2L, 2L, 2L, BigDecimal.valueOf(7000.00), LocalDateTime.of(2025, 6, 18, 23, 45, 0))

        );
        return ResponseEntity.ok(response);
    }

    // 결제 상세 내역 조회
    @GetMapping("/payments/{id}")
    public ResponseEntity<PaymentResponse.Details> getDetails(@PathVariable Long id) {
        // 예시 데이터 반환
        PaymentResponse.Details response = new PaymentResponse.Details(
                1L, 1L, 1L, BigDecimal.valueOf(10000.00),
                LocalDateTime.of(2025, 7, 17, 23, 45, 0)
        );
        return ResponseEntity.ok(response);
    }

}
