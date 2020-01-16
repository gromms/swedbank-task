package foo.services;

import foo.models.Account;
import foo.models.Payment;

import java.util.List;

public interface AccountService {
    List<Account> getAccounts();

    Account create(Account account);

    Account patch(Account account) throws Exception;

    Payment newPayment(Payment payment);

    void delete(long accountId);
}
