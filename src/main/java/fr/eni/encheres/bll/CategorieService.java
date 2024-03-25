package fr.eni.encheres.bll;

import fr.eni.encheres.bo.CCategorie;

import java.util.List;

public interface CategorieService {
    String CreateCategorie(CCategorie categorie);
    String ModifyCategorie(CCategorie categorie);
    String DeleteCategorie(int id);
    CCategorie SearchCategorie(int id);
    List<CCategorie> ListCategorie();
}
