package tn.esprit.models;

public class Produit {
    private int idProduit;
    private String nom;
    private String description;
    private float prix;
    private int idFournisseur;
    private String imageUrl;

    // Constructor for Adding a Product
    public Produit(String nom, String description, float prix, int idFournisseur, String imageUrl) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom du produit ne peut pas être null ou vide.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description du produit ne peut pas être null ou vide.");
        }

        this.nom = nom.trim();
        this.description = description.trim();
        this.prix = prix;
        this.idFournisseur = idFournisseur;
        this.imageUrl = imageUrl != null ? imageUrl.trim() : "/Images/default_product.png";
    }

    // Constructor for Fetching Products
    public Produit(int idProduit, String nom, String description, float prix, int idFournisseur, String imageUrl) {
        this(nom, description, prix, idFournisseur, imageUrl); // Call the first constructor
        this.idProduit = idProduit;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public float getPrix() { return prix; }
    public int getIdFournisseur() { return idFournisseur; }
    public String getImageUrl() { return imageUrl; }
}