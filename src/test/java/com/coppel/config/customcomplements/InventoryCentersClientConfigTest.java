package com.coppel.config.customcomplements;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ps-tms-fmd-inventarios-centrales valida la sesión: el Authorization que llegó a ds se reenvía tal cual.
 */
class InventoryCentersClientConfigTest {

    private final ClientHttpRequestInterceptor interceptor = InventoryCentersClientConfig.forwardAuthorization();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void forwardsIncomingAuthorization() throws IOException {
        MockHttpServletRequest incoming = new MockHttpServletRequest();
        incoming.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(incoming));

        assertThat(send().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer token");
    }

    @Test
    void withoutIncomingRequestSendsNoAuthorization() throws IOException {
        assertThat(send().getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)).isFalse();
    }

    private MockClientHttpRequest send() throws IOException {
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST,
            URI.create("http://centrales/custom-complements-furniture"));
        interceptor.intercept(request, new byte[0], (req, body) -> new MockClientHttpResponse());
        return request;
    }
}
