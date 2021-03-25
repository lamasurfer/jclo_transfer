package com.example.transfer.model.response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@AutoConfigureJsonTesters
class SuccessMessageTest {

    @Autowired
    private JacksonTester<SuccessMessage> jacksonTester;

    @Test
    void test_serialization() throws IOException {

        final SuccessMessage successMessage = new SuccessMessage("87000625-ba70-443a-baa7-beacc2accf34");
        final JsonContent<SuccessMessage> result = this.jacksonTester.write(successMessage);

        final String expectedJson = "{\"operationId\":\"87000625-ba70-443a-baa7-beacc2accf34\"}";
        assertEquals(expectedJson, result.getJson());
    }
}