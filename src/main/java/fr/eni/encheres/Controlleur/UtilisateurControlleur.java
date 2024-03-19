package fr.eni.encheres.Controlleur;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CEnchere;
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

    public UtilisateurControlleur(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String getUsers() {
        return "view-user";
    }

    @GetMapping("/Create")
    public String getCreateUsers() {
        return "view-user";
    }

    @GetMapping("/Detail")
    public String getDetailUsers() {
        return "view-user";
    }

    @GetMapping("/Modify")
    public String getModifyUsers() {
        return "view-user";
    }
    @GetMapping("/Delete")
    public String getDeleteUsers() {
        return "view-user";
    }
    @GetMapping("/Desactivation")
    public String getDesactivationUsers() {
        return "view-user";
    }
    @GetMapping("/Activation")
    public String getActivationUsers() {
        return "view-user";
    }
    @PostMapping("/Create")
    public String postUsersCreate() {

            return "redirect:/Enchere";
    }
    @PostMapping("/Modify")
    public String postUsersModify() {

        return "redirect:/Enchere";
    }
    @PostMapping("/Delete")
    public String postUsersDelete() {

        return "redirect:/Enchere";
    }
    @PostMapping("/Desactivation")
    public String postUsersDesactivation() {

        return "redirect:/Enchere";
    }
    @PostMapping("/Activation")
    public String postUsersActivation() {

        return "redirect:/Enchere";
    }
}
