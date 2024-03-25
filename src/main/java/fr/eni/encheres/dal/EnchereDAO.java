package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;

import java.time.LocalDateTime;
import java.util.List;

public interface EnchereDAO {
    String SoldArticle(CArticleVendu article);
    CArticleVendu viewArticle(int id);
    List<CArticleVendu> listEncheresDeconnecte();

    List<CEnchere> listEncheresByArticleId(int id);

    List<CArticleVendu> listEncheresConnecte();
    String ProposeEnchere(CEnchere enchere);
    int IsMaxOffre(CEnchere enchere);
    int IsUserMaxOffre(CEnchere enchere, int maxOffre);

    int WinnerOffre(int id);

    boolean IsPositifCredit(CEnchere enchere);
    boolean IsPositifOffre(CEnchere enchere);
    String remporterVente(CArticleVendu vente);
    CEnchere afficherDetailEnchere(int enchereId);
    String modifierVente(CArticleVendu vente);

    boolean IsPossibleModifySale(CArticleVendu vente);
    String SearchPhotoByArticleId(int id);
    int IsVenteFinish(int id);
    String annulerVente(int id);
    String CheckSale(LocalDateTime localDate);
    String ajouterPhotoVente(CArticleVendu vente);
    List<CUtilisateur> voirEncherisseurs(CArticleVendu vente);
    List<CArticleVendu> listerEncheresDeconnecteByFilters(String nomArticle, String categorie, int pageNumber, int pageSize);
    List<CArticleVendu> listerEncheresConnecteByFilters(String nomArticle, String categorie, int radio, boolean ventesencours, boolean ventesnoncommencer, boolean ventesterminer, boolean encheresremporter, boolean encheresencours, boolean encheresouvertes, int pageNumber, int pageSize);
}
