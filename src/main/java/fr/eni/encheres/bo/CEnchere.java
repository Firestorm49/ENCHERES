package fr.eni.encheres.bo;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public class CEnchere {
    private int noEnchere;
    @NotNull(message = "La date de l' enchère ne peut pas être nulle")
    private LocalDateTime dateEnchere;
    @Min(1)
    private int montant_enchere;
    private CUtilisateur utilisateur;
    private CArticleVendu article;

    public CEnchere() {

    }
    public CEnchere(int noEnchere,LocalDateTime dateEnchere, int montant_enchere,CUtilisateur utilisateur,CArticleVendu article) {
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

    public LocalDateTime getDateEnchere() {
        return dateEnchere;
    }

    public void setDateEnchere(LocalDateTime dateEnchere) {
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
