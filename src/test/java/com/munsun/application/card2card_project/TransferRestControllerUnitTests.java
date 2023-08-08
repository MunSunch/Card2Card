package com.munsun.application.card2card_project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munsun.application.card2card_project.dto.out.CardDtoOut;
import com.munsun.application.card2card_project.dto.out.SuccessTransferDtoOut;
import com.munsun.application.card2card_project.service.TransferService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferRestControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransferService service;
    @Autowired
    private ObjectMapper mapper;

    @Test
    public void deserializationJson2TransferInfoDtoIn() throws Exception {
        String json = "{\n" +
                "  \"cardFromNumber\": \"7596830406697811\",\n" +
                "  \"cardFromValidTill\": \"08/28\",\n" +
                "  \"cardFromCVV\": \"833\",\n" +
                "  \"cardToNumber\": \"1251580016888995\",\n" +
                "  \"amount\": {\n" +
                "    \"value\": 100,\n" +
                "    \"currency\": \"RUR\"\n" +
                "  }\n" +
                "}";

        SuccessTransferDtoOut successTransferDtoOut = new SuccessTransferDtoOut();
            successTransferDtoOut.setOperationId(0L);

        Mockito.when(service.send(Mockito.any())).thenReturn(successTransferDtoOut);

        mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(successTransferDtoOut)));
    }

    @Test
    public void deserializationJson2ConfirmTransferDtoIn() throws Exception {
        String json = "{\n" +
                "    \"transferInfo\": {\n" +
                "      \"operationId\": 3,\n" +
                "      \"cardFrom\": {\n" +
                "        \"cardNumber\": \"1251580016888995\",\n" +
                "        \"cardValidTill\": \"08/28\",\n" +
                "        \"cardCVV\": \"318\",\n" +
                "        \"currency\": \"RUR\",\n" +
                "        \"value\": 600,\n" +
                "        \"isActive\": true\n" +
                "      },\n" +
                "      \"cardTo\": {\n" +
                "        \"cardNumber\": \"7596830436697811\",\n" +
                "        \"cardValidTill\": \"08/28\",\n" +
                "        \"cardCVV\": \"833\",\n" +
                "        \"currency\": \"RUR\",\n" +
                "        \"value\": 1400,\n" +
                "        \"isActive\": true\n" +
                "      },\n" +
                "      \"currency\": \"RUR\",\n" +
                "      \"value\": 1000,\n" +
                "      \"status\": true\n" +
                "    },\n" +
                "    \"codeConfirm\": 0\n" +
                "  }";


    }
}
