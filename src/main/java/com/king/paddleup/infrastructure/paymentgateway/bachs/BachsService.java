package com.king.paddleup.infrastructure.paymentgateway.bachs;

import com.king.paddleup.domain.user.User;
import com.king.paddleup.infrastructure.paymentgateway.PaymentGatewayProvider;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeRequest;
import com.king.paddleup.infrastructure.paymentgateway.bachs.dto.PostChargeResponse;
import com.king.paddleup.infrastructure.paymentgateway.enums.ProviderName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BachsService implements PaymentGatewayProvider<PostChargeRequest> {
    private final BachsClient bachsClient;

    @Override
    public void pay(User user, PostChargeRequest request) {
        PostChargeResponse result = bachsClient.postCharge(request);
    }

    @Override
    public ProviderName getProviderName() {
        return null;
    }
}
