package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;

import java.util.List;

public interface EnchereDAO {
    void SoldArticle(CArticleVendu article);
    CArticleVendu viewArticle(int id);
    List<CEnchere> listEncheresDeconnecte();
    List<CEnchere> listEncheresConnecte();
    void ProposeEnchere(CEnchere enchere);

    boolean IsPositifCredit(CEnchere enchere);

    boolean IsPositifOffre(CEnchere enchere);

    CEnchere remporterVente(CArticleVendu vente);
    CEnchere afficherDetailEnchere(int enchereId);
    void modifierVente(CArticleVendu vente);
    void annulerVente(CArticleVendu vente);
    void ajouterPhotoVente(CArticleVendu vente);
    List<CArticleVendu> pagination(int pageNumber, int pageSize);
    List<CUtilisateur> voirEncherisseurs(CArticleVendu vente);
    void achatCredits(CUtilisateur utilisateur, int creditsAmount);
}
