package tn.esprit.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.ArrayList;
import java.util.List;

public class StripeService {

    private static final String STRIPE_SECRET_KEY = "******************";

    static {
        Stripe.apiKey = STRIPE_SECRET_KEY; 
    }

    /**
     * Creates a Stripe Checkout Session and returns the session URL for redirection.
     *
     * @param amount  The total amount in TND.
     * @param currency The currency code (e.g., "tnd", "usd", etc.).
     * @return The Stripe checkout session URL.
     * @throws StripeException if an error occurs while creating the session.
     */
    public static String createCheckoutSession(double amount, String currency) throws StripeException {
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();


        long amountInCents = (long) (amount * 100);


        lineItems.add(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(currency)
                                        .setUnitAmount(amountInCents)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Paiement Commande") // Product Name
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        );


        String successUrl = "http://localhost:8080/success";
        String cancelUrl = "http://localhost:8080/cancel";


        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
