package kr.hhplus.be.server.coupon.api.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.CouponUseResponseDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;


    @PostMapping("/issue")
    public ResponseEntity<UserCouponResponseDto> issueCoupon(
            @Valid @RequestBody CouponIssueRequestDto request
    ) {
        UserCouponResponseDto response = couponService.issue(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/use")
    public ResponseEntity<CouponUseResponseDto> useCoupon(@PathVariable Long id) {
        CouponUseResponseDto result = couponService.use(id);
        return ResponseEntity.ok(result);
    }
}