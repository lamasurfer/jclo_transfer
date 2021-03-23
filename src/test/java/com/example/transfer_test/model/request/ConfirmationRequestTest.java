package com.example.transfer_test.model.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureJsonTesters
class ConfirmationRequestTest {

    @Autowired
    private JacksonTester<ConfirmationRequest> jacksonTester;

    @Test
    void test_deserialization() throws IOException {
        final String json = "{\n" +
                "  \"operationId\": \"ea1ffcdd-46f6-4c34-b3d8-ee812ed805d8\",\n" +
                "  \"code\": \"0000\"\n" +
                "}";

        final ConfirmationRequest confirmationRequest = jacksonTester.parseObject(json);
        assertEquals("ea1ffcdd-46f6-4c34-b3d8-ee812ed805d8", confirmationRequest.getOperationId());
        assertEquals("0000", confirmationRequest.getCode());

    }
}