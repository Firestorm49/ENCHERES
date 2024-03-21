package fr.eni.encheres.bll;

import java.util.List;
import fr.eni.encheres.bo.*;
public interface UtilisateurService {
    void Inscription(CUtilisateur utilisateur);
    CUtilisateur ViewProfil(int id);
    void DeleteProfil(int id);
    void ModifyProfil(CUtilisateur utilisateur);
    void DeleteProfil(CUtilisateur utilisateur);
    void DesactiveProfil(CUtilisateur utilisateur);
    void DeleteMultiProfil(List<CUtilisateur> utilisateurList);
    CUtilisateur getUtilisateurByEmail(String mail);
    boolean checkUser(String mail);
    CUtilisateur viewAcheteurByArticleID(int id);
    void ModifyRoleUtilisateur(int id, boolean isAdministrateur);
    List<CUtilisateur> ViewAllUtilisateurs();
    void ActiveProfil(CUtilisateur utilisateur);
    boolean verifPassword(String mdp, CUtilisateur utilisateur);
}
