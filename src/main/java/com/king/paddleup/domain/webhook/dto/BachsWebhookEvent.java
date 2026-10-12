package com.king.paddleup.domain.webhook.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BachsWebhookEvent(
        @JsonProperty("id")
        String id,

        @JsonProperty("type")
        String type,

        @JsonProperty("created_at")
        String createdAt,

        @JsonProperty("organization_id")
        String organizationId,

        @JsonProperty("data")
        BachsWebhookData data
) {}
