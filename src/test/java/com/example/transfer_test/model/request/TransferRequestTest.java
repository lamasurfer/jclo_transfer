package com.example.transfer_test.model.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureJsonTesters
class TransferRequestTest {

    @Autowired
    JacksonTester<TransferRequest> jacksonTester;

    @Test
    void test_deserialization_noScale() throws IOException {

        final String json = "{\n" +
                "  \"cardFromNumber\": \"4485583965966956\",\n" +
                "  \"cardToNumber\": \"4485058901151674\",\n" +
                "  \"cardFromCVV\": \"343\",\n" +
                "  \"cardFromValidTill\": \"11/25\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": \"100000\"\n" +
                "  }\n" +
                "}";

        final TransferRequest transferRequest = jacksonTester.parseObject(json);
        assertEquals("4485583965966956", transferRequest.getCardFromNumber());
        assertEquals("4485058901151674", transferRequest.getCardToNumber());
        assertEquals("343", transferRequest.getCardFromCVV());
        assertEquals("2025-11", transferRequest.getCardFromValidTill().toString());
        assertEquals("RUR", transferRequest.getAmount().getCurrency());
        assertEquals(new BigDecimal("1000.00"), transferRequest.getAmount().getValue());

    }

    @Test
    void test_deserialization_withScale() throws IOException {

        final String json = "{\n" +
                "  \"cardFromNumber\": \"4485583965966956\",\n" +
                "  \"cardToNumber\": \"4485058901151674\",\n" +
                "  \"cardFromCVV\": \"343\",\n" +
                "  \"cardFromValidTill\": \"11/25\",\n" +
                "  \"amount\": {\n" +
                "    \"currency\": \"RUR\",\n" +
                "    \"value\": \"1000.00\"\n" +
                "  }\n" +
                "}";

        final TransferRequest transferRequest = jacksonTester.parseObject(json);
        assertEquals("4485583965966956", transferRequest.getCardFromNumber());
        assertEquals("4485058901151674", transferRequest.getCardToNumber());
        assertEquals("343", transferRequest.getCardFromCVV());
        assertEquals("2025-11", transferRequest.getCardFromValidTill().toString());
        assertEquals("RUR", transferRequest.getAmount().getCurrency());
        assertEquals(new BigDecimal("1000.00"), transferRequest.getAmount().getValue());

    }
}