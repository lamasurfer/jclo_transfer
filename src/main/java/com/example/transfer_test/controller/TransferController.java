package com.example.transfer_test.controller;

import com.example.transfer_test.model.request.ConfirmationRequest;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    // все запросы валидируются несмотря на валидацию фронта, на все высылаются понятные сообщения
    // если приходит некорректный json - ошибка "Некорректные запрос"
    // дальше запросы проверяются валидаторами, их поведение отражено в тестах
    // test...controller/ControllerMockMvcTest.java
    // test...model/validation/
    // попробовал свои написать валидаторы с аннотациями сделать
    // с ними, кажется, аккуратнее получается, чем с аннотациями на все подряд или if-else проверками

    @PostMapping("/transfer")
    public ResponseEntity<Object> transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<Object> confirmOperation(@Valid @RequestBody ConfirmationRequest request) {
        return transferService.confirmOperation(request);
    }

    // обработка jackson запросов/ответов описана в тестах также в /model
    // правда IDEA jacksonTester подчеркивает красным, но все равно работает

    // еще обратил внимание, что фронт присылает сумму перевода с двумя дополнительными нулями
    // при этом форматы https://github.com/zalando/jackson-datatype-money/blob/main/MONEY.md как вот тут например не указываются
    // использовал BigDecimal и попробовал вот таким способом этот вопрос решить в отдельном сеттере для jackson
    // this.value = value.scale() == 0 ? value.scaleByPowerOfTen(-2) : value;
    // в тестах отражены оба варианта

    // дальше все описал в сервисах, удобнее начинать с TransferService
}
