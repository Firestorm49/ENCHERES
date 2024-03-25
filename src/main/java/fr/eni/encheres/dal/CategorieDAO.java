package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CCategorie;

import java.util.List;

public interface CategorieDAO {

    String CreateCategorie(CCategorie categorie);
    String ModifyCategorie(CCategorie categorie);
    String DeleteCategorie(int id);
    CCategorie SearchCategorie(int id);
    List<CCategorie> ListCategorie();
}
