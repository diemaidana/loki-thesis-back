package com.loki.tesis.shared.mercadoPago;

import com.mercadopago.resources.payment.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/webhooks/mercadopago")
@RequiredArgsConstructor
@Slf4j
public class MercadoPagoWebhookController {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping
    public ResponseEntity<String> receiveNotification(@RequestBody JsonNode payload) {
        log.info("Webhook de MercadoPago: {}", payload.toString());

        try {
            if(payload.has("type") && "payment".equals(payload.get("type").asString())) {
                Long paymentId = payload.get("data").get("id").asLong();
                log.info("Processing payment ID: {}", paymentId);

                Payment officialPayment = mercadoPagoService.getPaymentById(paymentId);

                if("approved".equals(officialPayment.getStatus())) {
                    String orderId = officialPayment.getExternalReference();
                    log.info("Payment approved! Order ID: {}", orderId);
                } else {
                    log.info("Payment {} status: {}", paymentId, officialPayment.getStatus());
                }
            }

            return ResponseEntity.ok("Notification received correctly.");
        }catch (Exception e) {
            log.error("Error processing MercadoPago's webhook", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
