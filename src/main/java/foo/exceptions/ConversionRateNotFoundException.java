package foo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConversionRateNotFoundException extends RuntimeException {
    public ConversionRateNotFoundException(String currency) {
        super(String.format("Conversion rate for currency %s not found.", currency));
    }
}
