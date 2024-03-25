package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bo.CRetrait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RetraitDAOImpl implements RetraitDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public String CreateRetrait(CRetrait Retrait) {
        Logger.log("Trace_ENI.log","CreateRetrait : "+ Retrait);
        String insertRetraitQuery = "INSERT INTO RETRAITS (rue,code_postal,ville) VALUES (?,?,?)";
        try{
        jdbcTemplate.update(insertRetraitQuery, Retrait.getRue(), Retrait.getCode_postal(), Retrait.getVille());
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
    public String ModifyRetrait(CRetrait Retrait) {
        Logger.log("Trace_ENI.log","ModifyRetrait : "+ Retrait);
        String updateRetraitQuery = "UPDATE (rue = ?,code_postal = ?,ville = ?) SET RETRAITS WHERE no_retrait = ?";
        try{
        jdbcTemplate.update(updateRetraitQuery, Retrait.getRue(), Retrait.getCode_postal(), Retrait.getVille(), Retrait.getNoRetrait());
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
    public String DeleteRetrait(int id) {
        Logger.log("Trace_ENI.log","DeleteRetrait : "+ id);
        String deleteRetraitQuery = "DELETE FROM  RETRAITS WHERE no_retrait = ?";
        try{
        jdbcTemplate.update(deleteRetraitQuery, id);
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
    public CRetrait SearchRetrait(int id) {
        Logger.log("Trace_ENI.log","SearchRetrait : "+ id);
        String sql = "SELECT no_retrait, rue, code_postal, ville FROM RETRAITS WHERE no_retrait = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new CRetrait(rs.getInt("no_retrait"), rs.getString("rue"),rs.getInt("code_postal"),rs.getString("ville")));
    }

    @Override
    public CRetrait SearchRetraitByArticleID(int id) {
        Logger.log("Trace_ENI.log","SearchRetraitByArticleID : "+ id);
        String sql = "SELECT  RETRAITS.* FROM RETRAITS INNER JOIN\n" +
                " ARTICLES_VENDUS ON RETRAITS.no_article = ARTICLES_VENDUS.no_article WHERE RETRAITS.no_article = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                new CRetrait(rs.getInt("no_retrait"), rs.getString("rue"),rs.getInt("code_postal"),rs.getString("ville")));

    }
}
