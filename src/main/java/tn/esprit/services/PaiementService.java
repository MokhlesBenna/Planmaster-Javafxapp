package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

import java.util.HashMap;
import java.util.Map;

public class PaiementService {

    // Replace with your actual test secret key (do NOT expose real keys in production).
    private static final String STRIPE_SECRET_KEY = "*******************************************************";

    /**
     * Creates a charge on Stripe using the provided payment token.
     *
     * @param token       The payment source token (e.g., "tok_visa" for testing).
     * @param amountInCents The total amount to charge in cents (e.g., 1000 = 10.00 TND).
     * @param currency    The currency code (e.g., "usd", "eur", "tnd", etc.).
     * @param description A short description for the payment.
     * @return The created Charge object.
     * @throws StripeException if the payment fails or an error occurs.
     */
    public static Charge createCharge(String token, long amountInCents, String currency, String description) throws StripeException {
        // Set Stripe API key
        Stripe.apiKey = STRIPE_SECRET_KEY;

        // Prepare charge parameters
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amountInCents);
        chargeParams.put("currency", currency);
        chargeParams.put("source", token); 
        chargeParams.put("description", description);

        // Create the charge
        return Charge.create(chargeParams);
    }
}
