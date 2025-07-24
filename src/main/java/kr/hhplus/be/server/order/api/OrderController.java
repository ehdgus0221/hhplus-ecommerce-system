package kr.hhplus.be.server.order.api;

import jakarta.validation.Valid;
import kr.hhplus.be.server.order.api.dto.request.OrderRequestDto;
import kr.hhplus.be.server.order.api.dto.response.OrderResponseDto;
import kr.hhplus.be.server.order.application.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    // 주문 요청
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto.Create request) {
        OrderResponseDto response = orderFacade.placeOrder(request);
        return ResponseEntity.ok(response);
    }
}
