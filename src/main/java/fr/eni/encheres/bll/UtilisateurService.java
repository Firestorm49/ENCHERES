package fr.eni.encheres.bll;

import java.util.List;
import fr.eni.encheres.bo.*;
public interface UtilisateurService {
    String Inscription(CUtilisateur utilisateur);
    CUtilisateur ViewProfil(int id);
    String DeleteProfil(int id);
    String ModifyProfil(CUtilisateur utilisateur);
    String DeleteProfil(CUtilisateur utilisateur);
    String DesactiveProfil(CUtilisateur utilisateur);
    String DeleteMultiProfil(List<CUtilisateur> utilisateurList);
    CUtilisateur getUtilisateurByEmail(String mail);
    boolean checkUser(String mail);
    CUtilisateur viewAcheteurByArticleID(int id);
    String ModifyRoleUtilisateur(int id, int isAdministrateur);
    List<CUtilisateur> ViewAllUtilisateurs();
    String ActiveProfil(CUtilisateur utilisateur);
    boolean verifPassword(String mdp, CUtilisateur utilisateur);
    String achatCredits(CUtilisateur utilisateur, int creditsAmount);
}
