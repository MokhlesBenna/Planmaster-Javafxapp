package tn.esprit.services;

import tn.esprit.models.CartItem;
import tn.esprit.models.Produit;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static List<CartItem> cartItems = new ArrayList<>();

    public static void addToCart(Produit produit, int quantite) {
        for (CartItem item : cartItems) {
            if (item.getProduit().getIdProduit() == produit.getIdProduit()) {
                item.setQuantite(item.getQuantite() + quantite);
                return;
            }
        }
        cartItems.add(new CartItem(produit, quantite));
    }

    public static void removeFromCart(Produit produit) {
        cartItems.removeIf(item -> item.getProduit().getIdProduit() == produit.getIdProduit());
    }

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static float getTotalAmount() {
        float total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}
