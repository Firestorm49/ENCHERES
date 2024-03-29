package fr.eni.encheres.bll.Impl;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.CCategorie;
import fr.eni.encheres.dal.CategorieDAO;
import fr.eni.encheres.dal.EnchereDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorieServiceImpl implements CategorieService {

    private CategorieDAO categorieDAO;


    public CategorieServiceImpl(CategorieDAO categorieDAO) {
        this.categorieDAO = categorieDAO;
    }
    @Override
    public String CreateCategorie(CCategorie categorie) {
        return categorieDAO.CreateCategorie(categorie);
    }

    @Override
    public String ModifyCategorie(CCategorie categorie) {
        return categorieDAO.ModifyCategorie(categorie);
    }

    @Override
    public String DeleteCategorie(int id) {
        return categorieDAO.DeleteCategorie(id);
    }

    @Override
    public CCategorie SearchCategorie(int id) {
        return categorieDAO.SearchCategorie(id);
    }

    @Override
    public List<CCategorie> ListCategorie() {
        return categorieDAO.ListCategorie();
    }
}
