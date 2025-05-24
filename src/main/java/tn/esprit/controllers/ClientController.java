package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tn.esprit.models.Client;
import tn.esprit.services.ClientService;

import java.sql.SQLException;
import java.util.List;

public class ClientController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private ListView<Client> clientListView;

    private final ClientService clientService = new ClientService();

    @FXML
    public void initialize() {
        refreshClients();
    }

    /**
     * Adds a new Client to the database.
     */
    @FXML
    public void addClient() {
        try {
            String nom = nomField.getText();
            String prenom = prenomField.getText();
            String adresse = adresseField.getText();
            String telephone = telephoneField.getText();

            Client client = new Client(nom, prenom, adresse, telephone);
            clientService.addClient(client);
            refreshClients();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Client ajouté avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout du client.");
        }
    }

    /**
     * Deletes the selected Client.
     */
    @FXML
    public void deleteClient() {
        Client selectedClient = clientListView.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                clientService.deleteClient(selectedClient.getIdClient());
                refreshClients();
                showAlert(Alert.AlertType.INFORMATION, "Client supprimé !");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un client.");
        }
    }

    /**
     * Refreshes the ListView with updated Clients.
     */
    private void refreshClients() {
        try {
            List<Client> clients = clientService.getAllClients();
            ObservableList<Client> observableList = FXCollections.observableArrayList(clients);
            clientListView.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement des clients.");
        }
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        adresseField.clear();
        telephoneField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
