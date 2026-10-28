package com.coppel.client.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.exceptions.customcomplements.BadRequestException;
import com.coppel.exceptions.customcomplements.ConflictException;
import com.coppel.exceptions.customcomplements.NotFoundException;
import com.coppel.exceptions.customcomplements.RemoteServiceException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Convierte las respuestas de error de ps ({ meta: { statusCode, message } }) en las excepciones de negocio de ds:
 * 400 / 404 / 409 conservan su mensaje y lo demás es 502.
 */
public class PsClientErrorHandler implements RestClient.ResponseSpec.ErrorHandler {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        String message = message(response);
        switch (status.value()) {
            case 400 -> throw new BadRequestException(message != null ? message : AppMessages.ERROR_PERSISTENCIA);
            case 404 -> throw new NotFoundException(message != null ? message : AppMessages.ERROR_PERSISTENCIA);
            case 409 -> throw new ConflictException(message != null ? message : AppMessages.ERROR_PERSISTENCIA);
            default -> throw new RemoteServiceException(AppMessages.ERROR_PERSISTENCIA,
                new IllegalStateException(request.getMethod() + " " + request.getURI() + " → " + status.value()
                    + (message != null ? " " + message : "")));
        }
    }

    private static String message(ClientHttpResponse response) {
        try (InputStream body = response.getBody()) {
            JsonNode meta = MAPPER.readTree(body).path("meta");
            String message = meta.path("message").asString(null);
            return message == null || message.isBlank() ? null : message;
        } catch (IOException | RuntimeException ex) {
            return null;
        }
    }
}
