package com.microservice.userssecurity;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = stripeApiKey;
    }
}