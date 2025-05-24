package tn.esprit.models;

public class Fournisseur {
    private int idFournisseur;
    private String nomEntreprise;
    private String adresse;
    private String telephone;

    // Constructors
    public Fournisseur(int idFournisseur, String nomEntreprise, String adresse, String telephone) {
        this.idFournisseur = idFournisseur;
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    public Fournisseur(String nomEntreprise, String adresse, String telephone) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    // Getters and Setters
    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return nomEntreprise + " - " + adresse;
    }
}
