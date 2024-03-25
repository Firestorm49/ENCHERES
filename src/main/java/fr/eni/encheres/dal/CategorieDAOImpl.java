package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bo.CCategorie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class CategorieDAOImpl implements CategorieDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String CreateCategorie(CCategorie categorie) {
        Logger.log("Trace_ENI.log","CreateCategorie : "+ categorie.getLibelle());
        String insertCategorieQuery = "INSERT INTO CATEGORIES (libelle) VALUES (?)";
        try {
            jdbcTemplate.update(insertCategorieQuery, categorie.getLibelle());
            return ErrorCode.NO_ERROR; // pas d'erreur
        } catch (DuplicateKeyException e) {
            return ErrorCode.DUPLICATE_KEY; // Clé en double
        } catch (DataIntegrityViolationException e) {
            return ErrorCode.CONSTRAINT_VIOLATION; // Violation de contrainte
        } catch (DeadlockLoserDataAccessException e) {
            return ErrorCode.DEADLOCK_DETECTED; // Deadlock détecté
        } catch (BadSqlGrammarException e) {
            return ErrorCode.INCORRECT_COLUMN_TYPE; // Type de colonne incorrect
        } catch (DataAccessException e) {
            return ErrorCode.SQL_ERROR; // Erreur SQL non traitée
        }
    }

    @Override
    public String ModifyCategorie(CCategorie categorie) {
        Logger.log("Trace_ENI.log","ModifyCategorie : "+ categorie.getLibelle() + " " +  categorie.getNoCategorie());
        String updateCategorieQuery = "UPDATE CATEGORIES SET libelle=? WHERE no_categorie=?";
        try{
        jdbcTemplate.update(updateCategorieQuery, categorie.getLibelle(), categorie.getNoCategorie());
            return ErrorCode.NO_ERROR; // pas d'erreur
        } catch (DuplicateKeyException e) {
            return ErrorCode.DUPLICATE_KEY; // Clé en double
        } catch (DataIntegrityViolationException e) {
            return ErrorCode.CONSTRAINT_VIOLATION; // Violation de contrainte
        } catch (DeadlockLoserDataAccessException e) {
            return ErrorCode.DEADLOCK_DETECTED; // Deadlock détecté
        } catch (BadSqlGrammarException e) {
            return ErrorCode.INCORRECT_COLUMN_TYPE; // Type de colonne incorrect
        } catch (DataAccessException e) {
            return ErrorCode.SQL_ERROR; // Erreur SQL non traitée
        }
    }


    @Override
    public String DeleteCategorie(int id) {
        Logger.log("Trace_ENI.log","DeleteCategorie : "+ id);
        try{
        String updateCategorieQuery = "UPDATE ARTICLES_VENDUS SET no_categorie=5 WHERE no_categorie=?";
        jdbcTemplate.update(updateCategorieQuery, id);

        String deleteCategorieQuery = "DELETE FROM CATEGORIES WHERE no_categorie=?";
        jdbcTemplate.update(deleteCategorieQuery, id);
            return ErrorCode.NO_ERROR; // pas d'erreur
        } catch (DuplicateKeyException e) {
            return ErrorCode.DUPLICATE_KEY; // Clé en double
        } catch (DataIntegrityViolationException e) {
            return ErrorCode.CONSTRAINT_VIOLATION; // Violation de contrainte
        } catch (DeadlockLoserDataAccessException e) {
            return ErrorCode.DEADLOCK_DETECTED; // Deadlock détecté
        } catch (BadSqlGrammarException e) {
            return ErrorCode.INCORRECT_COLUMN_TYPE; // Type de colonne incorrect
        } catch (DataAccessException e) {
            return ErrorCode.SQL_ERROR; // Erreur SQL non traitée
        }
    }


    @Override
    public CCategorie SearchCategorie(int id) {
        Logger.log("Trace_ENI.log","SearchCategorie : "+ id);
        String sql = "SELECT no_categorie, libelle FROM CATEGORIES WHERE no_categorie=?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                    new CCategorie(rs.getInt("no_categorie"), rs.getString("libelle")));
    }

    @Override
    public List<CCategorie> ListCategorie() {
        Logger.log("Trace_ENI.log","ListCategorie ");
        String sql = "SELECT no_categorie, libelle FROM CATEGORIES";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new CCategorie(rs.getInt("no_categorie"), rs.getString("libelle")));
    }

}
