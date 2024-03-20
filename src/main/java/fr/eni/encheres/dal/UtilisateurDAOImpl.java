package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void Subscribe(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","Subscribe : " + utilisateur);
        String mdpCrypte = "{bcrypt}"+BCrypt.hashpw(utilisateur.getMotdepasse(), BCrypt.gensalt());
        String insertUtilisateurQuery = "INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUtilisateurQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), utilisateur.getMotdepasse(), utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive());
    }

    @Override
    public CUtilisateur viewAcheteurByArticleID(int id) {
        Logger.log("Trace_ENI.log","viewAcheteurByArticleID : " + id);
        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE no_article = ? AND prix_vente = montant_enchere";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UtilisateurRowMapper());
    }

    @Override
    public CUtilisateur ViewProfil(int id) {
        Logger.log("Trace_ENI.log","ViewProfil : " + id);
        String sql = "SELECT * FROM UTILISATEURS WHERE no_utilisateur= ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UtilisateurRowMapper() );
    }
    @Override
    public List<CUtilisateur> ViewAllUtilisateurs() {
        Logger.log("Trace_ENI.log","ViewAllUtilisateurs  ");
        String sql = "SELECT * FROM UTILISATEURS";
        return Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{}, new UtilisateurRowMapper()));
    }
    @Override
    public void ModifyProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","ModifyProfil : " + utilisateur);
        String updateProfilQuery = "UPDATE UTILISATEURS SET pseudo=?, nom=?, prenom=?, email=?, telephone=?, rue=?, code_postal=?, ville=?, mot_de_passe=?, credit=?, administrateur=?, active=? WHERE no_utilisateur=?";
        String mdpCrypte = "{bcrypt}"+BCrypt.hashpw(utilisateur.getMotdepasse(), BCrypt.gensalt());
        jdbcTemplate.update(updateProfilQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), mdpCrypte, utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive(), utilisateur.getNoUtilisateur());
    }
    @Override
    public void ModifyRoleUtilisateur(int id, int isAdministrateur) {
        Logger.log("Trace_ENI.log","ModifyRoleUtilisateur : " + id);
        String updateProfilQuery = "UPDATE UTILISATEURS SET  administrateur=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateProfilQuery, isAdministrateur, id);
    }

    @Override
    public void DeleteProfil(int id) {
        Logger.log("Trace_ENI.log","DeleteProfil : " + id);
        String deleteProfilQuery = "DELETE FROM UTILISATEURS WHERE no_utilisateur=?";
        jdbcTemplate.update(deleteProfilQuery, id);
    }

    @Override
    public void DeleteProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","DeleteProfil : " + utilisateur);
        DeleteProfil(utilisateur.getNoUtilisateur());
    }

    @Override
    public void DesactiveProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","DesactiveProfil : " + utilisateur);
        String desactivateProfilQuery = "UPDATE UTILISATEURS SET active=0 WHERE no_utilisateur=?";
        jdbcTemplate.update(desactivateProfilQuery, utilisateur.getNoUtilisateur());
    }
    @Override
    public void ActiveProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","ActiveProfil : " + utilisateur);
        String ActivateProfilQuery = "UPDATE UTILISATEURS SET active=1 WHERE no_utilisateur=?";
        jdbcTemplate.update(ActivateProfilQuery, utilisateur.getNoUtilisateur());
    }

    @Override
    public void DeleteMultiProfil(List<CUtilisateur> utilisateurList) {
        Logger.log("Trace_ENI.log","DeleteMultiProfil : " + utilisateurList);
        for (CUtilisateur utilisateur : utilisateurList) {
            DeleteProfil(utilisateur);
        }
    }

    @Override
    public CUtilisateur getUtilisateurByEmail(String mail) {
        Logger.log("Trace_ENI.log","getUtilisateurByEmail : " + mail);
        String sql = "SELECT * FROM UTILISATEURS WHERE email=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{mail},  new UtilisateurRowMapper() );
    }

    public class UtilisateurRowMapper implements RowMapper<CUtilisateur> {

        @Override
        public CUtilisateur mapRow(ResultSet rs, int rowNum) throws SQLException {
            CUtilisateur a = new CUtilisateur();

            a.setNoUtilisateur(rs.getInt("no_utilisateur"));
            a.setNom(rs.getString("nom"));
            a.setPrenom(rs.getString("prenom"));
            a.setPseudo(rs.getString("pseudo"));
            a.setEmail(rs.getString("email"));
            a.setTelephone(rs.getString("telephone"));
            a.setCodePostal(rs.getInt("code_postal"));
            a.setRue(rs.getString("rue"));
            a.setVille(rs.getString("ville"));
            a.setCredit(rs.getInt("credit"));
            a.setAdministrateur(rs.getBoolean("administrateur"));
            a.setActive(rs.getBoolean("active"));

            return a;
        }
    }
}
