package fr.eni.encheres.dal;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class EnchereDAOImpl implements EnchereDAO {

    private final CategorieDAO categorieDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final RetraitDAO retraitDAO;

    public EnchereDAOImpl(CategorieDAO categorieDAO, UtilisateurDAO utilisateurDAO,RetraitDAO retraitDAO) {
        this.categorieDAO = categorieDAO;
        this.utilisateurDAO = utilisateurDAO;
        this.retraitDAO = retraitDAO;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String SoldArticle(CArticleVendu article) {
        try{
        String insertArticleQuery = "INSERT INTO ARTICLES_VENDUS (nom_article, description, date_debut_encheres, date_fin_encheres, prix_initial, prix_vente, no_utilisateur, no_categorie, photo_url, etat_article) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected = jdbcTemplate.update(insertArticleQuery, article.getNomArticle(), article.getDescription(), article.getDateDebutEncheres(), article.getDateFinEncheres(), article.getMiseAPrix(), article.getPrixVente(), article.getVendeur().getNoUtilisateur(), article.getCategorie().getNoCategorie(), article.getPhoto(), article.getEtatVente());

        if (rowsAffected > 0) {
            String sql = "SELECT MAX(no_article) FROM ARTICLES_VENDUS WHERE nom_article = ?";
            Integer IdArticle = jdbcTemplate.queryForObject(sql, new Object[]{article.getNomArticle()}, Integer.class);

            if(article.getRetrait() == null){
                String insertRetraitQuery = "INSERT INTO RETRAITS (no_article, rue, code_postal, ville) VALUES (?,?,?,?)";
                jdbcTemplate.update(insertRetraitQuery, IdArticle, article.getVendeur().getRue(), article.getVendeur().getCodePostal(), article.getVendeur().getVille());

            }
            else{
                String insertRetraitQuery = "INSERT INTO RETRAITS (no_article, rue, code_postal, ville) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertRetraitQuery, IdArticle, article.getRetrait().getRue(), article.getRetrait().getCode_postal(), article.getRetrait().getVille());
            }
          }
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
    public CArticleVendu viewArticle(int id) {
        Logger.log("Trace_ENI.log","viewArticle : " + id);
        String sql = "SELECT UTILISATEURS.no_utilisateur, CATEGORIES.no_categorie, ARTICLES_VENDUS.*, RETRAITS.*\n" +
                "FROM  ARTICLES_VENDUS INNER JOIN CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN\n" +
                "UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur INNER JOIN\n" +
                "RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article\n" +
                "WHERE (ARTICLES_VENDUS.no_article = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ArticleVenduRowMapper());
    }

    @Override
    public List<CArticleVendu> listEncheresDeconnecte() {
        Logger.log("Trace_ENI.log","listEncheresDeconnecte : ");
        String sql = "SELECT UTILISATEURS.no_utilisateur, CATEGORIES.no_categorie, ARTICLES_VENDUS.*, RETRAITS.* \n" +
                " FROM  ARTICLES_VENDUS INNER JOIN CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN \n" +
                " UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur INNER JOIN \n" +
                "  RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article WHERE etat_article = 1";
        return  jdbcTemplate.query(sql, new ArticleVenduRowMapper());

    }
    @Override
    public List<CArticleVendu> listerEncheresDeconnecteByFilters(String nomArticle, int categorie, int pageNumber, int pageSize) {
        Logger.log("Trace_ENI.log","listerEncheresDeconnecteByFilters : " + pageNumber + " " + pageSize);
        int min = 0;
        int max = 0;
        if(pageNumber > 1) {
             min = (pageNumber * pageSize) -1;
             max = min + pageSize;
        }
        else{
            min = 1;
            max = min + pageSize;
        }

        String Filter = "WHERE";
        List<Object> params = new ArrayList<>();
        boolean isAND = false;
        if (nomArticle != null && nomArticle.trim() != "") {
            Filter += " nom_article LIKE '%' + ? + '%' ";
            isAND = true;
            params.add(nomArticle);
        }
        if (categorie  != 0) {
            if(isAND == true){
                Filter += " AND ";
            }
            Filter += " ARTICLES_VENDUS.no_categorie = ? ";
            params.add(categorie);
        }

        String sql = "SELECT UTILISATEURS.no_utilisateur, CATEGORIES.no_categorie, ARTICLES_VENDUS.*, RETRAITS.* \n" +
                "FROM ARTICLES_VENDUS INNER JOIN CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN \n" +
                "UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur INNER JOIN \n" +
                "RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article " ;
        if(Filter != "WHERE"){
             sql += Filter;
        }

        List<CArticleVendu> allArticles = jdbcTemplate.query(sql, params.toArray(), new ArticleVenduRowMapper());

        List<CArticleVendu> filteredArticles = allArticles.stream()
                .skip(min - 1)
                .limit(max - min)
                .collect(Collectors.toList());

        return filteredArticles;
    }
    @Override
    public List<CArticleVendu> listerEncheresConnecteByFilters(String nomArticle, int categorie,int no_utilisateur, int radio, boolean ventesencours, boolean ventesnoncommencer, boolean ventesterminer, boolean encheresremporter, boolean encheresencours, boolean encheresouvertes, int pageNumber, int pageSize) {
        Logger.log("Trace_ENI.log","listerEncheresConnecteByFilters : " + pageNumber + " " + pageSize);
        int min = 0;
        int max = 0;
        if(pageNumber > 1) {
            min = (pageNumber * pageSize) -1;
            max = min + pageSize;
        }
        else{
            min = 1;
            max = min + pageSize;
        }
        String Filter = "WHERE";
        List<Object> params = new ArrayList<>();
        Boolean EntrerAND = false;
      if (nomArticle != null && nomArticle.trim() != "") {
          Filter += " nom_article LIKE '%' + ? + '%' ";
          EntrerAND = true;
            params.add(nomArticle);
        }
      if (categorie != 0) {
          if(EntrerAND == true){
              Filter += " AND ";
          }
            Filter += " ARTICLES_VENDUS.no_categorie = ? ";
            params.add(categorie);
          EntrerAND = true;
        }
        if (radio == 1) {
            Boolean EntrerOR = false;
            if(EntrerAND == true){
                Filter += " AND ";
            }

            if (encheresencours) {
                Filter += " ( ";
                Filter += " (etat_article = 1 and ARTICLES_VENDUS.no_article IN (SELECT no_article FROM ENCHERES WHERE no_utilisateur = ?)) ";
                EntrerOR = true;
                params.add(no_utilisateur);
            }
            if (encheresouvertes) {
                if(EntrerOR == true){
                    Filter += " OR ";
                }else{
                    Filter += " ( ";
                }
                Filter += " (etat_article = 1 and ARTICLES_VENDUS.no_article IN (SELECT no_article FROM ENCHERES )) ";
                EntrerOR = true;
            }
            if (encheresremporter) {
                if(EntrerOR == true){
                    Filter += " OR ";
                } else{
                    Filter += " ( ";
                }
                Filter += " (etat_article = 2 and ARTICLES_VENDUS.prix_vente IN (SELECT montant_enchere FROM ENCHERES WHERE no_utilisateur =?)) ";
                params.add(no_utilisateur);
                Filter += ") ";
            }
            else{
                if(EntrerOR == true){
                    Filter += ") ";
                }
            }
        }
        else  if (radio == 2) {
            if(EntrerAND == true){
                Filter += " AND ";
            }
            Boolean EntrerOR = false;

            if (ventesencours) {
                Filter += " (ARTICLES_VENDUS.no_utilisateur = ? AND (";
                params.add(no_utilisateur);
                Filter += " etat_article = 1  ";
                EntrerOR = true;
            }
            if (ventesnoncommencer) {
                if(EntrerOR == true){
                    Filter += " OR ";
                }
                else{
                    Filter += " (ARTICLES_VENDUS.no_utilisateur = ? AND (";
                    params.add(no_utilisateur);
                }
                Filter += " etat_article = 0  ";
                EntrerOR = true;
            }
            if (ventesterminer) {
                if(EntrerOR == true){
                    Filter += " OR ";
                }
                else{
                    Filter += " (ARTICLES_VENDUS.no_utilisateur = ? AND (";
                    params.add(no_utilisateur);
                }
                Filter += " etat_article = 2  ";
                Filter += ")) ";
            }
            else{
                if(EntrerOR == true){
                    Filter += ")) ";
                }
            }
        }

        String sql = "SELECT UTILISATEURS.no_utilisateur, CATEGORIES.no_categorie, ARTICLES_VENDUS.*, RETRAITS.* \n" +
                "FROM ARTICLES_VENDUS INNER JOIN CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN \n" +
                "UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur INNER JOIN \n" +
                "RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article ";
        if(Filter != "WHERE"){
            sql += Filter;
        }

        List<CArticleVendu> allArticles = jdbcTemplate.query(sql, params.toArray(), new ArticleVenduRowMapper());

        List<CArticleVendu> filteredArticles = allArticles.stream()
                .skip(min - 1)
                .limit(max - min)
                .collect(Collectors.toList());

        return filteredArticles;
    }

    @Override
    public List<CEnchere> listEncheresByArticleId(int id) {
        Logger.log("Trace_ENI.log","listEncheresByArticleId : ");
        String sql = "SELECT ENCHERES.* FROM ENCHERES WHERE no_article= ? ORDER BY montant_enchere DESC";
        return  jdbcTemplate.query(sql, new Object[]{id},new EnchereRowMapper());

    }
    @Override
    public List<CArticleVendu> listEncheresConnecte() {
        Logger.log("Trace_ENI.log","listEncheresConnecte : ");
        String sql = "SELECT UTILISATEURS.no_utilisateur, CATEGORIES.no_categorie, ARTICLES_VENDUS.*, RETRAITS.* \n" +
                "   FROM  ARTICLES_VENDUS INNER JOIN CATEGORIES ON ARTICLES_VENDUS.no_categorie = CATEGORIES.no_categorie INNER JOIN \n" +
                "   UTILISATEURS ON ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur AND ARTICLES_VENDUS.no_utilisateur = UTILISATEURS.no_utilisateur INNER JOIN \n" +
                "  RETRAITS ON ARTICLES_VENDUS.no_article = RETRAITS.no_article";
        return  jdbcTemplate.query(sql, new ArticleVenduRowMapper());
    }

    @Override
    public String ProposeEnchere(CEnchere enchere) {
        Logger.log("Trace_ENI.log","ProposeEnchere : " + enchere);
        try{
            if(IsMaxOffre(enchere) > 0 && IsUserMaxOffre(enchere,IsMaxOffre(enchere)) > 0) {
                String updatePrevCreditsQuery = "UPDATE UTILISATEURS SET credit=(credit + ?)  WHERE no_utilisateur=?";
                jdbcTemplate.update(updatePrevCreditsQuery, IsMaxOffre(enchere), IsUserMaxOffre(enchere, IsMaxOffre(enchere)));
            }
            String updateProposeQuery = "INSERT INTO ENCHERES (no_utilisateur,no_article, montant_enchere,date_enchere) VALUES (?,?,?,?)";
            jdbcTemplate.update(updateProposeQuery, enchere.getUtilisateur().getNoUtilisateur(), enchere.getArticle().getNoArticle(), enchere.getMontant_enchere(), enchere.getDateEnchere());

            String updateCreditsQuery = "UPDATE UTILISATEURS SET credit=(credit - ?)  WHERE no_utilisateur=?";
            jdbcTemplate.update(updateCreditsQuery, enchere.getMontant_enchere(), enchere.getUtilisateur().getNoUtilisateur());
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
    public int IsMaxOffre(CEnchere enchere) {
        Logger.log("Trace_ENI.log","IsMaxOffre : " + enchere);
        String SelectRowsQuery = "SELECT COUNT(montant_enchere) FROM ENCHERES WHERE no_article=?";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{enchere.getArticle().getNoArticle()}, Integer.class);
        Integer maxOffre = 0;
        if(Nb_Rows > 0) {
            String sql = "SELECT MAX(montant_enchere) FROM ENCHERES WHERE no_article=?";
             maxOffre = jdbcTemplate.queryForObject(sql, new Object[]{enchere.getArticle().getNoArticle()}, Integer.class);
        }
        return maxOffre;
    }
    @Override
    public int IsUserMaxOffre(CEnchere enchere, int maxOffre) {
        Logger.log("Trace_ENI.log","IsUserMaxOffre : " + enchere);
        String SelectRowsQuery = "SELECT COUNT(no_utilisateur) FROM ENCHERES WHERE no_article=? AND montant_enchere = ?";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{enchere.getArticle().getNoArticle(), maxOffre}, Integer.class);
        Integer UserOffre = 0;
        if(Nb_Rows > 0) {
            String sql = "SELECT no_utilisateur FROM ENCHERES WHERE no_article=? AND montant_enchere = ?";
            UserOffre = jdbcTemplate.queryForObject(sql, new Object[]{enchere.getArticle().getNoArticle(), maxOffre}, Integer.class);
        }
        return UserOffre;
    }

    @Override
    public int WinnerOffre(int id) {
        Logger.log("Trace_ENI.log","IsUserMaxOffre : " + id);
        String SelectRowsQuery = "SELECT COUNT(montant_enchere) FROM ENCHERES  WHERE no_article = ?";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{id}, Integer.class);
        Integer UserOffre = 0;
        if(Nb_Rows > 0) {
            String sql = "SELECT no_utilisateur FROM ENCHERES WHERE no_article = ? AND montant_enchere = (SELECT MAX(montant_enchere) FROM ENCHERES  WHERE no_article = ?)";

            UserOffre = jdbcTemplate.queryForObject(sql, new Object[]{id, id}, Integer.class);
        }
        return UserOffre;
    }
    @Override
    public boolean IsPositifCredit(CEnchere enchere) {
        Logger.log("Trace_ENI.log","IsPositifCredit : " + enchere);
        String sql = "SELECT credit FROM UTILISATEURS WHERE no_utilisateur=?";
        Integer credit = jdbcTemplate.queryForObject(sql, new Object[]{enchere.getUtilisateur().getNoUtilisateur()}, Integer.class);

        if (credit != null && enchere.getMontant_enchere() < credit) {
            return true;
        } else {
            return false;
        }

    }
    @Override
    public boolean IsPositifOffre(CEnchere enchere) {
        Logger.log("Trace_ENI.log","IsPositifOffre : " + enchere);
        String SelectRowsQuery = "SELECT COUNT(montant_enchere) FROM ENCHERES WHERE no_article=?";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{enchere.getArticle().getNoArticle()}, Integer.class);
        if(Nb_Rows > 0) {
            String sql = "SELECT MAX(montant_enchere) FROM ENCHERES WHERE no_article=?";
            Integer maxOffre = jdbcTemplate.queryForObject(sql, new Object[]{enchere.getArticle().getNoArticle()}, Integer.class);

        if (maxOffre != null && enchere.getMontant_enchere() > maxOffre) {
            return true;
        } else {
            return false;
        }
        }
        else{
            return true;
        }
    }
    @Override
    public String remporterVente(CArticleVendu vente) {
        Logger.log("Trace_ENI.log","remporterVente : " + vente);
        try{
        String updateCreditsQuery = "UPDATE ARTICLES_VENDUS SET prix_vente=?,etat_article= ? WHERE no_article=?";
        jdbcTemplate.update(updateCreditsQuery, vente.getPrixVente(),vente.getEtatVente(),vente.getNoArticle());
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
    public String CheckSale(LocalDateTime localDate) {
        Logger.log("Trace_ENI.log","CheckSale : " + localDate);
        try{
        String SelectRowsQuery = "SELECT COUNT(no_article) FROM ARTICLES_VENDUS WHERE etat_article = 1 AND date_fin_encheres <= CONVERT(date,?,120)";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{localDate}, Integer.class);
        if(Nb_Rows > 0)
        {
        List<Integer> articleIDs = new ArrayList<>(Nb_Rows);
        String SelectVenteQuery = "SELECT no_article FROM ARTICLES_VENDUS WHERE etat_article = 1 AND date_fin_encheres <= CONVERT(date,?,120)";
        articleIDs = jdbcTemplate.queryForList(SelectVenteQuery, new Object[]{localDate}, Integer.class);

        for(int i =0 ;i<Nb_Rows;i++){
            String SelectRowsENCHERESQuery = "SELECT COUNT(no_encheres) FROM ENCHERES WHERE no_article = ?";
            Integer Nb_RowsENCHERES = jdbcTemplate.queryForObject(SelectRowsENCHERESQuery, new Object[]{articleIDs.get(i)}, Integer.class);
            if(Nb_RowsENCHERES > 0) {
                CArticleVendu articleVendu = viewArticle(articleIDs.get(i));
                articleVendu.setEtatVente(2);
                CEnchere enchere = new CEnchere();
                enchere.setArticle(articleVendu);
                articleVendu.setPrixVente(IsMaxOffre(enchere));
                remporterVente(articleVendu);
            }
        }
        }
        String SelectRowsNoStartQuery = "SELECT COUNT(no_article) FROM ARTICLES_VENDUS WHERE etat_article = 0 AND date_debut_encheres <= CONVERT(date,?,120)";
        Integer Nb_RowsNoStart = jdbcTemplate.queryForObject(SelectRowsNoStartQuery, new Object[]{localDate}, Integer.class);
        if(Nb_RowsNoStart > 0)
        {
            List<Integer> articleIDs = new ArrayList<>(Nb_RowsNoStart);
            String SelectVenteQuery = "SELECT no_article FROM ARTICLES_VENDUS WHERE etat_article = 0 AND date_debut_encheres <= CONVERT(date,?,120)";
            articleIDs = jdbcTemplate.queryForList(SelectVenteQuery, new Object[]{localDate}, Integer.class);

            for(int i =0 ;i<Nb_RowsNoStart;i++){
                String updateCreditsQuery = "UPDATE ARTICLES_VENDUS SET etat_article= 1 WHERE no_article=?";
                jdbcTemplate.update(updateCreditsQuery, articleIDs.get(i));
            }
        }
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
    public CEnchere afficherDetailEnchere(int enchereId) {
        Logger.log("Trace_ENI.log","afficherDetailEnchere : " + enchereId);
        String sql = "SELECT  ENCHERES.* FROM ENCHERES INNER JOIN\n" +
                "ARTICLES_VENDUS ON ENCHERES.no_article = ARTICLES_VENDUS.no_article INNER JOIN\n" +
                "UTILISATEURS ON ENCHERES.no_utilisateur = UTILISATEURS.no_utilisateur WHERE no_encheres=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{enchereId}, new EnchereRowMapper());
    }

    @Override
    public String modifierVente(CArticleVendu vente) {
        Logger.log("Trace_ENI.log","modifierVente : " + vente);
        try {
            if (IsPossibleModifySale(vente)) {
                String insertArticleQuery = "UPDATE  ARTICLES_VENDUS SET nom_article= ?, description= ?, date_debut_encheres= ?, date_fin_encheres= ?, prix_initial= ?, prix_vente= ?, no_utilisateur= ?, no_categorie= ?,etat_article= ? WHERE no_article =?";
                jdbcTemplate.update(insertArticleQuery, vente.getNomArticle(), vente.getDescription(), vente.getDateDebutEncheres(), vente.getDateFinEncheres(), vente.getMiseAPrix(), vente.getPrixVente(), vente.getVendeur().getNoUtilisateur(), vente.getCategorie().getNoCategorie(), vente.getEtatVente(), vente.getNoArticle());
            }
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
    public boolean
    IsPossibleModifySale(CArticleVendu vente) {
        Logger.log("Trace_ENI.log","IsPossibleModifySale : " + vente);

        String SelectRowsQuery = "SELECT COUNT(etat_article) FROM ARTICLES_VENDUS WHERE no_article =? AND date_debut_encheres <= GETDATE()";
        Integer Nb_Rows = jdbcTemplate.queryForObject(SelectRowsQuery, new Object[]{vente.getNoArticle()}, Integer.class);

        if(Nb_Rows > 0) {
            String SelectVenteQuery = "SELECT etat_article FROM ARTICLES_VENDUS WHERE no_article =? AND date_debut_encheres <= GETDATE()";
            Integer articleEtat = jdbcTemplate.queryForObject(SelectVenteQuery, new Object[]{vente.getNoArticle()}, Integer.class);
            if(articleEtat == 0){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            String insertArticleQuery = "UPDATE  ARTICLES_VENDUS SET etat_article= 1 WHERE no_article =?";
            jdbcTemplate.update(insertArticleQuery, vente.getNoArticle());
            return false;
        }

    }

    @Override
    public String SearchPhotoByArticleId(int id) {
        Logger.log("Trace_ENI.log","SearchPhotoByArticleId : " + id);
        String sql = "SELECT photo_url FROM ARTICLES_VENDUS WHERE no_article =?";
        String photoUrl = jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
        return photoUrl;
    }

    @Override
    public int IsVenteFinish(int id) {
        Logger.log("Trace_ENI.log","IsVenteFinish : " + id);
        String sql = "SELECT etat_article FROM ARTICLES_VENDUS WHERE no_article =?";
        Integer VenteFinish = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return VenteFinish;
    }
    @Override
    public String annulerVente(int id) {
        Logger.log("Trace_ENI.log","annulerVente : " + id);
        /* Pour l'identifiant de type d'etat d'une vente, se referencer au fichier README*/
        try{
        if(IsPossibleModifySale(viewArticle(id))) {
            String sql = "SELECT COUNT(no_article) FROM ENCHERES WHERE ENCHERES.no_article =?";
            Integer nbRows = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
            if(nbRows > 0){
                String updatePrevCreditsQuery = "UPDATE UTILISATEURS SET credit=(credit + (SELECT montant_enchere FROM ENCHERES WHERE no_article = ? AND no_utilisateur=? ))  WHERE no_utilisateur=?";
                jdbcTemplate.update(updatePrevCreditsQuery, id, WinnerOffre(id), WinnerOffre(id));

            }
            String insertArticleQuery = "UPDATE ARTICLES_VENDUS SET etat_article= 3  WHERE no_article=?";
            jdbcTemplate.update(insertArticleQuery, id);
        }
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
    public String ajouterPhotoVente(CArticleVendu vente) {
        Logger.log("Trace_ENI.log","ajouterPhotoVente : " + vente);
        try{
        String insertArticleQuery = "UPDATE ARTICLES_VENDUS SET photo_url= ? WHERE no_article=?";
        jdbcTemplate.update(insertArticleQuery, vente.getPhoto(), vente.getNoArticle());
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
    public List<CUtilisateur> voirEncherisseurs(CArticleVendu vente) {
        Logger.log("Trace_ENI.log","voirEncherisseurs : " + vente);
        String sql = "SELECT        VENDEUR.nom AS Nom_Encherisseur, VENDEUR.prenom AS Prenom_Encherisseur, ENCHERISSEURS.nom AS Nom_Vendeur, ENCHERISSEURS.prenom AS Prénom_Vendeur, ENCHERES.date_enchere, \n" +
                "                         ENCHERES.montant_enchere, ENCHERES.no_encheres, ARTICLES_VENDUS.no_article, ARTICLES_VENDUS.nom_article, ARTICLES_VENDUS.description, ARTICLES_VENDUS.date_debut_encheres, \n" +
                "                         ARTICLES_VENDUS.date_fin_encheres, ARTICLES_VENDUS.prix_initial, ARTICLES_VENDUS.prix_vente, ARTICLES_VENDUS.no_utilisateur, ARTICLES_VENDUS.no_categorie, ARTICLES_VENDUS.photo_url, \n" +
                "                         ARTICLES_VENDUS.etat_article\n" +
                "FROM            UTILISATEURS AS ENCHERISSEURS INNER JOIN\n" +
                "                         ARTICLES_VENDUS ON ENCHERISSEURS.no_utilisateur = ARTICLES_VENDUS.no_utilisateur AND ENCHERISSEURS.no_utilisateur = ARTICLES_VENDUS.no_utilisateur RIGHT OUTER JOIN\n" +
                "                         ENCHERES ON ARTICLES_VENDUS.no_article = ENCHERES.no_article LEFT OUTER JOIN\n" +
                "                         UTILISATEURS AS VENDEUR ON ENCHERES.no_utilisateur = VENDEUR.no_utilisateur WHERE ARTICLES_VENDUS.no_article=?";
        return jdbcTemplate.queryForList(sql, new Object[]{vente.getNoArticle()}, CUtilisateur.class);
    }

    public class EnchereRowMapper implements RowMapper<CEnchere> {

        @Override
        public CEnchere mapRow(ResultSet rs, int rowNum) throws SQLException {
            CEnchere a = new CEnchere();
            CUtilisateur utilisateur = utilisateurDAO.ViewProfil(rs.getInt("no_utilisateur"));
            CArticleVendu article = viewArticle(rs.getInt("no_article"));
            a.setNoEnchere(rs.getInt("no_encheres"));
            a.setDateEnchere((rs.getTimestamp("date_enchere")).toLocalDateTime());
            a.setMontant_enchere(rs.getInt("montant_enchere"));
            a.setUtilisateur(utilisateur);
            a.setArticle(article);

            return a;
        }
    }
    public class ArticleVenduRowMapper implements RowMapper<CArticleVendu> {

        @Override
        public CArticleVendu mapRow(ResultSet rs, int rowNum) throws SQLException {
            CArticleVendu a = new CArticleVendu();


            CCategorie categorie = categorieDAO.SearchCategorie(rs.getInt("no_categorie"));

            CUtilisateur utilisateur = utilisateurDAO.ViewProfil(rs.getInt("no_utilisateur"));
            if(IsVenteFinish(rs.getInt("no_article")) == 2) {
                CUtilisateur acheteur = utilisateurDAO.viewAcheteurByArticleID(rs.getInt("no_article"));
                a.setAcheteur(acheteur);
            }
            CRetrait retrait = retraitDAO.SearchRetrait(rs.getInt("no_retrait"));
            a.setNoArticle(rs.getInt("no_article"));
            a.setVendeur(utilisateur);
            a.setCategorie(categorie);
            a.setDescription(rs.getString("description"));
            a.setDateDebutEncheres((rs.getTimestamp("date_debut_encheres")).toLocalDateTime());
            a.setDateFinEncheres((rs.getTimestamp("date_fin_encheres")).toLocalDateTime());
            a.setEtatVente(rs.getInt("etat_article"));
            a.setMiseAPrix(rs.getInt("prix_initial"));
            a.setNomArticle(rs.getString("nom_article"));
            a.setPhoto(rs.getString("photo_url"));
            a.setPrixVente(rs.getInt("prix_vente"));
            a.setRetrait(retrait);

            return a;
        }
    }
}
