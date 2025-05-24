package tn.esprit.models;

public class CommandeProduits {
    private int idCommande;
    private int idProduit;
    private int quantity;

    // Constructors
    public CommandeProduits() {}

    public CommandeProduits(int idCommande, int idProduit, int quantity) {
        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getIdCommande() { return idCommande; }
    public void setIdCommande(int idCommande) { this.idCommande = idCommande; }

    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "CommandeProduits{" +
                "idCommande=" + idCommande +
                ", idProduit=" + idProduit +
                ", quantity=" + quantity +
                '}';
    }



}
