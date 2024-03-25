package fr.eni.encheres.bll.Impl;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CRetrait;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.dal.EnchereDAO;
import fr.eni.encheres.dal.RetraitDAO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
public class EnchereServiceImpl implements EnchereService {
    private EnchereDAO encheresDAO;

    private RetraitDAO retraitDAO;

    public EnchereServiceImpl(EnchereDAO encheresDAO, RetraitDAO retraitDAO) {

        this.encheresDAO = encheresDAO;
        this.retraitDAO = retraitDAO;
    }

    @Override
    public String vendreArticle(CArticleVendu article) {
     return encheresDAO.SoldArticle(article);
    }

    @Override
    public List<CArticleVendu> listerEncheresDeconnecte() {

        return encheresDAO.listEncheresDeconnecte();
    }

    @Override
    public List<CArticleVendu> listerEncheresConnecte() {

        return encheresDAO.listEncheresConnecte();
    }

    @Override
    public List<CArticleVendu> listerEncheresDeconnecteByFilters(String nomArticle, String categorie, int pageNumber, int pageSize) {
        return encheresDAO.listerEncheresDeconnecteByFilters(nomArticle,categorie,pageNumber,pageSize);
    }
    @Override
    public List<CArticleVendu> listerEncheresConnecteByFilters(String nomArticle, String categorie,int no_utilisateur, int radio, boolean ventesencours, boolean ventesnoncommencer, boolean ventesterminer, boolean encheresremporter, boolean encheresencours, boolean encheresouvertes, int pageNumber, int pageSize) {
        return encheresDAO.listerEncheresConnecteByFilters(nomArticle, categorie, no_utilisateur, radio, ventesencours, ventesnoncommencer, ventesterminer, encheresremporter, encheresencours, encheresouvertes, pageNumber, pageSize);
    }
    @Override
    public String CheckSale() {
        LocalDateTime localDate  = LocalDateTime.now();
        return encheresDAO.CheckSale(localDate);
    }
    @Override
    public String faireEnchere(CEnchere enchere) {
        if (VerifierCreditPositif(enchere) && VerifierOffreSup(enchere)) {
            return encheresDAO.ProposeEnchere(enchere);
        }
        else{
            return "Erreur Crédit insuffisant ou offre infèrieur";
        }
    }

    @Override
    public String remporterVente(CArticleVendu vente) {
        return encheresDAO.remporterVente(vente);
    }

    @Override
    public CEnchere afficherDetailEnchere(int enchereId) {

        return encheresDAO.afficherDetailEnchere(enchereId);
    }

    @Override
    public String modifierVente(CArticleVendu vente) {
        return encheresDAO.modifierVente(vente);
    }

    @Override
    public String annulerVente(int vente) {
        return encheresDAO.annulerVente(vente);
    }

    @Override
    public String ajouterPhotoVente(CArticleVendu vente) {
        return encheresDAO.ajouterPhotoVente(vente);
    }

    @Override
    public List<CUtilisateur> voirEncherisseurs(CArticleVendu vente) {

        return encheresDAO.voirEncherisseurs(vente);
    }

    @Override
    public CArticleVendu AfficherArticleById(int id) {

        return encheresDAO.viewArticle(id);
    }

    @Override
    public boolean VerifierCreditPositif(CEnchere enchere) {

        return encheresDAO.IsPositifCredit(enchere);
    }

    @Override
    public boolean VerifierOffreSup(CEnchere enchere) {

        return encheresDAO.IsPositifOffre(enchere);
    }

    @Override
    public String CreateRetrait(CRetrait Retrait) {
        return retraitDAO.CreateRetrait(Retrait);
    }

    @Override
    public String ModifyRetrait(CRetrait Retrait) {
        return retraitDAO.ModifyRetrait(Retrait);
    }

    @Override
    public String DeleteRetrait(int id) {
        return retraitDAO.DeleteRetrait(id);
    }

    @Override
    public CRetrait SearchRetrait(int id) {
        return retraitDAO.SearchRetrait(id);
    }

    @Override
    public CRetrait SearchRetraitByArticleID(int id) {
        return retraitDAO.SearchRetraitByArticleID(id);
    }

    @Override
    public int IsMaxOffre(CEnchere enchere) {
        return encheresDAO.IsMaxOffre(enchere);
    }

    @Override
    public int IsUserMaxOffre(CEnchere enchere, int maxOffre) {
        return encheresDAO.IsUserMaxOffre(enchere, maxOffre);
    }

    @Override
    public boolean IsPositifCredit(CEnchere enchere) {
        return encheresDAO.IsPositifCredit(enchere);
    }

    @Override
    public boolean IsPositifOffre(CEnchere enchere) {

        return encheresDAO.IsPositifOffre(enchere);
    }

    @Override
    public int IsVenteFinish(int id) {
        return encheresDAO.IsVenteFinish(id);
    }

    @Override
    public int WinnerOffre(int id) {
        return encheresDAO.WinnerOffre(id);
    }

    @Override
    public String SearchPhotoByArticleId(int id) {
        return encheresDAO.SearchPhotoByArticleId(id);
    }

    @Override
    public List<CEnchere> listEncheresByArticleId(int id) {
        return encheresDAO.listEncheresByArticleId(id);
    }
}
