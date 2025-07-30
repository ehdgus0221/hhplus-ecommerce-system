package kr.hhplus.be.server.balance.api.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.balance.api.dto.request.BalanceChargeRequestDto;
import kr.hhplus.be.server.balance.api.dto.request.BalanceUseRequestDto;
import kr.hhplus.be.server.balance.api.dto.response.BalanceResponseDto;
import kr.hhplus.be.server.balance.application.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balances")
public class BalanceController{

    private final BalanceService balanceService;

    @PostMapping("/charge/user/{userId}")
    public ResponseEntity<BalanceResponseDto> chargeBalance(@PathVariable("userId") Long userId,
                                                            @Valid @RequestBody BalanceChargeRequestDto request) {
        BalanceResponseDto response = balanceService.charge(userId, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BalanceResponseDto> getBalance(@PathVariable("userId") Long userId) {
        BalanceResponseDto response = balanceService.get(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/use/user/{userId}")
    public ResponseEntity<BalanceResponseDto> useBalance(@PathVariable("userId") Long userId,
                                                         @Valid @RequestBody BalanceUseRequestDto request) {
        BalanceResponseDto response = balanceService.use(userId, request.getAmount());
        return ResponseEntity.ok(response);
    }
}