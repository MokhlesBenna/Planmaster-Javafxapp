package tn.esprit.services;

import tn.esprit.models.CommandeProduits;
import tn.esprit.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandeProduitsService {
    private final Connection connection;

    public CommandeProduitsService() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Ajoute un produit à une commande dans la table commande_produits.
     */
    public void ajouterCommandeProduit(CommandeProduits commandeProduits) {
        String query = "INSERT INTO commande_produits (idCommande, idProduit, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, commandeProduits.getIdCommande());
            pstmt.setInt(2, commandeProduits.getIdProduit());
            pstmt.setInt(3, commandeProduits.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère tous les produits liés à une commande spécifique.
     */
    public List<CommandeProduits> getProduitsByCommande(int idCommande) {
        List<CommandeProduits> produitsList = new ArrayList<>();
        String query = "SELECT * FROM commande_produits WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                produitsList.add(new CommandeProduits(
                        rs.getInt("idCommande"),
                        rs.getInt("idProduit"),
                        rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produitsList;
    }

    /**
     * Met à jour la quantité d'un produit dans une commande.
     */
    public void modifierCommandeProduit(int idCommande, int idProduit, int nouvelleQuantite) {
        String query = "UPDATE commande_produits SET quantite = ? WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, nouvelleQuantite);
            pstmt.setInt(2, idCommande);
            pstmt.setInt(3, idProduit);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Quantité mise à jour avec succès.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un produit spécifique d'une commande.
     */
    public void supprimerCommandeProduit(int idCommande, int idProduit) {
        String query = "DELETE FROM commande_produits WHERE idCommande = ? AND idProduit = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            pstmt.setInt(2, idProduit);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Produit supprimé de la commande.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime tous les produits liés à une commande (lorsqu'une commande est supprimée).
     */
    public void supprimerProduitsDeCommande(int idCommande) {
        String query = "DELETE FROM commande_produits WHERE idCommande = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCommande);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Tous les produits liés à la commande ont été supprimés.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCommandeProduit(CommandeProduits commandeProduits) {
    }
}
