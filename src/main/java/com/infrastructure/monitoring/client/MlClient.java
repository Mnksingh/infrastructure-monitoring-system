package com.infrastructure.monitoring.client;

import com.infrastructure.monitoring.dto.MlPredictionResponseDTO;
import com.infrastructure.monitoring.dto.MlProjectFeaturesDTO;
import com.infrastructure.monitoring.exception.MlClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;

@Component
public class MlClient {

    private static final Logger log = LoggerFactory.getLogger(MlClient.class);

    private final RestClient restClient;
    private final String mlServiceUrl;

    public MlClient(
            @Value("${ML_API_URL}") String mlServiceUrl,
            @Value("${ml.service.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${ml.service.read-timeout-ms:10000}") int readTimeoutMs) {

        this.mlServiceUrl = mlServiceUrl;

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(
                Duration.ofMillis(connectTimeoutMs)
        );

        requestFactory.setReadTimeout(
                Duration.ofMillis(readTimeoutMs)
        );

        this.restClient = RestClient.builder()
                .baseUrl(mlServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public MlPredictionResponseDTO predictRisk(
            MlProjectFeaturesDTO features) {

        try {
            log.info(
                    "Dispatching feature vector to FastAPI at {}/predict: {}",
                    mlServiceUrl,
                    features
            );

            MlPredictionResponseDTO response = restClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(features)
                    .retrieve()
                    .body(MlPredictionResponseDTO.class);

            if (response == null) {
                throw new MlClientException(
                        "Received empty response body from ML service",
                        HttpStatus.BAD_GATEWAY
                );
            }

            log.info(
                    "Received prediction from FastAPI: risk_level={}, probability={}, threshold={}",
                    response.riskLevel(),
                    response.riskProbability(),
                    response.threshold()
            );

            return response;

        } catch (ResourceAccessException ex) {

            log.error(
                    "ML service connection failure or timeout at {}: {}",
                    mlServiceUrl,
                    ex.getMessage()
            );

            throw new MlClientException(
                    "ML prediction service is unavailable or timed out at "
                            + mlServiceUrl,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    ex
            );

        } catch (RestClientResponseException ex) {

            log.error(
                    "ML service returned HTTP {}: {}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString()
            );

            HttpStatus httpStatus =
                    HttpStatus.resolve(ex.getStatusCode().value());

            if (httpStatus == null) {
                httpStatus = HttpStatus.BAD_GATEWAY;
            }

            throw new MlClientException(
                    "ML service error (" + ex.getStatusCode() + "): "
                            + ex.getResponseBodyAsString(),
                    httpStatus,
                    ex
            );

        } catch (MlClientException ex) {

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "Unexpected error calling ML prediction service: {}",
                    ex.getMessage(),
                    ex
            );

            throw new MlClientException(
                    "Failed to communicate with ML prediction service: "
                            + ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ex
            );
        }
    }
}

