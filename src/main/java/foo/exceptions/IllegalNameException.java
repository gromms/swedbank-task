package foo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IllegalNameException extends RuntimeException {
    public IllegalNameException(String message) {
        super(message);
    }
}
