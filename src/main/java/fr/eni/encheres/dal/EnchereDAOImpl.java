package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
@Repository
public class EnchereDAOImpl implements EnchereDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void SoldArticle(CArticleVendu article) {
        String insertArticleQuery = "INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, photo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertArticleQuery, article.getNomArticle(), article.getDescription(), article.getDateDebutEncheres(), article.getDateFinEncheres(), article.getMiseAPrix(), article.getPrixVente(), article.getVendeur().getNoUtilisateur(), article.getCategorie().getNoCategorie(), article.getPhoto());

    }

    @Override
    public List<CEnchere> listEncheresDeconnecte() {
        return null;
    }

    @Override
    public List<CEnchere> listEncheresConnecte() {
        return null;
    }

    @Override
    public void ProposeEnchere(CEnchere enchere) {

    }

    @Override
    public CEnchere remporterVente(CArticleVendu vente) {
        String updateCreditsQuery = "UPDATE ARTICLES_VENDUS SET prix_vente=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateCreditsQuery, vente.getNomArticle());

        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE ARTICLES_VENDUS.no_article=?";
        return (CEnchere) Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{vente.getNoArticle()}, CUtilisateur.class));
    }

    @Override
    public CEnchere afficherDetailEnchere(int enchereId) {
        return null;
    }

    @Override
    public void modifierVente(CArticleVendu vente) {

    }

    @Override
    public void annulerVente(CArticleVendu vente) {

    }

    @Override
    public void ajouterPhotoVente(CArticleVendu vente) {

    }

    @Override
    public List<CEnchere> pagination(int pageNumber, int pageSize) {
        return null;
    }

    @Override
    public List<CUtilisateur> voirEncherisseurs(CArticleVendu vente) {
        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE ARTICLES_VENDUS.no_article=?";
        return Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{vente.getNoArticle()}, CUtilisateur.class));
    }

    @Override
    public void achatCredits(CUtilisateur utilisateur, int creditsAmount) {
        String updateCreditsQuery = "UPDATE utilisateur SET credit=? WHERE no_utilisateur=?";
        jdbcTemplate.update(updateCreditsQuery, creditsAmount, utilisateur.getNoUtilisateur());

    }

    public class EnchereRowMapper implements RowMapper<CEnchere> {

        @Override
        public CEnchere mapRow(ResultSet rs, int rowNum) throws SQLException {
            CEnchere a = new CEnchere();

            CUtilisateur utilisateur = new CUtilisateur();
            a.setNoEnchere(rs.getInt("iKey_avis"));
            a.setDateEnchere(rs.getString("s_Commentaire"));
            a.setMontant_enchere(rs.getInt("s_Commentaire"));
            utilisateur.setNom(rs.getString("s_Nom"));
            utilisateur.setPrenom(rs.getString("s_Prenom"));
            a.setUtilisateur(utilisateur);

            return a;
        }
    }
}
