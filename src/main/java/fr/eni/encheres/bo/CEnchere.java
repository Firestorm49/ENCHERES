package fr.eni.encheres.bo;

import java.time.LocalDate;

public class CEnchere {
    private int noEnchere;
    private LocalDate dateEnchere;
    private int montant_enchere;
    private CUtilisateur utilisateur;

    private CArticleVendu article;

    public CEnchere() {

    }
    public CEnchere(int noEnchere,LocalDate dateEnchere, int montant_enchere,CUtilisateur utilisateur,CArticleVendu article) {
        this.noEnchere = noEnchere;
        this.dateEnchere = dateEnchere;
        this.montant_enchere = montant_enchere;
        this.utilisateur = utilisateur;
        this.article = article;
    }

    public int getNoEnchere() {
        return noEnchere;
    }

    public void setNoEnchere(int noEnchere) {
        this.noEnchere = noEnchere;
    }

    public LocalDate getDateEnchere() {
        return dateEnchere;
    }

    public void setDateEnchere(LocalDate dateEnchere) {
        this.dateEnchere = dateEnchere;
    }

    public int getMontant_enchere() {
        return montant_enchere;
    }

    public void setMontant_enchere(int montant_enchere) {
        this.montant_enchere = montant_enchere;
    }

    public CArticleVendu getArticle() {
        return article;
    }

    public void setArticle(CArticleVendu article) {
        this.article = article;
    }

    public CUtilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(CUtilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "CEnchere{" +
                "dateEnchere=" + dateEnchere +
                ", montant_enchere=" + montant_enchere +
                '}';
    }
}
