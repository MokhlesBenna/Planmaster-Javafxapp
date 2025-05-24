package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.CartItem;
import tn.esprit.models.Commande;
import tn.esprit.models.Produit;
import tn.esprit.services.CommandeService;
import tn.esprit.services.ProduitService;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeController {
    @FXML private TextField quantiteField;
    @FXML private ListView<String> produitListView;
    @FXML private ListView<String> commandeList;
    @FXML private TextField commandeUtilisateur;
    @FXML private DatePicker commandeDate;
    @FXML private Label montantTotalLabel;
    @FXML private Label errorIdClient;
    @FXML private Label errorDate;
    @FXML private Label errorProduit;
    @FXML private Label errorQuantite;

    private final ProduitService produitService = new ProduitService();
    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        System.out.println("✅ Initializing CommandeController...");

        // Check if UI components are properly loaded
        if (commandeList == null || produitListView == null) {
            System.err.println("❌ ERROR: UI components not initialized!");
            return; // Avoid executing code if components are not ready
        }

        loadProduits();  // ✅ Ensure produitListView is not null before using it
        loadCommandes();

        produitListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        produitListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    updateMontantTotal();
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors du calcul du montant total.");
                }
            }
        });

        commandeList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateCommandeFields(newSelection);
            }
        });

        System.out.println("✅ CommandeController initialized successfully.");
    }


    private void loadProduits() {
        if (produitListView == null) {
            System.err.println("❌ ERROR: produitListView is NULL when calling loadProduits()!");
            return; // Prevents application crash
        }

        produitListView.getItems().clear();
        try {
            List<Produit> produits = produitService.getAllProduits();
            if (produits.isEmpty()) {
                System.out.println("ℹ Aucun produit trouvé.");
            } else {
                for (Produit produit : produits) {
                    produitListView.getItems().add(produit.getIdProduit() + " - " + produit.getNom() + " - " + produit.getPrix() + " TND");
                }
                System.out.println("✅ Produits chargés avec succès.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les produits.");
        }
    }

    private void populateCommandeFields(String newSelection) {
        if (commandeUtilisateur == null || commandeDate == null || quantiteField == null) {
            System.err.println("❌ ERROR: UI fields are not initialized properly!");
            return;
        }

        try {
            int idCommande = Integer.parseInt(newSelection.replaceAll("\\D+", ""));
            Commande commande = commandeService.getCommandeById(idCommande);

            if (commande != null) {
                commandeUtilisateur.setText(String.valueOf(commande.getIdClient()));
                commandeDate.setValue(commande.getDate());
                quantiteField.setText(String.valueOf(commande.getQuantite()));
                montantTotalLabel.setText("Montant Total: " + commande.getMontantTotal() + " TND");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les détails de la commande.");
        }
    }


    private boolean validateFields() {
        boolean isValid = true;

        if (commandeUtilisateur.getText().trim().isEmpty()) {
            errorIdClient.setText("⚠ Veuillez entrer l'ID du client.");
            isValid = false;
        } else {
            try {
                Integer.parseInt(commandeUtilisateur.getText().trim());
                errorIdClient.setText("");
            } catch (NumberFormatException e) {
                errorIdClient.setText("⚠ L'ID client doit être un nombre valide.");
                isValid = false;
            }
        }

        if (commandeDate.getValue() == null) {
            errorDate.setText("⚠ Veuillez sélectionner une date.");
            isValid = false;
        } else if (commandeDate.getValue().isBefore(LocalDate.now())) {
            errorDate.setText("⚠ La date ne peut pas être dans le passé.");
            isValid = false;
        } else {
            errorDate.setText("");
        }
        return isValid;
    }



    private void updateMontantTotal() throws SQLException {
        float total = 0.0f;
        int quantite = 1;

        try {
            quantite = Integer.parseInt(quantiteField.getText().trim());
        } catch (NumberFormatException ignored) {}

        for (String selectedItem : produitListView.getSelectionModel().getSelectedItems()) {
            int productId = Integer.parseInt(selectedItem.replaceAll("\\D+", ""));
            Produit produit = produitService.getProduitById(productId);
            if (produit != null) {
                total += produit.getPrix() * quantite;
            }
        }
        montantTotalLabel.setText("Montant Total: " + total + " TND");
    }

    @FXML
    private void createCommande() {
        if (!validateFields()) {
            return;
        }
        try {
            int idClient = Integer.parseInt(commandeUtilisateur.getText().trim());
            LocalDate selectedDate = commandeDate.getValue();
            int totalQuantity = 0;
            float totalAmount = 0.0f;

            for (CartItem item : cartItems) {
                totalQuantity += item.getQuantite();
                totalAmount += item.getTotalPrice();
            }

            Commande commande = new Commande(0, idClient, selectedDate, "En attente", totalAmount, totalQuantity);
            int idCommande = commandeService.addCommande(commande);

            if (idCommande != -1) {
                showAlertSuccess("Succès", "Commande ajoutée avec succès !");
                loadCommandes();
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout de la commande.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur avec la base de données.");
        }
    }

    private void loadCommandes() {
        if (commandeList == null) {
            System.err.println("❌ ERROR: commandeList is NULL when calling loadCommandes()!");
            return;
        }
        commandeList.getItems().clear();
        try {
            List<Commande> commandes = commandeService.getAllCommandes();
            for (Commande commande : commandes) {
                commandeList.getItems().add("Commande #" + commande.getIdCommande() + " - " +
                        commande.getStatus() + " - " + commande.getMontantTotal() + " TND");
            }
            System.out.println("✅ Commandes chargées avec succès.");
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les commandes.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void goBack(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ECommerceView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<CartItem> cartItems = new ArrayList<>();

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items;
        updateCommandeFromCart();

        if (produitListView != null) {
            produitListView.getItems().clear();
            for (CartItem item : cartItems) {
                produitListView.getItems().add(item.getProduit().getNom() + " - " + item.getQuantite() + " pcs");
            }
        } else {
            System.err.println("❌ ERROR: produitListView is NULL when setting cart items!");
        }
    }


    private void updateCommandeFromCart() {
        float totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getTotalPrice();
        }
        montantTotalLabel.setText("Montant Total: " + totalAmount + " TND");
    }

    @FXML
    private void handlePayerCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PaiementView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Paiement des Commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
