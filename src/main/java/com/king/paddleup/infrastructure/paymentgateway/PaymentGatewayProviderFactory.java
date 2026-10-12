package com.king.paddleup.infrastructure.paymentgateway;

import com.king.paddleup.infrastructure.paymentgateway.enums.ProviderName;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentGatewayProviderFactory {
    private final Map<ProviderName, PaymentGatewayProvider<?>> providers;

    public PaymentGatewayProviderFactory(
            List<PaymentGatewayProvider<?>> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toMap(
                        PaymentGatewayProvider::getProviderName,
                        Function.identity()
                ));
    }

    @Value("${payment-gateway:BACHS}")
    private String activeGateway;

    public PaymentGatewayProvider<?> getProvider() {
        ProviderName providerName = ProviderName.valueOf(
                activeGateway.toUpperCase()
        );

        PaymentGatewayProvider<?> provider = providers.get(providerName);

        if (provider == null) {
            throw new IllegalArgumentException("Unknown provider name: " + activeGateway);
        }

        return provider;
    }
}
