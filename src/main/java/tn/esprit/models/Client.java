package tn.esprit.models;

public class Client {
    private int idClient;
    private String nom;
    private String prenom;
    private String adresseLivraison;
    private String telephone;

    // Constructors
    public Client(int idClient, String nom, String prenom, String adresseLivraison, String telephone) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenom = prenom;
        this.adresseLivraison = adresseLivraison;
        this.telephone = telephone;
    }

    public Client(String nom, String prenom, String adresseLivraison, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresseLivraison = adresseLivraison;
        this.telephone = telephone;
    }

    // Getters and Setters
    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " - " + adresseLivraison;
    }
}
