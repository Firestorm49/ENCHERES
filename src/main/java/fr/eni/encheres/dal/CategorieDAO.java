package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CCategorie;

import java.util.List;

public interface CategorieDAO {

    void CreateCategorie(CCategorie categorie);
    void ModifyCategorie(CCategorie categorie);
    void DeleteCategorie(int id);
    CCategorie SearchCategorie(int id);
    List<CCategorie> ListCategorie();
}
