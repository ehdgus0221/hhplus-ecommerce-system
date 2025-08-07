package kr.hhplus.be.server.balance;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.balance.api.dto.request.BalanceChargeRequestDto;
import kr.hhplus.be.server.balance.api.dto.request.BalanceUseRequestDto;
import kr.hhplus.be.server.balance.domain.model.Balance;
import kr.hhplus.be.server.balance.domain.model.BalanceHistory;
import kr.hhplus.be.server.balance.infrastructure.persistence.jpa.BalanceHistoryJpaRepository;
import kr.hhplus.be.server.balance.infrastructure.persistence.jpa.BalanceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(TestcontainersConfiguration.class)
class BalanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalanceJpaRepository balanceJpaRepository;

    @Autowired
    private BalanceHistoryJpaRepository balanceHistoryJpaRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
    }




    @Test
    @DisplayName("잔액 충전 - 성공")
    void charge_success() throws Exception {
        // given
        long chargeAmount = 5000L;
        BalanceChargeRequestDto requestDto = new BalanceChargeRequestDto(chargeAmount);

        // when & then
        mockMvc.perform(post("/balances/charge/user/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(chargeAmount));

        // DB 확인
        Optional<Balance> balanceOpt = balanceJpaRepository.findByUserId(testUserId);
        assertTrue(balanceOpt.isPresent());
        assertEquals(chargeAmount, balanceOpt.get().getAmount());

        List<BalanceHistory> histories = balanceHistoryJpaRepository.findAll();
        assertEquals(1, histories.size());
        assertEquals("CHARGE", histories.get(0).getType());
    }

    @Test
    @DisplayName("잔액 조회 요청 - 성공")
    void get_balance_success() throws Exception {
        // given
        Balance saved = balanceJpaRepository.save(Balance.builder()
                .userId(testUserId)
                .amount(3000L)
                .build());

        // when & then
        mockMvc.perform(get("/balances/user/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(3000L));
    }

    @Test
    @DisplayName("잔액 조회 요청 - 실패")
    void 잔액_사용_요청_성공() throws Exception {
        // given
        balanceJpaRepository.save(Balance.builder()
                .userId(testUserId)
                .amount(5000L)
                .build());

        BalanceUseRequestDto requestDto = new BalanceUseRequestDto(2000L);

        // when & then
        mockMvc.perform(post("/balances/use/user/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(3000L));

        // DB 확인
        Balance updated = balanceJpaRepository.findByUserId(testUserId).orElseThrow();
        assertEquals(3000L, updated.getAmount());

        List<BalanceHistory> histories = balanceHistoryJpaRepository.findAll();
        assertEquals(1, histories.size());
        assertEquals("USE", histories.get(0).getType());
    }
}

