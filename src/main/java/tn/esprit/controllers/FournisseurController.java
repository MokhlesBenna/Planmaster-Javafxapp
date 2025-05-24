package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Fournisseur;
import tn.esprit.services.FournisseurService;

import java.sql.SQLException;
import java.util.List;

public class FournisseurController {
    @FXML private TextField nomEntrepriseField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private ListView<Fournisseur> fournisseurListView;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final FournisseurService fournisseurService = new FournisseurService();

    @FXML
    public void initialize() {
        refreshFournisseurs();
        disableActionButtons();

        fournisseurListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFournisseurFields(newSelection);
                enableActionButtons();
            } else {
                disableActionButtons();
            }
        });
    }

    /**
     * Adds a new Fournisseur to the database.
     */
    @FXML
    public void addFournisseur() throws SQLException {
        String nomEntreprise = nomEntrepriseField.getText().trim();
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nomEntreprise.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Fournisseur fournisseur = new Fournisseur(nomEntreprise, adresse, telephone);
        fournisseurService.addFournisseur(fournisseur);
        refreshFournisseurs();
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur ajouté avec succès !");
    }

    /**
     * Updates the selected Fournisseur in the database.
     */
    @FXML
    public void updateFournisseur() {
        Fournisseur selectedFournisseur = fournisseurListView.getSelectionModel().getSelectedItem();
        if (selectedFournisseur == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner un fournisseur.");
            return;
        }

        String nomEntreprise = nomEntrepriseField.getText().trim();
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nomEntreprise.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            selectedFournisseur.setNomEntreprise(nomEntreprise);
            selectedFournisseur.setAdresse(adresse);
            selectedFournisseur.setTelephone(telephone);

            fournisseurService.updateFournisseur(selectedFournisseur);
            refreshFournisseurs();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur mis à jour avec succès !");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le fournisseur.");
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected Fournisseur.
     */
    @FXML
    public void deleteFournisseur() {
        Fournisseur selectedFournisseur = fournisseurListView.getSelectionModel().getSelectedItem();
        if (selectedFournisseur == null) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner un fournisseur.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce fournisseur ?");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    fournisseurService.deleteFournisseur(selectedFournisseur.getIdFournisseur());
                    refreshFournisseurs();
                    clearFields();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Fournisseur supprimé avec succès !");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le fournisseur.");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Populates the form fields with the selected Fournisseur's data.
     */
    private void populateFournisseurFields(Fournisseur fournisseur) {
        nomEntrepriseField.setText(fournisseur.getNomEntreprise());
        adresseField.setText(fournisseur.getAdresse());
        telephoneField.setText(fournisseur.getTelephone());
    }

    /**
     * Refreshes the ListView with updated Fournisseurs.
     */
    private void refreshFournisseurs() {
        try {
            List<Fournisseur> fournisseurs = fournisseurService.getAllFournisseurs();
            ObservableList<Fournisseur> observableList = FXCollections.observableArrayList(fournisseurs);
            fournisseurListView.setItems(observableList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des fournisseurs.");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nomEntrepriseField.clear();
        adresseField.clear();
        telephoneField.clear();
    }

    private void disableActionButtons() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void enableActionButtons() {
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
