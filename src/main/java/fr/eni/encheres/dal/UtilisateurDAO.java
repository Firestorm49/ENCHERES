package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CUtilisateur;

import java.util.List;

public interface UtilisateurDAO {
    String Subscribe(CUtilisateur utilisateur);
    CUtilisateur viewAcheteurByArticleID(int id);
    CUtilisateur ViewProfil(int id);
    String ModifyRoleUtilisateur(int id, int isAdministrateur);
    String DeleteProfil(int id);
    List<CUtilisateur> ViewAllUtilisateurs();
    String ModifyProfil(CUtilisateur utilisateur);
    String DeleteProfil(CUtilisateur utilisateur);
    String DesactiveProfil(CUtilisateur utilisateur);
    String ActiveProfil(CUtilisateur utilisateur);
    String DeleteMultiProfil(List<CUtilisateur> utilisateurList);
    CUtilisateur getUtilisateurByEmail(String mail);
    boolean checkUser(String mail);
    boolean checkPassword(String mdp, int id);
    String achatCredits(CUtilisateur utilisateur, int creditsAmount);
}
