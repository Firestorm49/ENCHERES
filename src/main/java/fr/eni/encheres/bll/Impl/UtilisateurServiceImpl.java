package fr.eni.encheres.bll.Impl;

import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.dal.UtilisateurDAO;
import fr.eni.encheres.exceptions.BusinessCode;
import fr.eni.encheres.exceptions.BusinessException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private UtilisateurDAO utilisateursDAO;

    BusinessException be = new BusinessException();


    public UtilisateurServiceImpl(UtilisateurDAO utilisateursDAO) {
        this.utilisateursDAO = utilisateursDAO;
    }
    @Override
    public String Inscription(CUtilisateur utilisateur) {
        return utilisateursDAO.Subscribe(utilisateur);
    }

    @Override
    public CUtilisateur ViewProfil(int id) {
        return utilisateursDAO.ViewProfil(id);
    }

    @Override
    public String DeleteProfil(int id) {
        return utilisateursDAO.DeleteProfil(id);
    }

    @Override
    public String ModifyProfil(CUtilisateur utilisateur) {

           return utilisateursDAO.ModifyProfil(utilisateur);

    }

    @Override
    public String DeleteProfil(CUtilisateur utilisateur) {
        return utilisateursDAO.DeleteProfil(utilisateur);
    }

    @Override
    public String DesactiveProfil(CUtilisateur utilisateur) {
        return utilisateursDAO.DesactiveProfil(utilisateur);
    }

    @Override
    public String DeleteMultiProfil(List<CUtilisateur> utilisateurList) {
        return utilisateursDAO.DeleteMultiProfil(utilisateurList);
    }
    @Override
    public CUtilisateur getUtilisateurByEmail(String mail) {
        return utilisateursDAO.getUtilisateurByEmail(mail);
    }

    @Override
    public boolean checkUser(String mail) {
        return utilisateursDAO.checkUser(mail);
    }

    @Override
    public CUtilisateur viewAcheteurByArticleID(int id) {
        return utilisateursDAO.viewAcheteurByArticleID(id);
    }

    @Override
    public String ModifyRoleUtilisateur(int id, int isAdministrateur) {
        return utilisateursDAO.ModifyRoleUtilisateur(id,isAdministrateur);
    }
    @Override
    public String achatCredits(CUtilisateur utilisateur, int creditsAmount) {
        return utilisateursDAO.achatCredits(utilisateur, creditsAmount);
    }
    @Override
    public List<CUtilisateur> ViewAllUtilisateurs() {
        return utilisateursDAO.ViewAllUtilisateurs();
    }

    @Override
    public String ActiveProfil(CUtilisateur utilisateur) {
        return utilisateursDAO.ActiveProfil(utilisateur);
    }

    @Override
    public boolean verifPassword(String mdp, CUtilisateur utilisateur) {
        return utilisateursDAO.checkPassword(mdp, utilisateur.getNoUtilisateur());
    }
}
