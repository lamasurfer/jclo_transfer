package com.example.transfer_test.service;

import com.example.transfer_test.exception.AccountException;
import com.example.transfer_test.exception.TransactionException;
import com.example.transfer_test.model.account.Account;
import com.example.transfer_test.model.request.ConfirmationRequest;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.model.response.SuccessMessage;
import com.example.transfer_test.model.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {
    // записывает транзакцию при создании и после проведения, при этом заменяет первые 12 цифр карт
    // и CVV на X, полные данные остаются в репозитории,
    // записывает в файл transaction.log
    // в задании требуется все это в открытую хранить, мне показалось, что это не лучший вариант
    private static final Logger LOGGER = LoggerFactory.getLogger("transaction_logger");

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final VerificationService verificationService;
    // попробовал использовать такой способ получения сообщений, чтобы не засорять все константами
    // если фронт переведут на английский, будет в два раза больше констант? (и if-else скорее всего))
    // локализацию можно будет легко прикрутить
    private final MessageSourceAccessor messages;

    public TransferService(TransactionService transactionService,
                           AccountService accountService,
                           VerificationService verificationService,
                           MessageSourceAccessor messages) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.verificationService = verificationService;
        this.messages = messages;
    }

    // к этому моменту в запросе уже все проверено, валидность по алгоритму Луна итд
    // кроме данных аккаунта
    public ResponseEntity<Object> transfer(TransferRequest transferRequest) {
        // проверяет, что аккаунт отправителя зарегистрирован и его данные (CVV, дата) совпадают
        // если нет - ошибка, ответ отправит AppExceptionHandler, код 500
        final Account accountFrom = accountService.getSenderAccount(transferRequest)
                .orElseThrow(() -> new AccountException(messages.getMessage("wrong.sender.data")));

        // ищет аккаунт получателя, подробнее в AccountService
        final Account accountTo = accountService.getReceiverAccount(transferRequest.getCardToNumber());

        final BigDecimal amount = transferRequest.getAmount().getValue();

        // пробует создать и получить транзакцию,
        // в случае если на балансе отправителя недостаточно средств, вылетит ошибка код 500
        // все связанное с деньгами в TransactionService происходит
        final Transaction transaction = transactionService.getTransaction(accountFrom, accountTo, amount)
                .orElseThrow(() -> new AccountException(messages.getMessage("insufficient.funds")));

        // устанавливается код, он всегда 0000, но если бы это было не так, то за это явно
        // отдельная служба должна была бы отвечать
        verificationService.setVerificationCode(transaction);
        // записывает транзакцию, которая пока не подтверждена
        LOGGER.info(transaction.toString());
        // ответ в виде SuccessMessage как описано в api фронта
        return ResponseEntity.ok().body(new SuccessMessage(transaction.getOperationId()));
    }

    public ResponseEntity<Object> confirmOperation(ConfirmationRequest confirmationRequest) {
        // проверяет что транзакция есть или ошибка 500
        final Transaction transaction = transactionService.getById(confirmationRequest.getOperationId())
                .orElseThrow(() -> new TransactionException(messages.getMessage("transaction.not.found")));
        // проверяет код, в случае с фронтом он всегда правильный
        if (!verificationService.verifyTransaction(transaction)) {
            throw new TransactionException(messages.getMessage("invalid.transaction.code"));
        }
        // проводит транзакцию, подробнее в TransactionService
        transactionService.processTransaction(transaction);
        // записывает ее еще раз, уже с отметкой о том, что она проведена
        LOGGER.info(transaction.toString());
        return ResponseEntity.ok().body(new SuccessMessage(transaction.getOperationId()));
    }
}
