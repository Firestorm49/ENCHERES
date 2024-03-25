package fr.eni.encheres.bll;

import fr.eni.encheres.bo.*;
import java.util.List;

public interface EnchereService {
    String vendreArticle(CArticleVendu article);
    List<CArticleVendu> listerEncheresDeconnecte();
    List<CArticleVendu> listerEncheresConnecte();
    String faireEnchere(CEnchere enchere);
    String remporterVente(CArticleVendu vente);
    CEnchere afficherDetailEnchere(int enchereId);
    String modifierVente(CArticleVendu vente);
    String annulerVente(int id);
    List<CArticleVendu> listerEncheresDeconnecteByFilters(String nomArticle, int categorie, int pageNumber, int pageSize);
    List<CArticleVendu> listerEncheresConnecteByFilters(String nomArticle, int categorie,int no_utilisateur, int radio, boolean ventesencours, boolean ventesnoncommencer, boolean ventesterminer, boolean encheresremporter, boolean encheresencours, boolean encheresouvertes, int pageNumber, int pageSize);
    String CheckSale();
    String ajouterPhotoVente(CArticleVendu vente);
    List<CUtilisateur> voirEncherisseurs(CArticleVendu vente);
    CArticleVendu AfficherArticleById(int id);
    boolean VerifierCreditPositif(CEnchere enchere);
    boolean VerifierOffreSup(CEnchere enchere);
    String CreateRetrait(CRetrait Retrait);
    String ModifyRetrait(CRetrait Retrait);
    String DeleteRetrait(int id);
    CRetrait SearchRetrait(int id);
    CRetrait SearchRetraitByArticleID(int id);
    int IsMaxOffre(CEnchere enchere);
    int IsUserMaxOffre(CEnchere enchere, int maxOffre);
    boolean IsPositifCredit(CEnchere enchere);
    boolean IsPositifOffre(CEnchere enchere);
    int IsVenteFinish(int id);
    int WinnerOffre(int id);
    String SearchPhotoByArticleId(int id);
    List<CEnchere> listEncheresByArticleId(int id);
}
