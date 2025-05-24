package tn.esprit.models;

public class CartItem {
    private Produit produit;
    private int quantite;

    public CartItem(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public float getTotalPrice() {
        return produit.getPrix() * quantite;
    }

    @Override
    public String toString() {
        return produit.getNom() + " x" + quantite + " - " + getTotalPrice() + " TND";
    }
}
