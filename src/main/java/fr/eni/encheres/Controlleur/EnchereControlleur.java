package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CCategorie;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/bid")
@SessionAttributes({"membreEnSession", "CategorieSession"})
public class EnchereControlleur {
    private final EnchereService enchereService;
    private final UtilisateurService utilisateurService;
    private final CategorieService categorieService;
    private CUtilisateur UtilisateurConnecte;
    public EnchereControlleur(EnchereService enchereService,UtilisateurService utilisateurService,CategorieService categorieService)
    {
        this.enchereService = enchereService;
        this.utilisateurService = utilisateurService;
        this.categorieService = categorieService;
    }
    @ModelAttribute("membreEnSession")
    public CUtilisateur MembreAuthenticate(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UtilisateurConnecte	= utilisateurService.getUtilisateurByEmail(authentication.getName());
            if(UtilisateurConnecte.getNoUtilisateur() > 0 && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                UtilisateurConnecte.setAdministrateur(true);
            }
            else if(UtilisateurConnecte.getNoUtilisateur() > 0 && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
                UtilisateurConnecte.setAdministrateur(true);
            }
            else {
                UtilisateurConnecte.setAdministrateur(false);
            }
        } else {
            UtilisateurConnecte = null;
        }
        return UtilisateurConnecte;
    }

    @ModelAttribute("CategorieSession")
    public List<CCategorie> chargerSession() {
        System.out.println("liste de categorie");
        return categorieService.ListCategorie();
    }
    @GetMapping
    public String getEnchere(Authentication authentication, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getEnchere ");
        List<CEnchere> encheres = null;
        if(authentication != null && authentication.isAuthenticated()){
            encheres = enchereService.listerEncheresConnecte();
        }
        else{
            encheres = enchereService.listerEncheresDeconnecte();
        }
        model.addAttribute("encheres",encheres);
        enchereService.CheckSale();
        return "view_bid_list";
    }

    @GetMapping("/detail")
    public String getDetailEncheres(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getDetailEncheres ");
        CEnchere Enchere=enchereService.afficherDetailEnchere(id);
        model.addAttribute("enchere",Enchere);
        CArticleVendu ArticleVendu=enchereService.AfficherArticleById(Enchere.getArticle().getNoArticle());
        model.addAttribute("Vente",ArticleVendu);
        CUtilisateur winner = utilisateurService.ViewProfil(enchereService.WinnerOffre(ArticleVendu.getNoArticle()));
        if(enchereService.IsVenteFinish(ArticleVendu.getNoArticle()) == 2) {
            if (winner == UtilisateurConnecte) {
                model.addAttribute("Msg_FinVente", "Vous avez remporté la vente");
            } else {
                model.addAttribute("Msg_FinVente", winner.getPseudo() + " a remporté l'enchere");
            }
        }
        else{
            model.addAttribute("Msg_FinVente", "Enchere toujours en cours");
        }
        return "view_bid_detail";
    }

    @GetMapping("/purpose")
    public String getProposeEncheres(@RequestParam(name = "id", required = true) int id,Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getProposeEncheres ");
        CEnchere Enchere=enchereService.afficherDetailEnchere(id);
        model.addAttribute("enchere",Enchere);
        CArticleVendu ArticleVendu=enchereService.AfficherArticleById(Enchere.getArticle().getNoArticle());
        model.addAttribute("Vente",ArticleVendu);
        int MOffre=enchereService.IsMaxOffre(Enchere);
        model.addAttribute("MOffre",MOffre);
        CUtilisateur utilisateur=utilisateurService.ViewProfil(enchereService.IsUserMaxOffre(Enchere,MOffre));
        model.addAttribute("MOffreUser",utilisateur.getPseudo());

        return "view_bid_add";
    }

    @PostMapping("/purpose")
    public String postEncheresPropose(@ModelAttribute("id") int id,
                                      @ModelAttribute("Proposition") int Enchere) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresPropose " +id +  Enchere);
            CEnchere enchere = new CEnchere();
            CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
            enchere.setMontant_enchere(Enchere);
            enchere.setArticle(ArticleVendu);
            enchere.setUtilisateur(UtilisateurConnecte);
            enchere.setDateEnchere(LocalDateTime.now());
            enchereService.faireEnchere(enchere);
            return "redirect:/bid";
    }
}
