package tn.esprit.models;

import java.time.LocalDate;

public class Commande {
    private int idCommande;
    private int idClient;
    private LocalDate date;
    private String status; // "En attente", "Validée", "Rejetée"
    private float montantTotal;
    private int quantite; // ✅ New attribute added

    // Default Constructor
    public Commande() {
        this.status = "En attente"; // Default status at creation
        this.quantite = 1; // Default quantity to 1
    }

    // Constructor with All Fields
    public Commande(int idCommande, int idClient, LocalDate date, String status, float montantTotal, int quantite) {
        this.idCommande = idCommande;
        this.idClient = idClient;
        this.date = date;
        this.status = status;
        this.montantTotal = montantTotal;
        this.quantite = quantite;
    }

    // Getters and Setters
    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(float montantTotal) {
        this.montantTotal = montantTotal;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Commande #" + idCommande + " | Client: " + idClient + " | Montant: " + montantTotal + " TND | " + date;
    }


}
