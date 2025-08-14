package kr.hhplus.be.server.aop;


/**
 * 락 획득 실패 시 발생하는 런타임 예외
 */
public class LockAcquisitionException extends RuntimeException {
    public LockAcquisitionException(String message) {
        super(message);
    }
}
