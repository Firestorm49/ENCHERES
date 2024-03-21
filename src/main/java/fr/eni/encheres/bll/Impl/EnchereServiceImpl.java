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
    public void vendreArticle(CArticleVendu article) {

        encheresDAO.SoldArticle(article);
    }

    @Override
    public List<CEnchere> listerEncheresDeconnecte() {

        return encheresDAO.listEncheresDeconnecte();
    }

    @Override
    public List<CEnchere> listerEncheresConnecte() {

        return encheresDAO.listEncheresConnecte();
    }
    @Override
    public void CheckSale() {
        LocalDateTime localDate  = LocalDateTime.now();
       encheresDAO.CheckSale(localDate);
    }
    @Override
    public void faireEnchere(CEnchere enchere) {
        if (VerifierCreditPositif(enchere) && VerifierOffreSup(enchere)) {
            encheresDAO.ProposeEnchere(enchere);
        }
    }

    @Override
    public void remporterVente(CArticleVendu vente) {
        encheresDAO.remporterVente(vente);
    }

    @Override
    public CEnchere afficherDetailEnchere(int enchereId) {

        return encheresDAO.afficherDetailEnchere(enchereId);
    }

    @Override
    public void modifierVente(CArticleVendu vente) {

        encheresDAO.modifierVente(vente);
    }

    @Override
    public void annulerVente(int vente) {

        encheresDAO.annulerVente(vente);
    }

    @Override
    public void ajouterPhotoVente(CArticleVendu vente) {

        encheresDAO.ajouterPhotoVente(vente);
    }

    @Override
    public List<CArticleVendu> pagination(int pageNumber, int pageSize) {
        return encheresDAO.pagination(pageNumber, pageSize);
    }

    @Override
    public List<CUtilisateur> voirEncherisseurs(CArticleVendu vente) {

        return encheresDAO.voirEncherisseurs(vente);
    }

    @Override
    public void achatCredits(CUtilisateur utilisateur, int creditsAmount) {
        encheresDAO.achatCredits(utilisateur, creditsAmount);
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
    public void CreateRetrait(CRetrait Retrait) {

        retraitDAO.CreateRetrait(Retrait);
    }

    @Override
    public void ModifyRetrait(CRetrait Retrait) {

        retraitDAO.ModifyRetrait(Retrait);
    }

    @Override
    public void DeleteRetrait(int id) {

        retraitDAO.DeleteRetrait(id);
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
}
