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
@RequestMapping("/users")
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
            System.out.println(UtilisateurConnecte.toString());
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
    public String getUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getUsers ");
        return "view_user_list";
    }

    @GetMapping("/create")
    public String getCreateUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateUsers ");
        model.addAttribute("postValue", "/users/create");
        model.addAttribute("user", new CUtilisateur());
        return "view_user_edit";
    }

    @PostMapping("/create")
    public String postCreateUsers(@Validated @ModelAttribute("user") CUtilisateur user, BindingResult result, RedirectAttributes redirectAttributes) {
        Logger.log("Trace_ENI.log","Controlleur : postCreateUsers ");
        if (result.hasErrors()) {
            return "view_user_edit";
        }
        utilisateurService.Inscription(user);
        return "redirect:/bid";
    }

    @GetMapping("/modify")
    public String getModifyUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyUsers ");
        if(UtilisateurConnecte != null){
            model.addAttribute("postValue", "/users/modify");
            model.addAttribute("user", UtilisateurConnecte);
            return "view_user_edit";
        }else{
            return "redirect:/create";
        }
    }

    @PostMapping("/modify")
    public String postModifyUsers(@Validated @ModelAttribute("user") CUtilisateur user, BindingResult result, RedirectAttributes redirectAttributes) {
        Logger.log("Trace_ENI.log","Controlleur : postModifyUsers ");

        System.out.println("user : " + user.toString());


        String mdpActuel = user.getMotdepasse().split(",")[0];
        String mdpNouveau = "";
        String mdpConfirmation = "";

        //si user.getMotdepasse().split(",")[1] exist
        if(user.getMotdepasse().split(",").length > 1){
            mdpNouveau = user.getMotdepasse().split(",")[1];
        }
        if(user.getMotdepasse().split(",").length > 2){
            mdpConfirmation = user.getMotdepasse().split(",")[2];
        }

        if(mdpNouveau.equals(mdpConfirmation) && !mdpNouveau.equals("") && !mdpConfirmation.equals("")) {
            user.setMotdepasse(mdpNouveau);

            utilisateurService.ModifyProfil(user);
            System.out.println(user);
            return "view_user_edit";

            //return "redirect:/Detail";
        }else if(!mdpNouveau.equals(mdpConfirmation)){
            return "view_user_edit";
        }else if(mdpNouveau.equals("") || mdpConfirmation.equals("")){
            return "view_user_edit";
        }

        return "view_user_edit";

        /*if (result.hasErrors()) {
            return "view_user_edit";
        }
        if(UtilisateurConnecte != null) {
            utilisateurService.ModifyProfil(user);
            return "redirect:/Enchere";
        }else{
            return "redirect:/login";
        }*/
    }


    @GetMapping("/detail")
    public String getDetailUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getDetailUsers ");
        if(UtilisateurConnecte != null){
            model.addAttribute("user", UtilisateurConnecte);
        }
        return "view_user_detail";
    }


    @GetMapping("/delete")
    public String getDeleteUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getDeleteUsers ");
        return "view-user";
    }
    @GetMapping("/deactivation")
    public String getDesactivationUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getDesactivationUsers ");
        return "view-user";
    }
    @GetMapping("/activation")
    public String getActivationUsers() {
        Logger.log("Trace_ENI.log","Controlleur : getActivationUsers ");
        return "view-user";
    }
    @PostMapping("/delete")
    public String postUsersDelete() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersDelete ");
        return "redirect:/bid";
    }
    @PostMapping("/deactivation")
    public String postUsersDesactivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersDesactivation ");
        return "redirect:/bid";
    }
    @PostMapping("/activation")
    public String postUsersActivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersActivation ");
        return "redirect:/bid";
    }
}
