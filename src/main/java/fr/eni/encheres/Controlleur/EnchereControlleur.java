package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bid")
@SessionAttributes({ "membreEnSession" })
public class EnchereControlleur {
    private final EnchereService enchereService;
    private final UtilisateurService utilisateurService;
    private CUtilisateur UtilisateurConnecte;
    public EnchereControlleur(EnchereService enchereService,UtilisateurService utilisateurService)
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
        return "view_bid";
    }

    @GetMapping("/detail")
    public String getDetailEncheres(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getDetailEncheres ");
        CEnchere Enchere=enchereService.afficherDetailEnchere(id);
        model.addAttribute("Enchere",Enchere);

        return "view_bid";
    }

    @GetMapping("/purpose")
    public String getProposeEncheres(@RequestParam(name = "id", required = true) int id,Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getProposeEncheres ");
        CEnchere Enchere=enchereService.afficherDetailEnchere(id);
        model.addAttribute("Enchere",Enchere);
        return "view_bid";
    }

    @PostMapping("/purpose")
    public String postEncheresPropose(@Validated @ModelAttribute("Enchere") CEnchere Enchere,
                                 BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresPropose ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_bid";
        } else {
            enchereService.faireEnchere(Enchere);
            return "redirect:/bid";
        }
    }
}
