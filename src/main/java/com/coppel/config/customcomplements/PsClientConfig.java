package com.coppel.config.customcomplements;

import com.coppel.client.customcomplements.CustomComplementsPsClient;
import com.coppel.client.customcomplements.PsClientErrorHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.json.JsonMapper;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Cliente de ps-im-customComplements: RestClient con la interfaz {@link CustomComplementsPsClient} (@HttpExchange).
 */
@Configuration
@EnableConfigurationProperties(CustomComplementsProperties.class)
public class PsClientConfig {

    @Bean
    CustomComplementsPsClient customComplementsPsClient(RestClient.Builder builder, JsonMapper jsonMapper,
                                                        CustomComplementsProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.readTimeout());
        RestClient.Builder configured = configure(builder.clone(), jsonMapper)
            .baseUrl(properties.psUrl() == null ? "" : properties.psUrl())
            .requestFactory(requestFactory);
        return create(configured.build());
    }

    /** Errores de ps como excepciones de negocio y JSON con el JsonMapper de la aplicación (ver JacksonConfig). */
    public static RestClient.Builder configure(RestClient.Builder builder, JsonMapper jsonMapper) {
        return builder
            .configureMessageConverters(converters -> converters.withJsonConverter(
                new JacksonJsonHttpMessageConverter(jsonMapper)))
            .defaultStatusHandler(HttpStatusCode::isError, new PsClientErrorHandler());
    }

    public static CustomComplementsPsClient create(RestClient client) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client))
            .build()
            .createClient(CustomComplementsPsClient.class);
    }

    /** "Hoy" de las validaciones de fechas. */
    @Bean
    Clock customComplementsClock(CustomComplementsProperties properties) {
        return Clock.system(ZoneId.of(properties.timeZone()));
    }
}
