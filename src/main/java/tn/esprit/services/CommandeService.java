package tn.esprit.services;

import tn.esprit.models.Commande;
import tn.esprit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeService {
    private final Connection conn;

    public CommandeService() {
        this.conn = DBConnection.getConnection();
    }

    /**
     * Adds a new Commande to the database.
     *
     * @param commande The Commande object to insert.
     * @return The generated ID of the newly created Commande.
     * @throws SQLException if a database error occurs.
     */
    public int addCommande(Commande commande) throws SQLException {
        String query = "INSERT INTO commande (idClient, date, status, montant_total, quantite) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, commande.getIdClient());
        ps.setDate(2, Date.valueOf(commande.getDate()));
        ps.setString(3, "En attente");
        ps.setFloat(4, commande.getMontantTotal());
        ps.setInt(5, commande.getQuantite()); // ✅ Insert quantite

        int affectedRows = ps.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Échec de l'ajout de la commande.");
        }

        ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : -1;
    }

    /**
     * Retrieves all Commandes from the database.
     *
     * @return A list of Commande objects.
     * @throws SQLException if a database error occurs.
     */
    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            commandes.add(new Commande(
                    rs.getInt("idCommande"),
                    rs.getInt("idClient"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    rs.getFloat("montant_total"),
                    rs.getInt("quantite") // ✅ Retrieve quantite
            ));
        }
        return commandes;
    }

    /**
     * Updates an existing Commande in the database.
     *
     * @param commande The Commande object with updated data.
     * @throws SQLException if a database error occurs.
     */
    public void updateCommande(Commande commande) throws SQLException {
        String query = "UPDATE commande SET idClient = ?, date = ?, montant_total = ?, quantite = ? WHERE idCommande = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, commande.getIdClient());
        ps.setDate(2, Date.valueOf(commande.getDate())); // Convert LocalDate to java.sql.Date
        ps.setFloat(3, commande.getMontantTotal());
        ps.setInt(4, commande.getQuantite()); // ✅ Update quantite
        ps.setInt(5, commande.getIdCommande());
        ps.executeUpdate();
    }

    /**
     * Deletes a Commande by its ID.
     *
     * @param idCommande The ID of the Commande to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteCommande(int idCommande) throws SQLException {
        String query = "DELETE FROM commande WHERE idCommande = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCommande);
        ps.executeUpdate();
    }

    /**
     * Retrieves a Commande by its ID.
     *
     * @param idCommande The ID of the Commande.
     * @return The Commande object if found, or null otherwise.
     * @throws SQLException if a database error occurs.
     */
    public Commande getCommandeById(int idCommande) throws SQLException {
        String query = "SELECT * FROM commande WHERE idCommande = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCommande);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Commande(
                    rs.getInt("idCommande"),
                    rs.getInt("idClient"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    rs.getFloat("montant_total"),
                    rs.getInt("quantite") // ✅ Retrieve quantite
            );
        }
        return null;
    }

    /**
     * Searches for Commandes by Client ID.
     *
     * @param idClient The client ID to search for.
     * @return A list of matching Commande objects.
     * @throws SQLException if a database error occurs.
     */
    public List<Commande> searchCommandesByClient(int idClient) throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande WHERE idClient = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idClient);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            commandes.add(new Commande(
                    rs.getInt("idCommande"),
                    rs.getInt("idClient"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("status"),
                    rs.getFloat("montant_total"),
                    rs.getInt("quantite") // ✅ Retrieve quantite
            ));
        }
        return commandes;
    }

    /**
     * Updates the status of a Commande.
     *
     * @param idCommande The ID of the Commande to update.
     * @param newStatus  The new status to set.
     * @throws SQLException if a database error occurs or if status modification is not allowed.
     */
    public void updateCommandeStatus(int idCommande, String newStatus) throws SQLException {
        String currentStatusQuery = "SELECT status FROM commande WHERE idCommande = ?";
        PreparedStatement currentStatusPs = conn.prepareStatement(currentStatusQuery);
        currentStatusPs.setInt(1, idCommande);
        ResultSet rs = currentStatusPs.executeQuery();

        if (rs.next()) {
            String currentStatus = rs.getString("status");

            if ("Validée".equals(currentStatus) || "Rejetée".equals(currentStatus)) {
                throw new SQLException("Le statut ne peut plus être modifié.");
            }

            String updateQuery = "UPDATE commande SET status = ? WHERE idCommande = ?";
            PreparedStatement ps = conn.prepareStatement(updateQuery);
            ps.setString(1, newStatus);
            ps.setInt(2, idCommande);
            ps.executeUpdate();
        }
    }

    public List<Commande> getValidatedCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT idCommande, idClient, date, status, montant_total, quantite FROM commande WHERE status = 'Validée'";

        try (
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Commande commande = new Commande(
                        rs.getInt("idCommande"),
                        rs.getInt("idClient"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("status"),
                        rs.getFloat("montant_Total"),
                        rs.getInt("quantite") // Ensure new field is retrieved correctly
                );
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandes;
    }

    public void markCommandeAsPaid(int idCommande) throws SQLException {
        String query = "UPDATE commande SET isPaid = 1 WHERE idCommande = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, idCommande);
        ps.executeUpdate();
    }
}