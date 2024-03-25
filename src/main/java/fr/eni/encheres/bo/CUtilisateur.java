package fr.eni.encheres.bo;

import java.util.List;

import jakarta.validation.constraints.*;

public class CUtilisateur {

    private int noUtilisateur;
    @NotBlank(message = "Le pseudo n'est pas correcte")
    @Size(max=250)
    private String pseudo;
    @NotBlank(message = "Le nom n'est pas correcte")
    @Size(max=250)
    private String nom;
    @NotBlank(message = "Le prénom n'est pas correcte")
    @Size(max=250)
    private String prenom;
    @NotBlank(message = "L'email n'est pas correcte")
    @Size(max=250)
    @Email
    private String email;
    @NotBlank(message = "Le téléphone n'est pas correcte")
    @Size(max=250)
    private String telephone;
    @NotBlank(message = "La rue n'est pas correcte")
    @Size(max=250)
    private String rue;
    @Max(99999)
    @Min(1)
    private int codePostal;
    @NotBlank(message = "La ville n'est pas correcte")
    @Size(max=250)
    private String ville;
    @NotBlank(message = "Le mot de passe n'est pas correcte")
    @Size(max=250, min=8)
    private String motdepasse;
    @Min(0)
    private int credit;
    private int administrateur;
    private boolean Active;

    public CUtilisateur() {

    }

    public CUtilisateur(int noUtilisateur, String pseudo, String nom, String prenom, String email, String telephone, String rue, int codePostal, String ville, String motdepasse, int credit, int administrateur) {
        this.noUtilisateur = noUtilisateur;
        this.pseudo = pseudo;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.rue = rue;
        this.codePostal = codePostal;
        this.ville = ville;
        this.motdepasse = motdepasse;
        this.credit = credit;
        this.administrateur = administrateur;
    }

    public int getNoUtilisateur() {
        return noUtilisateur;
    }

    public void setNoUtilisateur(int noUtilisateur) {
        this.noUtilisateur = noUtilisateur;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public int getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public void setMotdepasse(String motdepasse) {
        this.motdepasse = motdepasse;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int isAdministrateur() {
        return administrateur;
    }

    public void setAdministrateur(int administrateur) {
        this.administrateur = administrateur;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    @Override
    public String toString() {
        return "CUtilisateur{" +
                "noUtilisateur=" + noUtilisateur +
                ", pseudo='" + pseudo + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", rue='" + rue + '\'' +
                ", codePostal='" + codePostal + '\'' +
                ", ville='" + ville + '\'' +
                ", motdepasse='" + motdepasse + '\'' +
                ", credit=" + credit +
                ", administrateur=" + administrateur +
                '}';
    }
}
