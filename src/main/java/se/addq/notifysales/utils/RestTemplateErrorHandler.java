package se.addq.notifysales.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.server.NotAcceptableStatusException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;


@Component
public class RestTemplateErrorHandler
        implements ResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {

        return (
                httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.SERVER_ERROR) {
            log.error(httpResponse.getStatusText());
            log.error(httpResponse.getBody().toString());
        } else if (httpResponse.getStatusCode()
                .series() == HttpStatus.Series.CLIENT_ERROR) {
            log.error(httpResponse.getStatusText());
            log.error(httpResponse.getBody().toString());
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotAcceptableStatusException("NOT_FOUND");
            }
        }
    }
}
