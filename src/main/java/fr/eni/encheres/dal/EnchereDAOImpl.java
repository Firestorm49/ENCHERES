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
        String insertArticleQuery = "INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, photo_url,etat_article) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        jdbcTemplate.update(insertArticleQuery, article.getNomArticle(), article.getDescription(), article.getDateDebutEncheres(), article.getDateFinEncheres(), article.getMiseAPrix(), article.getPrixVente(), article.getVendeur().getNoUtilisateur(), article.getCategorie().getNoCategorie(), article.getPhoto(), article.getEtatVente());

    }

    @Override
    public List<CUtilisateur> listEncheresDeconnecte() {

        String sql = "SELECT        UTILISATEURS.nom, UTILISATEURS.prenom, RETRAITS.rue, RETRAITS.code_postal, RETRAITS.ville, CATEGORIES.libelle, ARTICLES_VENDUS.nom_article, ARTICLES_VENDUS.description, \n" +
                "                         ARTICLES_VENDUS.date_debut_encheres, ARTICLES_VENDUS.date_fin_encheres, ARTICLES_VENDUS.prix_initial, ARTICLES_VENDUS.no_utilisateur, ARTICLES_VENDUS.etat_article\n" +
                "FROM            ARTICLES_VENDUS INNER JOIN\n" +
                "                         ENCHERES ON ARTICLES_VENDUS.no_article = ENCHERES.no_article INNER JOIN\n" +
                "                         CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN\n" +
                "                         RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article INNER JOIN\n" +
                "                         UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur WHERE etat_article=1";
        return  Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{}, CUtilisateur.class)));

    }

    @Override
    public List<CEnchere> listEncheresConnecte() {

        return null;
    }

    @Override
    public void ProposeEnchere(CEnchere enchere) {
        String updateCreditsQuery = "INSERT INTO ENCHERES (montant_enchere,date_enchere) VALUES (?,?) WHERE no_encheres=?";
        jdbcTemplate.update(updateCreditsQuery, enchere.getNoEnchere());
    }

    @Override
    public CEnchere remporterVente(CArticleVendu vente) {
        String updateCreditsQuery = "UPDATE ARTICLES_VENDUS SET prix_vente=?,etat_article= ? WHERE no_article=?";
        jdbcTemplate.update(updateCreditsQuery, vente.getPrixVente(),vente.getEtatVente(),vente.getNoArticle());

        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE ARTICLES_VENDUS.no_article=?";
        return (CEnchere) Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{vente.getNoArticle()}, CUtilisateur.class));
    }

    @Override
    public CEnchere afficherDetailEnchere(int enchereId) {
        String sql = "SELECT  UTILISATEURS.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE no_encheres=?";
        return (CEnchere) Collections.singletonList(jdbcTemplate.queryForObject(sql, new Object[]{enchereId}, CUtilisateur.class));
    }

    @Override
    public void modifierVente(CArticleVendu vente) {
        String insertArticleQuery = "UPDATE (nom_article= ?, description= ?, date_debut_encheres= ?, date_fin_encheres= ?, prix_initial= ?, prix_vente= ?, no_utilisateur= ?, no_categorie= ?, photo_url= ?,etat_article= ?)  SET ARTICLES_VENDUS WHERE no_article=?";
        jdbcTemplate.update(insertArticleQuery, vente.getNomArticle(), vente.getDescription(), vente.getDateDebutEncheres(), vente.getDateFinEncheres(), vente.getMiseAPrix(), vente.getPrixVente(), vente.getVendeur().getNoUtilisateur(), vente.getCategorie().getNoCategorie(), vente.getPhoto(), vente.getEtatVente(), vente.getNoArticle());

    }

    @Override
    public void annulerVente(CArticleVendu vente) {
        /* Pour l'identifiant de type d'etat d'une vente, se referencer au fichier README*/
        String insertArticleQuery = "UPDATE ( etat_article= ?)  SET ARTICLES_VENDUS WHERE no_article=?";
        jdbcTemplate.update(insertArticleQuery, vente.getEtatVente(), vente.getNoArticle());
    }

    @Override
    public void ajouterPhotoVente(CArticleVendu vente) {
        String insertArticleQuery = "UPDATE ( photo_url= ?)  SET ARTICLES_VENDUS WHERE no_article=?";
        jdbcTemplate.update(insertArticleQuery, vente.getPhoto(), vente.getNoArticle());

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
