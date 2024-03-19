package fr.eni.encheres.bll.Impl;

import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.dal.UtilisateurDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private UtilisateurDAO utilisateursDAO;


    public UtilisateurServiceImpl(UtilisateurDAO utilisateursDAO) {
        this.utilisateursDAO = utilisateursDAO;
    }
    @Override
    public void Inscription(CUtilisateur utilisateur) {
        utilisateursDAO.Subscribe(utilisateur);
    }

    @Override
    public CUtilisateur ViewProfil(int id) {
        return utilisateursDAO.ViewProfil(id);
    }

    @Override
    public void DeleteProfil(int id) {
        utilisateursDAO.DeleteProfil(id);
    }

    @Override
    public void ModifyProfil(CUtilisateur utilisateur) {
        utilisateursDAO.ModifyProfil(utilisateur);
    }

    @Override
    public void DeleteProfil(CUtilisateur utilisateur) {
        utilisateursDAO.DeleteProfil(utilisateur);
    }

    @Override
    public void DesactiveProfil(CUtilisateur utilisateur) {
        utilisateursDAO.DesactiveProfil(utilisateur);
    }

    @Override
    public void DeleteMultiProfil(List<CUtilisateur> utilisateurList) {
        utilisateursDAO.DeleteMultiProfil(utilisateurList);
    }
    @Override
    public CUtilisateur getUtilisateurByEmail(String mail) {
        return utilisateursDAO.getUtilisateurByEmail(mail);
    }
}
