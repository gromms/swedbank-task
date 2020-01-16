package foo.controllers;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import foo.models.Account;
import foo.models.Payment;
import foo.services.AccountService;
import foo.util.Currencies;
import foo.util.CustomExceptionMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@CrossOrigin
public class ApiController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/api/accounts")
    @ResponseBody
    public List<Account> accounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/api/currencies")
    @ResponseBody
    public Currencies.Currency[] currencies() {
        return Currencies.Currency.values();
    }

    @PatchMapping(value = "/api/accounts")
    @ResponseBody
    public Account updateAccount(@RequestBody Account account) throws Exception {
        return accountService.patch(account);
    }

    @PostMapping("/api/accounts")
    @ResponseBody
    public Account newAccount(@RequestBody Account account) {
        return accountService.create(account);
    }

    @DeleteMapping("/api/accounts/{id}")
    @ResponseBody
    public void deleteAccount(@PathVariable(value = "id") long accountId) {
        accountService.delete(accountId);
    }

    @PostMapping("/api/payment")
    @ResponseBody
    public Payment newPayment(@RequestBody Payment payment) {
        return accountService.newPayment(payment);
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody
    CustomExceptionMessage handleInvalidFormatException(HttpServletRequest req, Exception e) {
        return new CustomExceptionMessage("Currency not found.", e, HttpStatus.NOT_FOUND.value());
    }
}
