package com.example.transfer_test.controller;

import com.example.transfer_test.model.request.ConfirmationRequest;
import com.example.transfer_test.model.request.TransferRequest;
import com.example.transfer_test.model.response.SuccessMessage;
import com.example.transfer_test.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @Test
    public void test_transfer_wrongJson_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\"}"));
    }

    @Test
    public void test_transfer_emptyJson_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"CVC/CVV код не заполнен, " +
                        "Некорректный номер кредитной карты отправителя, " +
                        "Некорректный номер кредитной карты получателя, " +
                        "Срок действия карты не заполнен, " +
                        "Сумма и валюта перевода не указаны\"}"));
    }

    @Test
    public void test_transfer_wrongSenderCard_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"cardFromNumber\": \"1111111111111111\",\n" +
                        "  \"cardToNumber\": \"4111111111111111\",\n" +
                        "  \"cardFromCVV\": \"343\",\n" +
                        "  \"cardFromValidTill\": \"11/22\",\n" +
                        "  \"amount\": {\n" +
                        "    \"currency\": \"RUR\",\n" +
                        "    \"value\": \"100000\"\n" +
                        "  }\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный номер кредитной карты отправителя\"}"));
    }

    @Test
    public void test_transfer_sameCardNumbers_isBadRequest() throws Exception {
        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"cardFromNumber\": \"4111111111111111\",\n" +
                        "  \"cardToNumber\": \"4111111111111111\",\n" +
                        "  \"cardFromCVV\": \"343\",\n" +
                        "  \"cardFromValidTill\": \"11/22\",\n" +
                        "  \"amount\": {\n" +
                        "    \"currency\": \"RUR\",\n" +
                        "    \"value\": \"100000\"\n" +
                        "  }\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Номера карт совпадают\"}"));
    }

    @Test
    public void test_transfer_wrongValue_JsonError() throws Exception {

        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"cardFromNumber\": \"4111111111111111\",\n" +
                        "  \"cardToNumber\": \"4111111111111111\",\n" +
                        "  \"cardFromCVV\": \"343\",\n" +
                        "  \"cardFromValidTill\": \"11/22\",\n" +
                        "  \"amount\": {\n" +
                        "    \"currency\": \"RUR\",\n" +
                        "    \"value\": \"\"\n" +
                        "  }\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\"}"));
    }

    @Test
    public void test_transfer_validJson_serviceInvolved() throws Exception {
        final String operationId = "ea1ffcdd-46f6-4c34-b3d8-ee812ed805d8";
        when(transferService.transfer(any(TransferRequest.class)))
                .thenReturn(ResponseEntity.ok().body(new SuccessMessage(operationId)));

        this.mockMvc.perform(post("/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"cardFromNumber\": \"4485583965966956\",\n" +
                        "  \"cardToNumber\": \"4485058901151674\",\n" +
                        "  \"cardFromCVV\": \"343\",\n" +
                        "  \"cardFromValidTill\": \"11/25\",\n" +
                        "  \"amount\": {\n" +
                        "    \"currency\": \"RUR\",\n" +
                        "    \"value\": \"100000\"\n" +
                        "  }\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"operationId\":\"" + operationId + "\"}"));
    }

    @Test
    public void test_confirmOperation_wrongJson() throws Exception {

        this.mockMvc.perform(post("/confirmOperation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный запрос\"}"));
    }

    @Test
    public void test_confirmOperation_emptyJson() throws Exception {

        this.mockMvc.perform(post("/confirmOperation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный идентификатор транзакции, " +
                        "Некорректный код подтверждения\"}"));
    }

    @Test
    public void test_confirmOperation_wrongCode() throws Exception {

        this.mockMvc.perform(post("/confirmOperation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"operationId\": \"  \",\n" +
                        "  \"code\": \"aaaaaa\"\n" +
                        "}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Некорректный идентификатор транзакции, " +
                                "Некорректный код подтверждения\"}"));
    }

    @Test
    public void test_confirmOperation_validJson_serviceInvolved() throws Exception {
        final String operationId = "ea1ffcdd-46f6-4c34-b3d8-ee812ed805d8";
        when(transferService.confirmOperation(any(ConfirmationRequest.class)))
                .thenReturn(ResponseEntity.ok().body(new SuccessMessage(operationId)));

        this.mockMvc.perform(post("/confirmOperation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"operationId\": \"" + operationId + "\",\n" +
                        "  \"code\": \"0000\"\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"operationId\":\"" + operationId + "\"}"));
    }
}
