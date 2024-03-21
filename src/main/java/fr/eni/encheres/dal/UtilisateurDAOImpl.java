package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.Cryptage;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    Cryptage cryptage = new Cryptage();


    @Override
    public void Subscribe(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","Subscribe : " + utilisateur);
        String mdpCrypte = "{bcrypt}"+cryptage.cryptageBCrypt(utilisateur.getMotdepasse());
        String insertUtilisateurQuery = "INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUtilisateurQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), mdpCrypte, utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive());
    }

    @Override
    public CUtilisateur viewAcheteurByArticleID(int id) {
        Logger.log("Trace_ENI.log","viewAcheteurByArticleID : " + id);
        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE ENCHERES.no_article = ? AND prix_vente = montant_enchere";
        CUtilisateur users = null;
        users = jdbcTemplate.queryForObject(sql, new Object[]{id}, new UtilisateurRowMapper());
        return users;
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
        return jdbcTemplate.query(sql, new UtilisateurListRowMapper());
    }
    @Override
    public void ModifyProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","ModifyProfil : " + utilisateur);
        String updateProfilQuery = "UPDATE UTILISATEURS SET pseudo=?, nom=?, prenom=?, email=?, telephone=?, rue=?, code_postal=?, ville=?, mot_de_passe=?, credit=?, administrateur=?, active=? WHERE no_utilisateur=?";
        String mdpCrypte = "{bcrypt}"+cryptage.cryptageBCrypt(utilisateur.getMotdepasse());
        jdbcTemplate.update(updateProfilQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), mdpCrypte, utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive(), utilisateur.getNoUtilisateur());
    }
    @Override
    public void ModifyRoleUtilisateur(int id, boolean isAdministrateur) {
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

    @Override
    public boolean checkUser(String mail) {
        Logger.log("Trace_ENI.log","checkUser" + mail);
        String checkUserQuery = "SELECT COUNT(*) FROM UTILISATEURS WHERE email=?";
        int count = jdbcTemplate.queryForObject(checkUserQuery, new Object[]{mail}, Integer.class);
        return count > 0;
    }

    @Override
    public boolean checkPassword(String mdp, int id) {
        String mdpBaseQuery = "SELECT mot_de_passe FROM UTILISATEURS WHERE no_utilisateur=?";
        String mdpBase = jdbcTemplate.queryForObject(mdpBaseQuery, new Object[]{id}, String.class);
        mdpBase = mdpBase.replace("{bcrypt}", "");
        return cryptage.checkPassword(mdp, mdpBase);
    }

    public class UtilisateurRowMapper implements RowMapper<CUtilisateur>
    {

        @Override
        public CUtilisateur mapRow(ResultSet rs, int rowNum) throws SQLException
        {
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

    public class UtilisateurListRowMapper implements RowMapper<CUtilisateur> {
        @Override
        public CUtilisateur mapRow(ResultSet rs, int rowNum) throws SQLException {
            CUtilisateur utilisateur = new CUtilisateur();

            utilisateur.setNoUtilisateur(rs.getInt("no_utilisateur"));
            utilisateur.setNom(rs.getString("nom"));
            utilisateur.setPrenom(rs.getString("prenom"));
            utilisateur.setPseudo(rs.getString("pseudo"));
            utilisateur.setEmail(rs.getString("email"));
            utilisateur.setTelephone(rs.getString("telephone"));
            utilisateur.setCodePostal(rs.getInt("code_postal"));
            utilisateur.setRue(rs.getString("rue"));
            utilisateur.setVille(rs.getString("ville"));
            utilisateur.setCredit(rs.getInt("credit"));
            utilisateur.setAdministrateur(rs.getBoolean("administrateur"));
            utilisateur.setActive(rs.getBoolean("active"));

            return utilisateur;
        }
    }
}
