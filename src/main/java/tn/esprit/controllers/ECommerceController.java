package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.CartItem;
import tn.esprit.models.Produit;
import tn.esprit.services.ProduitService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ECommerceController {

    @FXML private GridPane productGrid;
    @FXML private VBox cartContainer;
    @FXML private VBox cartItemsContainer;
    @FXML private Button cartButton;
    @FXML private Button checkoutButton;

    private final ProduitService produitService = new ProduitService();
    private boolean isCartVisible = false;
    private final List<CartItem> cartItems = new ArrayList<>();

    @FXML
    public void initialize() {
        loadProducts();
    }

    private void loadProducts() {
        productGrid.getChildren().clear();

        try {
            List<Produit> produits = produitService.getAllProduits();
            int row = 0, col = 0;

            for (Produit produit : produits) {
                VBox productCard = createProductCard(produit);
                productGrid.add(productCard, col, row);
                col++;
                if (col > 2) { // 3 products per row
                    col = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createProductCard(Produit produit) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #FFF3E0; -fx-border-radius: 10px; -fx-padding: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 3);");

        ImageView imageView = new ImageView(new Image(
                produit.getImageUrl().isEmpty() ? getClass().getResource("/Images/default.png").toExternalForm() : produit.getImageUrl(),
                120, 120, true, true));

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D81B60;");

        Label priceLabel = new Label(produit.getPrix() + " TND");

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 10, 1);
        Button addToCartButton = new Button("Ajouter au Panier");
        addToCartButton.setStyle("-fx-background-color: #EC407A; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px;");
        addToCartButton.setOnAction(e -> addToCart(produit, quantitySpinner.getValue()));

        card.getChildren().addAll(imageView, nameLabel, priceLabel, quantitySpinner, addToCartButton);
        return card;
    }

    private void addToCart(Produit produit, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduit().getIdProduit() == produit.getIdProduit()) {
                item.setQuantite(item.getQuantite() + quantity);
                updateCartDisplay();
                return;
            }
        }

        cartItems.add(new CartItem(produit, quantity));
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartItemsContainer.getChildren().clear();

        if (cartItems.isEmpty()) {
            cartItemsContainer.getChildren().add(new Label("Votre panier est vide."));
        }

        for (CartItem item : cartItems) {
            HBox cartItem = new HBox(10);
            cartItem.setStyle("-fx-background-color: #FFEBEE; -fx-border-color: #D81B60; -fx-border-radius: 8px; -fx-padding: 10px;");

            ImageView imageView = new ImageView(new Image(item.getProduit().getImageUrl(), 50, 50, true, true));

            Label nameLabel = new Label(item.getProduit().getNom());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label priceLabel = new Label(item.getProduit().getPrix() * item.getQuantite() + " TND");

            Label quantityLabel = new Label("x" + item.getQuantite());

            Button removeBtn = new Button("❌");
            removeBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;");
            removeBtn.setOnAction(e -> {
                cartItems.remove(item);
                updateCartDisplay();
            });

            cartItem.getChildren().addAll(imageView, nameLabel, quantityLabel, priceLabel, removeBtn);
            cartItemsContainer.getChildren().add(cartItem);
        }
    }

    @FXML
    private void toggleCartDisplay() {
        isCartVisible = !isCartVisible;
        cartContainer.setVisible(isCartVisible);
    }

    @FXML
    private void clearCart() {
        cartItems.clear();
        updateCartDisplay();
    }

    @FXML
    private void goToCommande() {
        if (cartItems.isEmpty()) {
            showAlert("Panier Vide", "Ajoutez des produits avant de passer une commande.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CommandeView.fxml"));
            Parent root = loader.load();

            // Pass cart data to the command view
            CommandeController commandeController = loader.getController();
            commandeController.setCartItems(cartItems);

            Stage stage = (Stage) cartButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}