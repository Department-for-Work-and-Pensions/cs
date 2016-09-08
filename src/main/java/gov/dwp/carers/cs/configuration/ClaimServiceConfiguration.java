package gov.dwp.carers.cs.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Created by peterwhitehead on 30/06/2016.
 */
@Configuration
public class ClaimServiceConfiguration {
    @Value("${rest-read-timeout}")
    private Integer readTimeout;
    @Value("${rest-connection-timeout}")
    private Integer connectionTimeout;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(clientHttpRequestFactory());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectionTimeout);
        return factory;
    }
}
