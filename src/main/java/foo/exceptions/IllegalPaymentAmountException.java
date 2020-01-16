package foo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalPaymentAmountException extends RuntimeException {
    public IllegalPaymentAmountException(BigDecimal amount) {
        super(String.format("Payment amount must be greater than 0. Received: %s", amount.toString()));
    }
}
