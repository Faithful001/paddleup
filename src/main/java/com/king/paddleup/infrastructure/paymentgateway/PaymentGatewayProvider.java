package com.king.paddleup.infrastructure.paymentgateway;

import com.king.paddleup.domain.user.User;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeResponse;
import com.king.paddleup.infrastructure.paymentgateway.enums.ProviderName;

public interface PaymentGatewayProvider<T> {
    PostChargeResponse pay(User user, T postCharge, String idempotencyKey);

    default PostChargeResponse pay(User user, T postCharge) {
        return pay(user, postCharge, null);
    }

    ProviderName getProviderName();
}
