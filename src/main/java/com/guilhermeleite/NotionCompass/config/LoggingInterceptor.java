package com.guilhermeleite.NotionCompass.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            logger.info("--- HTTP Request ---\nURI: {}\nMethod: {}\nHeaders: {}\nBody: {}",
                    request.getURI(), request.getMethod(), request.getHeaders(), new String(body, Charset.defaultCharset()));
        } catch (Exception ex) {
            logger.warn("Failed to log request details", ex);
        }

        ClientHttpResponse response = execution.execute(request, body);

        try {
            byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
            logger.info("--- HTTP Response ---\nStatus code: {}\nHeaders: {}\nBody: {}",
                    response.getStatusCode(), response.getHeaders(), new String(responseBody, Charset.defaultCharset()));
        } catch (Exception ex) {
            logger.warn("Failed to log response details", ex);
        }

        return response;
    }
}

