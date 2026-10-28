package com.coppel.client.customcomplements;

import com.coppel.config.customcomplements.PsClientConfig;
import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.CustomsArea;
import com.coppel.exceptions.customcomplements.NotFoundException;
import com.coppel.exceptions.customcomplements.RemoteServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Los errores de ps conservan su mensaje de negocio (404 → NotFoundException); lo demás es 502.
 */
class PsClientErrorHandlerTest {

    private MockRestServiceServer server;
    private CustomComplementsPsClient client;

    @BeforeEach
    void setUp() {
        JsonMapper mapper = JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
        RestClient.Builder builder = PsClientConfig.configure(RestClient.builder(), mapper).baseUrl("http://ps");
        server = MockRestServiceServer.bindTo(builder).build();
        client = PsClientConfig.create(builder.build());
    }

    @Test
    void readsData() {
        server.expect(requestTo("http://ps/v1/custom-complements/codes/ROPA/123456/exists"))
            .andRespond(withSuccess("{\"meta\":{\"status\":\"OK\",\"statusCode\":200},\"data\":{\"exists\":true}}",
                MediaType.APPLICATION_JSON));

        assertThat(PsResponses.data(client.codeExists(CustomsArea.ROPA, 123456)).exists()).isTrue();
    }

    @Test
    void notFoundKeepsTheMessage() {
        server.expect(requestTo("http://ps/v1/custom-complements/codes/MUEBLES/123456/general"))
            .andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                .body("{\"meta\":{\"status\":\"NOT FOUND\",\"statusCode\":404,"
                    + "\"message\":\"No se encontraron datos del código.\"}}"));

        assertThatThrownBy(() -> client.general(CustomsArea.MUEBLES, 123456))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("No se encontraron datos del código.");
    }

    @Test
    void serverErrorIsBadGateway() {
        server.expect(requestTo("http://ps/v1/custom-complements/catalogs/importers"))
            .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.importers())
            .isInstanceOf(RemoteServiceException.class)
            .hasMessage(AppMessages.ERROR_PERSISTENCIA);
    }
}
