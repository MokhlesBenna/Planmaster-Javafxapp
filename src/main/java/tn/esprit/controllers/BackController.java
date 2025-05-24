package tn.esprit.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.models.Commande;
import tn.esprit.services.CommandeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class BackController {

    @FXML private ListView<String> commandeListView;
    @FXML private Button filterAllButton;
    @FXML private Button filterPendingButton;
    @FXML private Button filterAcceptedButton;
    @FXML private Button filterRejectedButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageNumberLabel;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button updateStatusButton;
    @FXML private Label errorMessageLabel;

    private final CommandeService commandeService = new CommandeService();
    private List<Commande> allCommandes;
    private int currentPage = 1;
    private final int itemsPerPage = 4;
    private String currentFilter = "Tous";

    @FXML
    public void initialize() {
        loadCommandes(currentFilter);

        applyFadeTransition(commandeListView);

        filterAllButton.setOnAction(this::filterAll);
        filterPendingButton.setOnAction(this::filterPending);
        filterAcceptedButton.setOnAction(this::filterAccepted);
        filterRejectedButton.setOnAction(this::filterRejected);

        prevPageButton.setOnAction(this::prevPage);
        nextPageButton.setOnAction(this::nextPage);

        updateStatusButton.setOnAction(this::updateCommandeStatus);
        statusComboBox.getItems().addAll("Validée", "Rejetée");

        updatePagination();
    }

    @FXML
    private void filterRejected(ActionEvent actionEvent) {
        currentFilter = "Rejetée";
        loadCommandes(currentFilter);
    }

    @FXML
    private void filterAccepted(ActionEvent actionEvent) {
        currentFilter = "Validée";
        loadCommandes(currentFilter);
    }

    @FXML
    private void filterPending(ActionEvent actionEvent) {
        currentFilter = "En attente";
        loadCommandes(currentFilter);
    }

    @FXML
    private void filterAll(ActionEvent actionEvent) {
        currentFilter = "Tous";
        loadCommandes(currentFilter);
    }

    private void applyFadeTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void loadCommandes(String filter) {
        try {
            allCommandes = commandeService.getAllCommandes().stream()
                    .filter(commande -> filter.equals("Tous") || commande.getStatus().equals(filter))
                    .collect(Collectors.toList());

            currentPage = 1;
            updatePagination();
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les commandes.");
        }
    }

    private void updatePagination() {
        commandeListView.getItems().clear();

        int totalItems = allCommandes.size();
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        if (startIndex < totalItems) {
            List<String> paginatedList = allCommandes.subList(startIndex, endIndex)
                    .stream()
                    .map(commande -> "Commande #" + commande.getIdCommande() + " - " +
                            commande.getStatus() + " - " + commande.getMontantTotal() + " TND")
                    .collect(Collectors.toList());

            commandeListView.getItems().addAll(paginatedList);
        }

        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(endIndex >= totalItems);

        pageNumberLabel.setText(String.valueOf(currentPage));

        applyFadeTransition(commandeListView);
    }

    public void prevPage(ActionEvent actionEvent) {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }

    public void nextPage(ActionEvent actionEvent) {
        int totalItems = allCommandes.size();
        if ((currentPage * itemsPerPage) < totalItems) {
            currentPage++;
            updatePagination();
        }
    }

    public void updateCommandeStatus(ActionEvent actionEvent) {
        String selectedCommande = commandeListView.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            displayFieldError("Veuillez sélectionner une commande.");
            return;
        }

        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.isEmpty()) {
            displayFieldError("Veuillez sélectionner un statut.");
            return;
        }

        int idCommande = Integer.parseInt(selectedCommande.split("#")[1].split(" - ")[0].trim());

        try {
            commandeService.updateCommandeStatus(idCommande, newStatus);
            showAlert("Succès", "Statut mis à jour avec succès !");
            loadCommandes(currentFilter);
        } catch (SQLException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void displayFieldError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
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
}