package kr.hhplus.be.server.common;

public class ErrorMessages {
    public static final String INVALID_CHARGE_AMOUNT = "충전 금액은 1원 이상이어야 합니다.";
    public static final String INSUFFICIENT_BALANCE = "잔액이 부족합니다.";
    public static final String INVALID_USAGE_AMOUNT = "사용 금액은 0보다 커야 합니다.";
    public static final String BALANCE_NOT_FOUND = "잔고 정보가 존재하지 않습니다.";
    public static final String COUPON_OUT_OF_STOCK = "쿠폰 수량이 부족합니다.";
    public static final String COUPON_QUANTITY_INVALID = "쿠폰 수량은 1 이상이어야 합니다.";
    public static final String COUPON_DISCOUNT_RATE_INVALID = "쿠폰 할인율은 1이상 100이하의 자연수를 입력해주세요.";
    public static final String COUPON_EXPIRED_AT_INVALID = "쿠폰 만료일은 현재 시간 이후로 입력해야 합니다.";
    public static final String COUPON_EXPIRED = "쿠폰이 만료되었습니다.";
    public static final String INVALID_PRODUCT_PRICE = "상품 가격은 0보다 커야합니다.";
    public static final String PRODUCT_OPTION_NOT_FOUND = "잔고 정보가 존재하지 않습니다.";
    public static final String PRODUCT_OPTION_INACTIVE = "비활성화된 옵션입니다.";
    public static final String PAYMENT_PRICE_INVALID = "결제 금액은 0보다 커야합니다.";
}
