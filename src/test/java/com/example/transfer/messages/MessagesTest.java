package com.example.transfer.messages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.MessageSourceAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MessagesTest {

    @Autowired
    MessageSourceAccessor messages;

    @Test
    void test_get_generalMessages() {
        assertEquals("Некорректный запрос", messages.getMessage("bad.request.message"));
        assertEquals("Карта отправителя не зарегистрирована или ее данные заполнены неверно",
                messages.getMessage("wrong.sender.data"));
        assertEquals("Операция не найдена", messages.getMessage("transaction.not.found"));
        assertEquals("Не удалось подтвердить операцию", messages.getMessage("invalid.transaction.code"));
        assertEquals("Недостаточно средств", messages.getMessage("insufficient.funds"));
        assertEquals("Непредвиденная ошибка", messages.getMessage("unexpected.error"));
    }
}