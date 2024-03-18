package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CUtilisateur;

import java.util.List;

public interface UtilisateurDAO {
    void Subscribe(CUtilisateur utilisateur);
    CUtilisateur ViewProfil(int id);
    void DeleteProfil(int id);
    void ModifyProfil(CUtilisateur utilisateur);
    void DeleteProfil(CUtilisateur utilisateur);
    void DesactiveProfil(CUtilisateur utilisateur);
    void DeleteMultiProfil(List<CUtilisateur> utilisateurList);
}
