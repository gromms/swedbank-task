package foo.exceptions;

import foo.models.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Account account, BigDecimal amount) {
        super(String.format("Account %s has insufficient balance. Balance: %s, Required: %s",
                account.getName(),
                account.getBalance(),
                amount)
        );
    }
}
