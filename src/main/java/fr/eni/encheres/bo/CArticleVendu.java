package fr.eni.encheres.bo;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CArticleVendu {
    private int noArticle;
    @NotBlank(message = "Le nom de l'article n'est pas correcte")
    @Size(max=250)
    private String nomArticle;
    @NotBlank(message = "La description n'est pas correcte")
    @Size(max=400)
    private String description;
    @NotNull(message = "La date de début des enchères ne peut pas être nulle")
    private LocalDateTime dateDebutEncheres;

    @NotNull(message = "La date de fin des enchères ne peut pas être nulle")
    @Future(message = "La date de fin des enchères doit être dans le futur")
    private LocalDateTime dateFinEncheres;
    @Min(0)
    private int miseAPrix = 0;
    @Min(0)
    private int prixVente = 0;
    @Max(4)
    @Min(0)
    private int etatVente = 0;
    private CCategorie categorie;

    private String photo;
    private CRetrait retrait;
    private CUtilisateur Acheteur;
    private CUtilisateur Vendeur;

    public CArticleVendu() {

    }
    public CArticleVendu(int noArticle, String nomArticle, String description, LocalDateTime dateDebutEncheres, LocalDateTime dateFinEncheres, int miseAPrix, int prixVente, int etatVente, CCategorie categorie, String photo,CRetrait retrait,CUtilisateur Vendeur) {
        this.noArticle = noArticle;
        this.nomArticle = nomArticle;
        this.description = description;
        this.dateDebutEncheres = dateDebutEncheres;
        this.dateFinEncheres = dateFinEncheres;
        this.miseAPrix = miseAPrix;
        this.prixVente = prixVente;
        this.etatVente = etatVente;
        this.categorie = categorie;
        this.photo = photo;
        this.retrait = retrait;
        this.Vendeur = Vendeur;
    }

    public int getNoArticle() {
        return noArticle;
    }

    public void setNoArticle(int noArticle) {
        this.noArticle = noArticle;
    }

    public String getNomArticle() {
        return nomArticle;
    }

    public void setNomArticle(String nomArticle) {
        this.nomArticle = nomArticle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateDebutEncheres() {
        return dateDebutEncheres;
    }

    public void setDateDebutEncheres(LocalDateTime dateDebutEncheres) {
        this.dateDebutEncheres = dateDebutEncheres;
    }

    public LocalDateTime getDateFinEncheres() {
        return dateFinEncheres;
    }

    public void setDateFinEncheres(LocalDateTime dateFinEncheres) {
        this.dateFinEncheres = dateFinEncheres;
    }

    public int getMiseAPrix() {
        return miseAPrix;
    }

    public void setMiseAPrix(int miseAPrix) {
        this.miseAPrix = miseAPrix;
    }

    public int getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(int prixVente) {
        this.prixVente = prixVente;
    }

    public int getEtatVente() {
        return etatVente;
    }

    public void setEtatVente(int etatVente) {
        this.etatVente = etatVente;
    }

    public CCategorie getCategorie() {
        return categorie;
    }

    public void setCategorie(CCategorie categorie) {
        this.categorie = categorie;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CRetrait getRetrait() {
        return retrait;
    }

    public void setRetrait(CRetrait retrait) {
        this.retrait = retrait;
    }

    public CUtilisateur getAcheteur() {
        return Acheteur;
    }

    public void setAcheteur(CUtilisateur acheteur) {
        Acheteur = acheteur;
    }

    public CUtilisateur getVendeur() {
        return Vendeur;
    }

    public void setVendeur(CUtilisateur vendeur) {
        Vendeur = vendeur;
    }

    @Override
    public String toString() {
        return "CArticleVendu{" +
                "noArticle=" + noArticle +
                ", nomArticle='" + nomArticle + '\'' +
                ", description='" + description + '\'' +
                ", dateDebutEncheres='" + dateDebutEncheres + '\'' +
                ", dateFinEncheres='" + dateFinEncheres + '\'' +
                ", miseAPrix=" + miseAPrix +
                ", prixVente=" + prixVente +
                ", etatVente='" + etatVente + '\'' +
                ", categorie=" + categorie +
                ", photo='" + photo + '\'' +
                ", retrait=" + retrait +
                ", Acheteur=" + Acheteur +
                ", Vendeur=" + Vendeur +
                '}';
    }
}
