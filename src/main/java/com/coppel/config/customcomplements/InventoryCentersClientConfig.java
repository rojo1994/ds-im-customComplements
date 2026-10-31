package com.coppel.config.customcomplements;

import com.coppel.client.customcomplements.InventoryCentersClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.json.JsonMapper;

/**
 * Cliente de ps-tms-fmd-inventarios-centrales. Ese API valida la sesión con el header {@code Authorization}, así que
 * se reenvía el de la petición que llegó a ds.
 */
@Configuration
public class InventoryCentersClientConfig {

    @Bean
    InventoryCentersClient inventoryCentersClient(RestClient.Builder builder, JsonMapper jsonMapper,
                                                  CustomComplementsProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.readTimeout());
        RestClient client = PsClientConfig.configure(builder.clone(), jsonMapper)
            .baseUrl(properties.centralesUrl() == null ? "" : properties.centralesUrl())
            .requestFactory(requestFactory)
            .requestInterceptor(forwardAuthorization())
            .build();
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
            .build()
            .createClient(InventoryCentersClient.class);
    }

    static ClientHttpRequestInterceptor forwardAuthorization() {
        return (request, body, execution) -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null) {
                    request.getHeaders().set(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
            return execution.execute(request, body);
        };
    }
}
