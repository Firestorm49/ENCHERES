package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CCategorie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CategorieDAOImpl implements CategorieDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void CreateCategorie(CCategorie categorie) {

        String insertCategorieQuery = "INSERT INTO CATEGORIES (libelle) VALUES (?)";
        jdbcTemplate.update(insertCategorieQuery, categorie.getLibelle());
    }

    @Override
    public void ModifyCategorie(CCategorie categorie) {
        String updateCategorieQuery = "UPDATE CATEGORIES SET libelle=? WHERE no_categorie=?";
        jdbcTemplate.update(updateCategorieQuery, categorie.getLibelle(), categorie.getNoCategorie());
    }


    @Override
    public void DeleteCategorie(int id) {
        String deleteCategorieQuery = "DELETE FROM CATEGORIES WHERE no_categorie=?";
        jdbcTemplate.update(deleteCategorieQuery, id);
    }


    @Override
    public CCategorie SearchCategorie(int id) {
        String sql = "SELECT no_categorie, libelle FROM CATEGORIES WHERE no_categorie=?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new CCategorie(rs.getInt("no_categorie"), rs.getString("libelle")));
    }

    @Override
    public List<CCategorie> ListCategorie() {
        String sql = "SELECT no_categorie, libelle FROM CATEGORIES";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new CCategorie(rs.getInt("no_categorie"), rs.getString("libelle")));
    }

}
