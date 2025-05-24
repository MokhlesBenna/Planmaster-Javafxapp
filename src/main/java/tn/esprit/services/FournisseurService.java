package tn.esprit.services;

import tn.esprit.models.Fournisseur;
import tn.esprit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FournisseurService {
    private final Connection connection;

    public FournisseurService() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Adds a new Fournisseur to the database.
     */
    public void addFournisseur(Fournisseur fournisseur) throws SQLException {
        String query = "INSERT INTO fournisseur (nomEntreprise, adresse, telephone) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fournisseur.getNomEntreprise());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getTelephone());
            pstmt.executeUpdate();

            // Retrieve and set the generated ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                fournisseur.setIdFournisseur(rs.getInt(1));
            }
        }
    }

    /**
     * Deletes a Fournisseur by ID.
     */
    public void deleteFournisseur(int idFournisseur) throws SQLException {
        String query = "DELETE FROM fournisseur WHERE idFournisseur = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idFournisseur);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all Fournisseurs' names for the ComboBox.
     */
    public List<String> getAllFournisseurNames() throws SQLException {
        List<String> fournisseurNames = new ArrayList<>();
        String query = "SELECT nomEntreprise FROM fournisseur";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                fournisseurNames.add(rs.getString("nomEntreprise"));
            }
        }
        return fournisseurNames;
    }

    /**
     * Retrieves a Fournisseur's ID by Name.
     */
    public int getFournisseurIdByName(String nomEntreprise) throws SQLException {
        String query = "SELECT idFournisseur FROM fournisseur WHERE nomEntreprise = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, nomEntreprise);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("idFournisseur");
            }
        }
        return -1; // Return -1 if the fournisseur is not found
    }

    /**
     * Retrieves all Fournisseurs from the database.
     */
    public List<Fournisseur> getAllFournisseurs() throws SQLException {
        List<Fournisseur> fournisseurs = new ArrayList<>();
        String query = "SELECT * FROM fournisseur";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                fournisseurs.add(new Fournisseur(
                        rs.getInt("idFournisseur"),
                        rs.getString("nomEntreprise"),
                        rs.getString("adresse"),
                        rs.getString("telephone")
                ));
            }
        }
        return fournisseurs;
    }

    /**
     * Updates an existing Fournisseur in the database.
     */
    public void updateFournisseur(Fournisseur fournisseur) throws SQLException {
        String query = "UPDATE fournisseur SET nomEntreprise = ?, adresse = ?, telephone = ? WHERE idFournisseur = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, fournisseur.getNomEntreprise());
            pstmt.setString(2, fournisseur.getAdresse());
            pstmt.setString(3, fournisseur.getTelephone());
            pstmt.setInt(4, fournisseur.getIdFournisseur());
            pstmt.executeUpdate();
        }
    }
}
