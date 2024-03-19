package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void Subscribe(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","Subscribe : " + utilisateur);
        String insertUtilisateurQuery = "INSERT INTO UTILISATEURS (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUtilisateurQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), utilisateur.getMotdepasse(), utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive());
    }

    @Override
    public CUtilisateur ViewProfil(int id) {
        Logger.log("Trace_ENI.log","ViewProfil : " + id);
        String sql = "SELECT * FROM UTILISATEURS WHERE no_utilisateur= ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new UtilisateurRowMapper() );
    }

    @Override
    public void ModifyProfil(CUtilisateur utilisateur) {
        Logger.log("Trace_ENI.log","ModifyProfil : " + utilisateur);
        String updateProfilQuery = "UPDATE UTILISATEURS SET pseudo=?, nom=?, prenom=?, email=?, telephone=?, rue=?, code_postal=?, ville=?, mot_de_passe=?, credit=?, administrateur=?, active=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateProfilQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), utilisateur.getMotdepasse(), utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive(), utilisateur.getNoUtilisateur());
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
