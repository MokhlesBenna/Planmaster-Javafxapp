package tn.esprit.controllers;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.models.Produit;
import tn.esprit.services.FournisseurService;
import tn.esprit.services.ProduitService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static tn.esprit.util.DBConnection.getConnection;

public class ProduitController {
    @FXML private TextField produitNom;
    @FXML private TextField produitDescription;
    @FXML private TextField produitPrix;
    @FXML private ComboBox<String> produitFournisseur;
    @FXML private TextField searchField;
    @FXML private ListView<String> produitListView;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private ImageView produitImageView;
    @FXML private Label errorDescription;
    @FXML private Label errorPrix;
    @FXML private Label errorFournisseur;
    @FXML private Label errorNom;

    private String selectedImagePath = null; // Stores the image path
    private final ProduitService produitService = new ProduitService();
    private final FournisseurService fournisseurService = new FournisseurService();


    public ProduitController() {
        // Initialize the connection (example using a hypothetical Database class)
    }

    @FXML
    public void initialize() {
        loadProduits();
        loadFournisseurs();
        disableActionButtons();

        produitListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateProduitFields(newSelection);
                enableActionButtons();
            } else {
                disableActionButtons();
            }
        });

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            searchProduits(newValue);
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        String nom = produitNom.getText().trim();
        String description = produitDescription.getText().trim();

        if (nom.isEmpty()) {
            errorNom.setText("⚠ Veuillez entrer le nom du produit.");
            isValid = false;
        } else {
            errorNom.setText("");
        }

        if (description.isEmpty()) {
            errorDescription.setText("⚠ Veuillez entrer une description.");
            isValid = false;
        } else {
            errorDescription.setText("");
        }

        if (produitPrix.getText().trim().isEmpty()) {
            errorPrix.setText("⚠ Veuillez entrer un prix.");
            isValid = false;
        } else {
            try {
                Float.parseFloat(produitPrix.getText().trim());
                errorPrix.setText("");
            } catch (NumberFormatException e) {
                errorPrix.setText("⚠ Le prix doit être un nombre valide.");
                isValid = false;
            }
        }

        if (produitFournisseur.getValue() == null) {
            errorFournisseur.setText("⚠ Veuillez sélectionner un fournisseur.");
            isValid = false;
        } else {
            errorFournisseur.setText("");
        }

        System.out.println("Validation Status: " + isValid); // Debugging Output

        return isValid;
    }

    private void searchProduits(String keyword) {
        produitListView.getItems().clear();
        List<Produit> produits = produitService.searchProduits(keyword);
        for (Produit produit : produits) {
            produitListView.getItems().add(produit.getNom() + " - " + produit.getPrix() + " TND");
        }
    }

    private void disableActionButtons() {
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void enableActionButtons() {
        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void clearFields() {
        produitNom.clear();
        produitDescription.clear();
        produitPrix.clear();
        produitFournisseur.getSelectionModel().clearSelection();
    }

    private void loadProduits() {
        produitListView.getItems().clear();
        try {
            List<Produit> produits = produitService.getAllProduits();
            for (Produit produit : produits) {
                produitListView.getItems().add(produit.getNom() + " - " + produit.getPrix() + " TND");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les produits.");
            e.printStackTrace();
        }
    }

    private void populateProduitFields(String selectedProduit) {
        try {
            String productName = selectedProduit.split(" - ")[0];
            Produit produit = produitService.getProduitByName(productName);
            if (produit == null) {
                showAlert("Erreur", "Produit introuvable dans la base de données.");
                return;
            }

            produitNom.setText(produit.getNom());
            produitDescription.setText(produit.getDescription());
            produitPrix.setText(String.valueOf(produit.getPrix()));

            List<String> fournisseurNames = fournisseurService.getAllFournisseurNames();
            if (!fournisseurNames.isEmpty() && produit.getIdFournisseur() > 0 && produit.getIdFournisseur() <= fournisseurNames.size()) {
                produitFournisseur.setValue(fournisseurNames.get(produit.getIdFournisseur() - 1));
            } else {
                produitFournisseur.setValue(null);
                showAlert("Erreur", "Le fournisseur du produit est introuvable.");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des détails du produit.");
        }
    }

    @FXML
    private void updateProduit() {
        String selectedProduit = produitListView.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit à modifier.");
            return;
        }

        boolean isValid = validateFields();
        if (!isValid) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
            return;
        }

        try {
            String productName = selectedProduit.split(" - ")[0];
            Produit produit = produitService.getProduitByName(productName);
            if (produit == null) {
                showAlert("Erreur", "Produit introuvable dans la base de données.");
                return;
            }

            produit.setNom(produitNom.getText().trim());
            produit.setDescription(produitDescription.getText().trim());
            produit.setPrix(Float.parseFloat(produitPrix.getText().trim()));

            String selectedFournisseur = produitFournisseur.getValue();
            int idFournisseur = fournisseurService.getFournisseurIdByName(selectedFournisseur);
            if (idFournisseur == -1) {
                errorFournisseur.setText("⚠ Fournisseur non trouvé.");
                return;
            }
            produit.setIdFournisseur(idFournisseur);

            produitService.updateProduit(produit);
            showAlertSuccess("Succès", "Produit mis à jour avec succès !");
            clearFields();
            loadProduits();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur avec la base de données.");
        }
    }

    @FXML
    private void supprimerProduit() {
        String selectedProduit = produitListView.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showAlert("Erreur", "Veuillez sélectionner un produit à supprimer.");
            return;
        }
        try {
            String productName = selectedProduit.split(" - ")[0];
            Produit produit = produitService.getProduitByName(productName);
            if (produit == null) {
                showAlert("Erreur", "Produit introuvable dans la base de données.");
                return;
            }
            produitService.deleteProduit(produit.getIdProduit());
            showAlertSuccess("Succès", "Produit supprimé avec succès.");
            clearFields();
            loadProduits();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur avec la base de données.");
        }
    }

    private void loadFournisseurs() {
        try {
            List<String> fournisseurs = fournisseurService.getAllFournisseurNames();
            produitFournisseur.getItems().setAll(fournisseurs);
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les fournisseurs.");
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

    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.toURI().toString(); // Convert path to URI format
            produitImageView.setImage(new Image(selectedImagePath));
        }
    }

    @FXML
    private void addProduit() {
        if (!validateFields()) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
            return;
        }

        try {
            String nom = produitNom.getText().trim();
            String description = produitDescription.getText().trim();
            float prix = Float.parseFloat(produitPrix.getText().trim());
            String selectedFournisseur = produitFournisseur.getValue();

            System.out.println("🔍 Données envoyées - Nom: '" + nom + "', Description: '" + description + "'");

            int idFournisseur = fournisseurService.getFournisseurIdByName(selectedFournisseur);
            if (idFournisseur == -1) {
                showAlert("Erreur", "Fournisseur invalide.");
                return;
            }

            String imageUrl = (selectedImagePath != null) ? selectedImagePath : "/Images/default_product.png";

            // ✅ Debugging before passing to ProduitService
            Produit produit = new Produit(nom, description, prix, idFournisseur, imageUrl);
            System.out.println("✅ Produit créé - Nom: '" + produit.getNom() + "', Description: '" + produit.getDescription() + "'");

            produitService.addProduit(produit);
            showAlertSuccess("Succès", "Produit ajouté avec succès !");
            clearFields();
            loadProduits();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur avec la base de données.");
            e.printStackTrace();
        }
    }
    @FXML
    private void exportProduitsToPdf() {
        // Prompt user to choose where to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Liste_Produits.pdf");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                PdfWriter writer = new PdfWriter(new FileOutputStream(file));
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Title
                Paragraph title = new Paragraph("🛍 Liste des Produits")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(18);
                document.add(title);

                // Create Table (Explicitly using iText's Cell)
                Table table = new Table(UnitValue.createPercentArray(new float[]{2, 5, 3, 3})).useAllAvailableWidth();
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("ID").setBold()));
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Nom du Produit").setBold()));
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Prix (TND)").setBold()));
                table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Fournisseur").setBold()));

                // Fetch Products and Populate Table
                List<Produit> produits = produitService.getAllProduits();
                for (Produit produit : produits) {
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(produit.getIdProduit()))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(produit.getNom())));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.format("%.2f", produit.getPrix()))));
                    table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(produit.getIdFournisseur()))));
                }

                document.add(table);
                document.close();

                showAlertSuccess("✅ Exportation Réussie", "Le fichier PDF a été enregistré avec succès !");
            } catch (FileNotFoundException e) {
                showAlert("❌ Erreur", "Impossible d'enregistrer le fichier PDF.");
            } catch (Exception e) {
                showAlert("❌ Erreur", "Une erreur s'est produite lors de l'exportation.");
                e.printStackTrace();
            }
        }
    }


}