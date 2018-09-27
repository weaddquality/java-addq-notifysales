package se.addq.notifysales.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RestRequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        logRequest(request, body);
        ClientHttpResponse response = null;
        try {
            response = execution.execute(request, body);
            logResponse(response);
        } catch (IOException e) {
            log.error("Could not execute request");
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.debug("HTTP Request -> URI:{} Method:{} Headers:{} Body:{}", request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.debug("HTTP Response -> Status:{} {} Headers:{} Body:{}", response.getStatusText(), response.getStatusCode(), response.getHeaders(), StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
    }
}
