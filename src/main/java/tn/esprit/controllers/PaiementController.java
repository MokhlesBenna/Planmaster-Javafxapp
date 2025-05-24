package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.models.Commande;
import tn.esprit.services.CommandeService;
import tn.esprit.services.StripeService;

import java.awt.*;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

public class PaiementController {
    public javafx.scene.control.Label montaneTotalLabel;
    public javafx.scene.control.Label selectedCommandDetails;
    @FXML
    private ListView<Commande> validatedCommandeList;
    @FXML
    private Button payButton;


    private CommandeService commandeService = new CommandeService(); // Service for DB operations

    @FXML
    public void initialize() {
        List<Commande> commandes = commandeService.getValidatedCommandes();
        validatedCommandeList.getItems().addAll(commandes);

        // Set a custom cell factory for better UI
        validatedCommandeList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Commande commande, boolean empty) {
                super.updateItem(commande, empty);
                if (empty || commande == null) {
                    setText(null);
                } else {
                    setText("🛒 Commande #" + commande.getIdCommande() +
                            " | 💰 " + String.format("%.2f TND", commande.getMontantTotal()) +
                            " | 📅 " + commande.getDate());
                }
            }
        });
    }

    @FXML
    private void handlePayment() {
        Commande selectedCommande = validatedCommandeList.getSelectionModel().getSelectedItem();

        if (selectedCommande != null) {
            try {
                // Redirect to Stripe Payment
                String checkoutUrl = StripeService.createCheckoutSession(selectedCommande.getMontantTotal(), "usd");
                Desktop.getDesktop().browse(new URI(checkoutUrl));

                // Simulate successful payment (In real case, this should be handled after confirmation)
                showSuccessDialog();

                // Remove the paid order from the list
                validatedCommandeList.getItems().remove(selectedCommande);
                payButton.setDisable(true);

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(AlertType.ERROR, "Erreur lors de la redirection vers Stripe.").show();
            }
        } else {
            new Alert(AlertType.WARNING, "Veuillez sélectionner une commande à payer.").show();
        }
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) validatedCommandeList.getScene().getWindow();
        stage.close(); // Close current window
    }





    @FXML
    public void updateSelectedCommandeDetails(MouseEvent mouseEvent) {

        Commande selectedCommande = validatedCommandeList.getSelectionModel().getSelectedItem();

        if (selectedCommande != null) {

            montaneTotalLabel.setText(String.format("%.2f TND", selectedCommande.getMontantTotal()));


            selectedCommandDetails.setText(
                    "🆔 Client ID: " + selectedCommande.getIdClient() + "\n" +
                            "📅 Date: " + selectedCommande.getDate() + "\n" +
                            "💰 Montant: " + selectedCommande.getMontantTotal() + " TND\n" +
                            "📦 Quantité: " + selectedCommande.getQuantite() + "\n" +
                            "✅ Statut: " + selectedCommande.getStatus()
            );


            payButton.setDisable(false);
        }
    }
    private void showSuccessDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Paiement Réussi");
        alert.setHeaderText(null);
        alert.setContentText("🎉 Votre paiement a été effectué avec succès !\nMerci pour votre achat.");

        // Custom styling
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.toFront();

        alert.showAndWait();
    }


}
