package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.Cryptage;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        Logger.log("Trace_ENI.log","ViewAllUtilisateurs");
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
    public void ModifyRoleUtilisateur(int id, int isAdministrateur) {
        Logger.log("Trace_ENI.log","ModifyRoleUtilisateur : " + id);
        String updateProfilQuery = "UPDATE UTILISATEURS SET  administrateur=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateProfilQuery, isAdministrateur, id);
    }

    @Override
    public void DeleteProfil(int id) {
        Logger.log("Trace_ENI.log", "DeleteProfil : " + id);
        DesactiveProfil(ViewProfil(id));
        // Supprimer l'utilisateur de la table des profils
        String deleteProfilQuery = "DELETE FROM Utilisateurs WHERE no_utilisateur = ?";
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

        String SelectRowsQuery = "SELECT COUNT(no_article) FROM ARTICLES_VENDUS WHERE no_utilisateur =? AND etat_article < 2";
        Integer Nb_RowsUsers = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{utilisateur.getNoUtilisateur()}, Integer.class);

        if(Nb_RowsUsers > 0) {
            List<Integer> articleIDs = new ArrayList<>(Nb_RowsUsers);
            String SelectarticleQuery = "SELECT no_article FROM ARTICLES_VENDUS WHERE no_utilisateur =? AND etat_article < 2";
            articleIDs = jdbcTemplate.queryForList(SelectarticleQuery, new Object[]{utilisateur.getNoUtilisateur()}, Integer.class);

            for (int i = 0; i < Nb_RowsUsers; i++) {
                String sql = "SELECT no_utilisateur FROM ENCHERES WHERE no_article = ? AND montant_enchere = (SELECT MAX(montant_enchere) FROM ENCHERES  WHERE no_article = ?)";
                Integer UserOffre = jdbcTemplate.queryForObject(sql, new Object[]{articleIDs.get(i),articleIDs.get(i)}, Integer.class);
                    String updatePrevCreditsQuery = "UPDATE UTILISATEURS SET credit=(credit + (SELECT montant_enchere FROM ENCHERES WHERE no_article = ? AND no_utilisateur=? ))  WHERE no_utilisateur=?";
                    jdbcTemplate.update(updatePrevCreditsQuery, articleIDs.get(i), UserOffre, UserOffre);

                    String insertArticleQuery = "UPDATE ARTICLES_VENDUS SET etat_article= 3  WHERE no_article=?";
                    jdbcTemplate.update(insertArticleQuery, articleIDs.get(i));
            }
        }

        String enchereEnCoursQuery = "SELECT ENCHERES.no_article FROM ENCHERES INNER JOIN ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article \n" +
                "WHERE ENCHERES.no_utilisateur = ? AND ARTICLES_VENDUS.etat_article = 1 \n" +
                "GROUP BY ENCHERES.no_article";

        List<Integer> articlesAEnlever = jdbcTemplate.queryForList(enchereEnCoursQuery, Integer.class, utilisateur.getNoUtilisateur());

        // Pour chaque article, récupérer le deuxième montant le plus élevé et retirer l'utilisateur de la table des enchères
        for (Integer noArticle : articlesAEnlever) {
            String UsersMontantQuery = "SELECT montant_enchere FROM ENCHERES WHERE no_utilisateur = ? AND no_article =?";

            Integer UsersMontant = jdbcTemplate.queryForObject(UsersMontantQuery, Integer.class,utilisateur.getNoUtilisateur(), noArticle);

            String MontantMaxQuery = "SELECT MAX(montant_enchere) FROM ENCHERES WHERE no_article =?";

            Integer MontantMax = jdbcTemplate.queryForObject(MontantMaxQuery, Integer.class, noArticle);

            if(UsersMontant == MontantMax){

                String deleteEnchereQuery = "DELETE FROM Encheres WHERE no_utilisateur = ? AND no_article = ?";
                jdbcTemplate.update(deleteEnchereQuery, utilisateur.getNoUtilisateur(), noArticle);

                String MontantSecondQuery = "SELECT MAX(montant_enchere) FROM ENCHERES WHERE no_article =?";

                Integer MontantSecond = jdbcTemplate.queryForObject(MontantSecondQuery, Integer.class, noArticle);

                String UserSecondQuery = "SELECT no_utilisateur FROM ENCHERES WHERE no_article =? AND montant_enchere = ?";

                Integer UserSecond = jdbcTemplate.queryForObject(UserSecondQuery, Integer.class, noArticle, MontantSecond);

                String sql = "SELECT credit FROM UTILISATEURS WHERE no_utilisateur=?";
                Integer credit = jdbcTemplate.queryForObject(sql, new Object[]{UserSecond}, Integer.class);

                if (credit != null && MontantSecond < credit) {
                    String DesactivateProfilQuery = "UPDATE UTILISATEURS SET credit= credit - ? WHERE no_utilisateur=?";
                    jdbcTemplate.update(DesactivateProfilQuery, MontantSecond,UserSecond);
                }
            }
        }
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
    @Override
    public void achatCredits(CUtilisateur utilisateur, int creditsAmount) {
        Logger.log("Trace_ENI.log","achatCredits : " + utilisateur + " " + creditsAmount);
        String updateCreditsQuery = "UPDATE UTILISATEURS SET credit= credit + ? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateCreditsQuery, creditsAmount, utilisateur.getNoUtilisateur());

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
            a.setAdministrateur(rs.getInt("administrateur"));
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
            utilisateur.setAdministrateur(rs.getInt("administrateur"));
            utilisateur.setActive(rs.getBoolean("active"));

            return utilisateur;
        }
    }
}
