package com.loki.tesis.shared.mercadoPago;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MercadoPagoService {

    public String createPreference(String sellerAccessToken,
                                   List<MercadoPagoItemDTO> items,
                                   UUID orderId) {
        try {
            List<PreferenceItemRequest> preferenceItemsRequests = items.stream()
                    .map(item -> PreferenceItemRequest.builder()
                            .title(item.title())
                            .quantity(item.quantity())
                            .unitPrice(item.unitPrice())
                            .currencyId("ARS")
                            .build()
                    )
                    .toList();

            PreferenceBackUrlsRequest preferenceBackUrlsRequest = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:4200/payment/success")
                    .pending("http://localhost:4200/payment/pending")
                    .failure("http://localhost:4200/payment/failure")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(preferenceItemsRequests)
                    .backUrls(preferenceBackUrlsRequest)
                    .autoReturn("approved")
                    .externalReference(String.valueOf(orderId))
                    .build();

            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .accessToken(sellerAccessToken)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest, requestOptions);

            return preference.getInitPoint();
        }catch (Exception e) {
            throw new MercadoPagoException("MercadoPago connection failed: " + e.getMessage());
        }
    }

    public Payment getPaymentById(Long paymentId) {
        try{
            PaymentClient client = new PaymentClient();

            return client.get(paymentId);
        }catch (Exception e) {
            throw new MercadoPagoException("Payment not found: " + e.getMessage());
        }
    }
}
