package com.king.paddleup.infrastructure.paymentgateway.bachs;

import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeRequest;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;

public class BachsClient {
    private final RestClient restClient;

    public BachsClient(
            @Value("bachs.base-url") String baseUrl,
            @Value("bachs.api-key") String apiKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public PostChargeResponse postCharge(PostChargeRequest postChargeRequest) {
        restClient.post().uri("/charges")
                .body(postChargeRequest);
    }
}
