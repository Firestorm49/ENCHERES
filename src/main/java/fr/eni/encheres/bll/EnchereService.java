package fr.eni.encheres.bll;

import fr.eni.encheres.bo.*;
import java.util.List;
public interface EnchereService {
    void vendreArticle(CArticleVendu article);
    List<CEnchere> listerEncheresDeconnecte();
    List<CEnchere> listerEncheresConnecte();
    void faireEnchere(CEnchere enchere);
    CEnchere remporterVente(CArticleVendu vente);
    CEnchere afficherDetailEnchere(int enchereId);
    void modifierVente(CArticleVendu vente);
    void annulerVente(CArticleVendu vente);
    void ajouterPhotoVente(CArticleVendu vente);
    List<CEnchere> pagination(int pageNumber, int pageSize);
    List<CUtilisateur> voirEncherisseurs(CArticleVendu vente);
    void achatCredits(CUtilisateur utilisateur, int creditsAmount);

}
