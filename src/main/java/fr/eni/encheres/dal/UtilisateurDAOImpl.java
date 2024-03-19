package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void Subscribe(CUtilisateur utilisateur) {
        String insertUtilisateurQuery = "INSERT INTO utilisateur (pseudo, nom, prenom, email, telephone, rue, code_postal, ville, mot_de_passe, credit, administrateur, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertUtilisateurQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), utilisateur.getMotdepasse(), utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive());
    }

    @Override
    public CUtilisateur ViewProfil(int id) {
        String sql = "SELECT * FROM utilisateur WHERE no_utilisateur=?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, CUtilisateur.class );
    }

    @Override
    public void ModifyProfil(CUtilisateur utilisateur) {
        String updateProfilQuery = "UPDATE utilisateur SET pseudo=?, nom=?, prenom=?, email=?, telephone=?, rue=?, code_postal=?, ville=?, mot_de_passe=?, credit=?, administrateur=?, active=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateProfilQuery, utilisateur.getPseudo(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getTelephone(), utilisateur.getRue(), utilisateur.getCodePostal(), utilisateur.getVille(), utilisateur.getMotdepasse(), utilisateur.getCredit(), utilisateur.isAdministrateur(), utilisateur.isActive(), utilisateur.getNoUtilisateur());
    }

    @Override
    public void DeleteProfil(int id) {
        String deleteProfilQuery = "DELETE FROM utilisateur WHERE no_utilisateur=?";
        jdbcTemplate.update(deleteProfilQuery, id);
    }

    @Override
    public void DeleteProfil(CUtilisateur utilisateur) {
        DeleteProfil(utilisateur.getNoUtilisateur());
    }

    @Override
    public void DesactiveProfil(CUtilisateur utilisateur) {
        String desactivateProfilQuery = "UPDATE utilisateur SET active=0 WHERE no_utilisateur=?";
        jdbcTemplate.update(desactivateProfilQuery, utilisateur.getNoUtilisateur());
    }

    @Override
    public void DeleteMultiProfil(List<CUtilisateur> utilisateurList) {
        for (CUtilisateur utilisateur : utilisateurList) {
            DeleteProfil(utilisateur);
        }
    }

    @Override
    public CUtilisateur getUtilisateurByEmail(String mail) {
        String sql = "SELECT * FROM utilisateur WHERE email=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{mail}, CUtilisateur.class );
    }
}
