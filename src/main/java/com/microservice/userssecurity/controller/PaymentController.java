package com.microservice.userssecurity.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    static class CreatePaymentItem {
        private String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
    }

    @Getter
    @Setter
    static class CreatePaymentRequest {
        private CreatePaymentItem[] items;
        private Double amount;

    }

    static class CreatePaymentResponse {
        private String clientSecret;
        public CreatePaymentResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }
        public String getClientSecret() {
            return clientSecret;
        }
    }

    private Long calculateOrderAmount(CreatePaymentItem[] items, Double clientAmount) {
        // Verificamos que el monto del cliente no sea null
        if (clientAmount == null) {
            throw new IllegalArgumentException("Customer amount cannot be null");
        }
        Long amountInCents = Math.round(clientAmount * 100);
        return amountInCents;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@RequestBody CreatePaymentRequest request) {
        logger.info("Recibida solicitud de creación de PaymentIntent");
        try {
            logger.info("Configurando clave API de Stripe");
            Stripe.apiKey = stripeApiKey;

            logger.info("Calculando monto del pedido");
            Long amountInCents = calculateOrderAmount(request.getItems(), request.getAmount());
            logger.info("Monto calculado: {} centavos", amountInCents);

            logger.info("Creando parámetros de PaymentIntent");
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("mxn")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            logger.info("Creando PaymentIntent");
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            logger.info("PaymentIntent creado exitosamente");
            CreatePaymentResponse response = new CreatePaymentResponse(paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Error al crear PaymentIntent: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreatePaymentResponse(null));
        } catch (IllegalArgumentException e) {
            logger.error("Error en los argumentos: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CreatePaymentResponse(null));
        } catch (Exception e) {
            logger.error("Error inesperado: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new CreatePaymentResponse(null));
        }
    }

}