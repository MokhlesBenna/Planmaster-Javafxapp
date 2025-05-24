package tn.esprit.services;

import tn.esprit.models.Produit;
import tn.esprit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService {
    private final Connection conn;

    public ProduitService() {
        this.conn = DBConnection.getConnection();
    }

    public void addProduit(Produit produit) throws SQLException {
        if (produit == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null.");
        }

        // ✅ Debugging Output: Check What is Passed to the Service
        System.out.println("Produit reçu - Nom: '" + produit.getNom() + "', Description: '" + produit.getDescription() + "'");

        if (produit.getNom() == null || produit.getNom().trim().isEmpty() ||
                produit.getDescription() == null || produit.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Nom et description du produit ne peuvent pas être vides.");
        }

        String query = "INSERT INTO produit (nom, description, prix, idFournisseur, image_url) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produit.getNom().trim());
            ps.setString(2, produit.getDescription().trim());
            ps.setFloat(3, produit.getPrix());
            ps.setInt(4, produit.getIdFournisseur());
            ps.setString(5, produit.getImageUrl() != null ? produit.getImageUrl() : "");
            ps.executeUpdate();
        }
    }
    public List<Produit> getAllProduits() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT idProduit, nom, description, prix, idFournisseur, image_url FROM produit";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getFloat("prix"),
                        rs.getInt("idFournisseur"),
                        rs.getString("image_url") // ✅ Fetch image URL
                ));
            }
        }
        return produits;
    }


    public void updateProduit(Produit produit) throws SQLException {
        if (produit == null || produit.getIdProduit() <= 0) {
            throw new IllegalArgumentException("Invalid product data.");
        }

        String query = "UPDATE produit SET nom=?, description=?, prix=?, idFournisseur=?, image_url=? WHERE idProduit=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setFloat(3, produit.getPrix());
            ps.setInt(4, produit.getIdFournisseur());
            ps.setString(5, produit.getImageUrl());
            ps.setInt(6, produit.getIdProduit());
            ps.executeUpdate();
        }
    }

    public void deleteProduit(int idProduit) throws SQLException {
        String query = "DELETE FROM produit WHERE idProduit=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProduit);
            ps.executeUpdate();
        }
    }

    public Produit getProduitById(int idProduit) throws SQLException {
        String query = "SELECT idProduit, nom, description, prix, idFournisseur, image_url FROM produit WHERE idProduit=?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idProduit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getFloat("prix"),
                        rs.getInt("idFournisseur"),
                        rs.getString("image_url") // ✅ Fetch image URL
                );
            }
        }
        return null;
    }

    public Produit getProduitByName(String name) throws SQLException {
        String query = "SELECT idProduit, nom, description, prix, idFournisseur, image_url FROM produit WHERE LOWER(nom) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getFloat("prix"),
                        rs.getInt("idFournisseur"),
                        rs.getString("image_url")
                );
            }
        }
        return null;
    }

    public List<Produit> searchProduits(String keyword) {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT idProduit, nom, description, prix, idFournisseur, image_url FROM produit WHERE LOWER(nom) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                produits.add(new Produit(
                        rs.getInt("idProduit"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getFloat("prix"),
                        rs.getInt("idFournisseur"),
                        rs.getString("image_url")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la recherche des produits : " + e.getMessage());
        }
        return produits;
    }
}
