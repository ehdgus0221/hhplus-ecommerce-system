package kr.hhplus.be.server.coupon.api.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.coupon.api.dto.request.CouponIssueRequestDto;
import kr.hhplus.be.server.coupon.api.dto.response.UserCouponResponseDto;
import kr.hhplus.be.server.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;


    @PostMapping("/issue")
    public ResponseEntity<UserCouponResponseDto> issueCoupon(
            @Valid @RequestBody CouponIssueRequestDto request
    ) {
        UserCouponResponseDto response = couponService.issueCoupon(request);
        return ResponseEntity.ok(response);
    }
}