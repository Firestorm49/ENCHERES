package fr.eni.encheres.bo;

import java.time.LocalDate;
public class CArticleVendu {
    private int noArticle;
    private String nomArticle;
    private String description;
    private LocalDate  dateDebutEncheres;
    private LocalDate dateFinEncheres;
    private int miseAPrix = 0;
    private int prixVente = 0;
    private int etatVente = 0;
    private CCategorie categorie;
    private String photo;
    private CRetrait retrait;
    private CUtilisateur Acheteur;
    private CUtilisateur Vendeur;

    public CArticleVendu() {

    }
    public CArticleVendu(int noArticle, String nomArticle, String description, LocalDate dateDebutEncheres, LocalDate dateFinEncheres, int miseAPrix, int prixVente, int etatVente, CCategorie categorie, String photo,CRetrait retrait,CUtilisateur Vendeur) {
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

    public LocalDate getDateDebutEncheres() {
        return dateDebutEncheres;
    }

    public void setDateDebutEncheres(LocalDate dateDebutEncheres) {
        this.dateDebutEncheres = dateDebutEncheres;
    }

    public LocalDate getDateFinEncheres() {
        return dateFinEncheres;
    }

    public void setDateFinEncheres(LocalDate dateFinEncheres) {
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
