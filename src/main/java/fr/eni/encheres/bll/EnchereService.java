package fr.eni.encheres.bll;

import fr.eni.encheres.bo.*;
import java.util.List;

public interface EnchereService {
    void vendreArticle(CArticleVendu article);
    List<CEnchere> listerEncheresDeconnecte();
    List<CEnchere> listerEncheresConnecte();
    void faireEnchere(CEnchere enchere);
    void remporterVente(CArticleVendu vente);
    CEnchere afficherDetailEnchere(int enchereId);
    void modifierVente(CArticleVendu vente);
    void annulerVente(int id);
    void CheckSale();
    void ajouterPhotoVente(CArticleVendu vente);
    List<CArticleVendu> pagination(int pageNumber, int pageSize);
    List<CUtilisateur> voirEncherisseurs(CArticleVendu vente);
    void achatCredits(CUtilisateur utilisateur, int creditsAmount);
    CArticleVendu AfficherArticleById(int id);
    boolean VerifierCreditPositif(CEnchere enchere);
    boolean VerifierOffreSup(CEnchere enchere);
    void CreateRetrait(CRetrait Retrait);
    void ModifyRetrait(CRetrait Retrait);
    void DeleteRetrait(int id);
    CRetrait SearchRetrait(int id);
    CRetrait SearchRetraitByArticleID(int id);
    int IsMaxOffre(CEnchere enchere);
    int IsUserMaxOffre(CEnchere enchere, int maxOffre);
    boolean IsPositifCredit(CEnchere enchere);
    boolean IsPositifOffre(CEnchere enchere);
    int IsVenteFinish(int id);
    int WinnerOffre(int id);
    String SearchPhotoByArticleId(int id);

}
