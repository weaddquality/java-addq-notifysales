package se.addq.notifysales.configuration;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import se.addq.notifysales.utils.RestRequestLoggingInterceptor;
import se.addq.notifysales.utils.RestTemplateErrorHandler;

@Configuration
class ApplicationConfig {

    @Bean(name = "restTemplate")
    public RestTemplate prepareRestTemplateForApplication() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        return restTemplateBuilder.requestFactory(() -> factory).errorHandler(new RestTemplateErrorHandler()).interceptors(new RestRequestLoggingInterceptor()).build();
    }


}