package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bo.CRetrait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RetraitDAOImpl implements RetraitDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void CreateRetrait(CRetrait Retrait) {
        Logger.log("Trace_ENI.log","CreateRetrait : "+ Retrait);
        String insertRetraitQuery = "INSERT INTO RETRAITS (rue,code_postal,ville) VALUES (?,?,?)";
        jdbcTemplate.update(insertRetraitQuery, Retrait.getRue(), Retrait.getCode_postal(), Retrait.getVille());
    }

    @Override
    public void ModifyRetrait(CRetrait Retrait) {
        Logger.log("Trace_ENI.log","ModifyRetrait : "+ Retrait);
        String updateRetraitQuery = "UPDATE (rue = ?,code_postal = ?,ville = ?) SET RETRAITS WHERE no_retrait = ?";
        jdbcTemplate.update(updateRetraitQuery, Retrait.getRue(), Retrait.getCode_postal(), Retrait.getVille(), Retrait.getNoRetrait());
    }

    @Override
    public void DeleteRetrait(int id) {
        Logger.log("Trace_ENI.log","DeleteRetrait : "+ id);
        String deleteRetraitQuery = "DELETE FROM  RETRAITS WHERE no_retrait = ?";
        jdbcTemplate.update(deleteRetraitQuery, id);
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
