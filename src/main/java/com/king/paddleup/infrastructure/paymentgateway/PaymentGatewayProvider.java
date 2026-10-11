package com.king.paddleup.infrastructure.paymentgateway;

import com.king.paddleup.domain.user.User;
import com.king.paddleup.infrastructure.paymentgateway.enums.ProviderName;

public interface PaymentGatewayProvider<T> {
    void pay(User user, T postCharge);
    ProviderName getProviderName();
}
