package com.king.paddleup.infrastructure.paymentgateway.bachs;

import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeRequest;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeResponse;
import com.king.paddleup.infrastructure.paymentgateway.exception.PaymentGatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class BachsClient {

    private final RestClient restClient;

    public BachsClient(
            @Value("${bachs.base-url:https://sandbox-api.bachs.io/v1}") String baseUrl,
            @Value("${bachs.api-key:}") String apiKey
    ) {
        String authHeaderValue = (apiKey != null && apiKey.startsWith("Bearer "))
                ? apiKey
                : "Bearer " + (apiKey != null ? apiKey : "");

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeaderValue)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public PostChargeResponse postCharge(PostChargeRequest postChargeRequest, String idempotencyKey) {
        try {
            return restClient.post()
                    .uri("/charges")
                    .headers(headers -> {
                        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                            headers.set("Idempotency-Key", idempotencyKey);
                        }
                    })
                    .body(postChargeRequest)
                    .retrieve()
                    .body(PostChargeResponse.class);
        } catch (RestClientResponseException ex) {
            log.error("Bachs API error: status={}, response={}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            throw new PaymentGatewayException("Bachs payment charge failed: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error("Bachs request execution failure", ex);
            throw new PaymentGatewayException("Error communicating with Bachs payment gateway: " + ex.getMessage(), ex);
        }
    }

    public PostChargeResponse postCharge(PostChargeRequest postChargeRequest) {
        return postCharge(postChargeRequest, null);
    }
}
