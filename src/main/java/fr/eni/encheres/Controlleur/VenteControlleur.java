package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sale")
@SessionAttributes({ "membreEnSession" })
public class VenteControlleur {

    private final EnchereService enchereService;
    private final UtilisateurService utilisateurService;
    private CUtilisateur UtilisateurConnecte;
    public VenteControlleur(EnchereService enchereService,UtilisateurService utilisateurService)
    {
        this.enchereService = enchereService;
        this.utilisateurService = utilisateurService;
    }
    @ModelAttribute("membreEnSession")
    public CUtilisateur MembreAuthenticate(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UtilisateurConnecte	= utilisateurService.getUtilisateurByEmail(authentication.getName());
            if(UtilisateurConnecte.getNoUtilisateur() > 0 && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
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

    @GetMapping("/create")
    public String getCreateArticleVendu(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateEncheres ");
        CArticleVendu ArticleVendu= new CArticleVendu();
        model.addAttribute("ArticleVendu",ArticleVendu);
        return "view_sale";
    }
    @GetMapping("/modify")
    public String getModifyArticleVendu(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyEncheres ");
        CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
        model.addAttribute("ArticleVendu",ArticleVendu);
        return "view_sale";
    }
    @GetMapping("/cancel")
    public String getAnnuleArticleVendu(@RequestParam(name = "id", required = true) int id,Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getAnnuleArticleVendu ");
        CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
        model.addAttribute("ArticleVendu",ArticleVendu);
        return "view_sale";
    }

    @PostMapping("/create")
    public String postArticleVenduCreate(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                     BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postArticleVenduCreate ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "bid";
        } else {
            enchereService.vendreArticle(ArticleVendu);
            return "redirect:/bid";
        }
    }

    @PostMapping("/modify")
    public String postArticleVenduModify(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                     BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresModify ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "bid";
        } else {
            enchereService.modifierVente(ArticleVendu);
            return "redirect:/bid";
        }
    }
    @PostMapping("/cancel")
    public String postArticleVenduDelete(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                     BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresDelete ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "bid";
        } else {
            enchereService.annulerVente(ArticleVendu);
            return "redirect:/bid";
        }
    }
}
