package fr.eni.encheres.bll.Impl;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.dal.EnchereDAO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class EnchereServiceImpl implements EnchereService {
    private EnchereDAO encheresDAO;


    public EnchereServiceImpl(EnchereDAO encheresDAO) {
        this.encheresDAO = encheresDAO;
    }

    @Override
    public void vendreArticle(CArticleVendu article) {
        encheresDAO.SoldArticle(article);
    }

    @Override
    public List<CEnchere> listerEncheresDeconnecte() {
        return  encheresDAO.listEncheresDeconnecte();
    }

    @Override
    public List<CEnchere> listerEncheresConnecte() {
        return encheresDAO.listEncheresConnecte();
    }

    @Override
    public void faireEnchere(CEnchere enchere) {
         encheresDAO.ProposeEnchere(enchere);
    }

    @Override
    public CEnchere remporterVente(CArticleVendu vente) {
        return encheresDAO.remporterVente(vente);
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
    public void annulerVente(CArticleVendu vente) {
        encheresDAO.annulerVente(vente);
    }

    @Override
    public void ajouterPhotoVente(CArticleVendu vente) {
        encheresDAO.ajouterPhotoVente(vente);
    }

    @Override
    public List<CEnchere> pagination(int pageNumber, int pageSize) {
        return encheresDAO.pagination(pageNumber,pageSize);
    }

    @Override
    public List<CUtilisateur> voirEncherisseurs(CArticleVendu vente) {
        return encheresDAO.voirEncherisseurs(vente);
    }

    @Override
    public void achatCredits(CUtilisateur utilisateur, int creditsAmount) {
        encheresDAO.achatCredits(utilisateur,creditsAmount);
    }
}
