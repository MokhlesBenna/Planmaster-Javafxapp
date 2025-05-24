package tn.esprit.services;

import tn.esprit.models.Client;
import tn.esprit.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private final Connection connection;

    public ClientService() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Add a new Client to the database.
     */
    public void addClient(Client client) throws SQLException {
        String query = "INSERT INTO client (nom, prenom, adresse_livraison, telephone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresseLivraison());
            pstmt.setString(4, client.getTelephone());
            pstmt.executeUpdate();

            // Retrieve generated ID
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                client.setIdClient(rs.getInt(1));
            }
        }
    }

    /**
     * Retrieve all Clients from the database.
     */
    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM client";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id_client"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("adresse_livraison"),
                        rs.getString("telephone")
                ));
            }
        }
        return clients;
    }

    /**
     * Update an existing Client in the database.
     */
    public void updateClient(Client client) throws SQLException {
        String query = "UPDATE client SET nom=?, prenom=?, adresse_livraison=?, telephone=? WHERE id_client=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenom());
            pstmt.setString(3, client.getAdresseLivraison());
            pstmt.setString(4, client.getTelephone());
            pstmt.setInt(5, client.getIdClient());
            pstmt.executeUpdate();
        }
    }

    /**
     * Delete a Client by ID.
     */
    public void deleteClient(int idClient) throws SQLException {
        String query = "DELETE FROM client WHERE id_client=?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idClient);
            pstmt.executeUpdate();
        }
    }
}
