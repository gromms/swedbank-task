package foo.exceptions;

import foo.models.Account;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(Account account) {
        super(String.format("Account with a name %s already exists.", account.getName()));
    }
}
