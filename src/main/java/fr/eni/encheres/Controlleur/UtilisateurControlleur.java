package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/Users")
public class UtilisateurControlleur {

    private final UtilisateurService utilisateurService;
    private CUtilisateur UtilisateurConnecte;

    public UtilisateurControlleur(UtilisateurService utilisateurService) {
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


    @GetMapping
    public String getUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getUsers ");

        if(UtilisateurConnecte != null){
            model.addAttribute("user", UtilisateurConnecte);
        }
        return "view-user-detail";
    }

    @GetMapping("/Create")
    public String getCreateUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateUsers ");
        model.addAttribute("postValue", "/Users/Create");
        model.addAttribute("user", new CUtilisateur());
        return "view-user-edit";
    }

    @GetMapping("/Modify")
    public String getModifyUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyUsers ");
        if(UtilisateurConnecte != null){
            model.addAttribute("postValue", "/Users/Modify");
            model.addAttribute("user", UtilisateurConnecte);
            return "view-user-edit";
        }else{
            return "redirect:/Create";
        }
    }

    @GetMapping("/Detail")
    public String getDetailUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getDetailUsers ");
        return "view-user";
    }


    @GetMapping("/Delete")
    public String getDeleteUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getDeleteUsers ");
        return "view-user";
    }
    @GetMapping("/Desactivation")
    public String getDesactivationUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getDesactivationUsers ");
        return "view-user";
    }
    @GetMapping("/Activation")
    public String getActivationUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getActivationUsers ");
        return "view-user";
    }
    @PostMapping("/Create")
    public String postUsersCreate() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersCreate ");
            return "redirect:/Enchere";
    }
    @PostMapping("/Modify")
    public String postUsersModify() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersModify ");
        return "redirect:/Enchere";
    }
    @PostMapping("/Delete")
    public String postUsersDelete() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersDelete ");
        return "redirect:/Enchere";
    }
    @PostMapping("/Desactivation")
    public String postUsersDesactivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersDesactivation ");
        return "redirect:/Enchere";
    }
    @PostMapping("/Activation")
    public String postUsersActivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersActivation ");
        return "redirect:/Enchere";
    }
}
