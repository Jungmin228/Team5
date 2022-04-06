package paymentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PaymentServiceTest {
    // SUT
    PaymentService service;
    // DOC
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        service = new PaymentService(customerRepository);
    }

    @DisplayName("실 결재 금액을 기준으로 적립율에 따라서 적립됨.")
    @Test
    void pay_and_save() {
        Long productAmt = 2000L;
        Long customerId = 1L;

        Customer customer = new Customer(customerId, 10000L, 0L);
        when(customerRepository.findById(customerId)).thenReturn(customer);
        //service.setPaymentService(customerRepository);
        Receipt result = service.pay(productAmt, customerId);

        assertThat(customer.getBalance()).isEqualTo(8000L);
        assertThat(customer.getPoint()).isEqualTo(100L);
        assertThat(result).isNotNull();
        // 구매를 했을 때 손님 잔액이 잘 남는지
        // 하지만 포인트 쌓이는 것은 아직 구현 안함

        // assertThat(customer.getPoint()).isEqualTo(75L);
        // 포인트 모두 사용한다고 가정하고, 계산 후 적립금이 실지불금액 * 5%가 맞는지 확인

        // assertion w/ receipt
    }

    @DisplayName("결재 금액이 유효해야함.(null이면 안됨, 음수이면 안됨)")
    @Test
    void pay_productAmtInvalid_throwIllegalArgumentException() {
        Long productAmt = -1L;
        Long customerId = 0L;

        assertThatThrownBy(() -> service.pay(productAmt, customerId))
            .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContainingAll("productAmt is invalid");

    }


    @DisplayName("계정이 없으면 예외 발생.")
    @Test
    void pay_notExistsCustomer_throwCustomerNotExistsException(){
        long productAmt = 1_000L;
        long customerId = -1;

        when(customerRepository.findById(customerId)).thenThrow(new CustomerNotExistsException("id가 존재하지 않습니다"));

        assertThatThrownBy(() -> service.pay(productAmt, customerId))
            .isInstanceOf(CustomerNotExistsException.class);
    }




}
