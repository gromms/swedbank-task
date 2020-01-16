package foo.exceptions;

import foo.models.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Account account) {
        super(String.format("Account with an ID of %d not found.", account.getId()));
    }

    public AccountNotFoundException(Long id) {
        super(String.format("Account with an ID of %d not found.", id));
    }
}
