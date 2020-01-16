package foo.services;

import foo.exceptions.*;
import foo.models.Account;
import foo.models.Payment;
import foo.repositories.AccountRepository;
import foo.repositories.ConversionRateRepository;
import foo.util.Currencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ConversionRateRepository conversionRateRepository;

    @Override
    public Account create(Account account) {
        accountRepository.findByName(account.getName()).ifPresent(acc -> {
            throw new AccountAlreadyExistsException(account);
        });

        return accountRepository.save(account);
    }

    @Override
    public Account patch(Account newAccount) {
        accountRepository.findById(newAccount.getId()).orElseThrow(() ->
                new AccountNotFoundException(newAccount)
        );
        accountRepository.findByName(newAccount.getName()).ifPresent(account -> {
                    if (!account.getId().equals(newAccount.getId())) {
                        throw new IllegalNameException("Name already exists");
                    }
                }
        );

        return accountRepository.save(newAccount);
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @Transactional
    public Payment newPayment(Payment payment) {
        Account sourceAccount = accountRepository.findById(payment.getSourceId()).orElseThrow(() ->
                new AccountNotFoundException(payment.getSourceId()));
        Account targetAccount = accountRepository.findById(payment.getTargetId()).orElseThrow(() ->
                new AccountNotFoundException(payment.getTargetId()));

        if (payment.getAmount().compareTo(new BigDecimal(0)) <= 0) {
            throw new IllegalPaymentAmountException(payment.getAmount());
        }

        if (sourceAccount.getBalance().compareTo(payment.getAmount()) < 0) {
            throw new InsufficientBalanceException(sourceAccount, payment.getAmount());
        }

        BigDecimal sourceAccountConversionRate = new BigDecimal("1").setScale(6, RoundingMode.DOWN);
        BigDecimal targetAccountConversionRate = new BigDecimal("1").setScale(6, RoundingMode.DOWN);

        if (sourceAccount.getCurrency() != Currencies.Currency.EUR) {
            try {
                sourceAccountConversionRate = conversionRateRepository.findByCurrency(sourceAccount.getCurrency()).orElseThrow(() ->
                        new ConversionRateNotFoundException(sourceAccount.getCurrency().toString())
                ).getConversionRateToEur();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (targetAccount.getCurrency() != Currencies.Currency.EUR) {
            try {
                targetAccountConversionRate = conversionRateRepository.findByCurrency(targetAccount.getCurrency()).orElseThrow(() ->
                        new ConversionRateNotFoundException(targetAccount.getCurrency().toString())
                ).getConversionRateToEur();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BigDecimal newSourceBalance = sourceAccount.subtractBalance(payment.getAmount());
        BigDecimal newTargetBalance = targetAccount.addBalance(payment.getAmount().
                divide(sourceAccountConversionRate, RoundingMode.DOWN).
                multiply(targetAccountConversionRate).
                setScale(2, RoundingMode.DOWN));

        sourceAccount.setBalance(newSourceBalance);
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        return payment;
    }

    @Override
    public void delete(long accountId) {
        accountRepository.findById(accountId).orElseThrow(() ->
                new AccountNotFoundException(accountId)
        );

        accountRepository.deleteById(accountId);
    }
}
