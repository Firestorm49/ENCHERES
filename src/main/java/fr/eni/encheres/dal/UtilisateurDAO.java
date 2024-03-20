package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CUtilisateur;

import java.util.List;

public interface UtilisateurDAO {
    void Subscribe(CUtilisateur utilisateur);
    CUtilisateur viewAcheteurByArticleID(int id);
    CUtilisateur ViewProfil(int id);
    void ModifyRoleUtilisateur(int id, boolean isAdministrateur);
    void DeleteProfil(int id);
    List<CUtilisateur> ViewAllUtilisateurs();
    void ModifyProfil(CUtilisateur utilisateur);
    void DeleteProfil(CUtilisateur utilisateur);
    void DesactiveProfil(CUtilisateur utilisateur);
    void ActiveProfil(CUtilisateur utilisateur);
    void DeleteMultiProfil(List<CUtilisateur> utilisateurList);
    CUtilisateur getUtilisateurByEmail(String mail);
    boolean checkPassword(String mdp, int id);
}
