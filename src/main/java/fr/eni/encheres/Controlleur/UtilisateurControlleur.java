package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.exceptions.BusinessCode;
import fr.eni.encheres.exceptions.BusinessException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
@SessionAttributes({ "membreEnSession" })
public class UtilisateurControlleur extends BaseControlleur  {

    private final UtilisateurService utilisateurService;

    BusinessException be = new BusinessException();

    public UtilisateurControlleur(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public String getUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getUsers ");
        List<CUtilisateur> users = utilisateurService.ViewAllUtilisateurs();
        model.addAttribute("users",users);
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
    public String postCreateUsers(@Valid @ModelAttribute("user") CUtilisateur user, BindingResult bindingResult, Model model ) {
        Logger.log("Trace_ENI.log","Controlleur : postCreateUsers ");

        if(bindingResult.hasErrors()){
            model.addAttribute("postValue", "/users/create");
            return "view_user_edit";
        }

        String mdp = "";
        String mdpConfirmation = "";

        if(user.getMotdepasse().split(",").length > 0){
            mdp = user.getMotdepasse().split(",")[0];
        }
        if(user.getMotdepasse().split(",").length > 1){
            mdpConfirmation = user.getMotdepasse().split(",")[1];
        }

        user.setMotdepasse(mdp);

        if(utilisateurService.checkUser(user.getEmail())){
            return "redirect:/login";
        }else{
            user.setCredit(0);
            user.setAdministrateur(0);
            user.setActive(false);
            utilisateurService.Inscription(user);
            return "redirect:/users/detail";
        }
    }
    @GetMapping("/modify/password")
    public String getModifyPasswordUsers(@RequestParam(name = "email", required = false) String email,
                                        @RequestParam(name = "mdp", required = false) String mdp,
                                        @RequestParam(name = "mdpConfirmer", required = false) String mdpConfirmer, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyPasswordUsers ");
        if(UtilisateurConnecte == null){
            model.addAttribute("postValue", "/users/modify/password");
            return "view_user_password";
        }else{
            return "redirect:/modify";
        }
    }
    @PostMapping("/modify/password")
    public String postModifyPasswordUsers(@ModelAttribute(name = "email") String email,
                                          @ModelAttribute(name = "mdp") String mdp,
                                          @ModelAttribute(name = "mdpConfirmer") String mdpConfirmer,
                                          BindingResult bindingResult,
                                          Model model) {
        Logger.log("Trace_ENI.log","Controlleur : postModifyUsersPassword ");
        try {
            CUtilisateur user = utilisateurService.getUtilisateurByEmail(email);

            if(user != null) {
                if (mdp.equals(mdpConfirmer)) {
                    user.setMotdepasse(mdp);
                    utilisateurService.ModifyProfil(user);
                    return "redirect:/login";
                } else {
                    be.add(BusinessCode.VALIDATION_USER_MDP);
                    throw be;
                }
            }
        } catch (BusinessException e) {
            e.getClefsExternalisations().forEach(key -> {
                ObjectError error = new ObjectError("global", key);
                bindingResult.addError(error);
            });
            model.addAttribute("postValue", "/users/modify/password");
            return "view_user_password";
        }
        return "view_user_password";
    }

    @GetMapping("/modify")
    public String getModifyUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyUsers ");
        if(UtilisateurConnecte.getNoUtilisateur() > 0){
            model.addAttribute("postValue", "/users/modify");
            model.addAttribute("user", UtilisateurConnecte);
            return "view_user_edit";
        }else{
            return "redirect:/create";
        }
    }

    @PostMapping("/modify")
    public String postModifyUsers(@Validated @ModelAttribute("user") CUtilisateur user, BindingResult bindingResult, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : postModifyUsers ");

        String mdpActuel = user.getMotdepasse().split(",")[0];
        String mdpNouveau = "";
        String mdpConfirmation = "";

        if(user.getMotdepasse().split(",").length > 1){
            mdpNouveau = user.getMotdepasse().split(",")[1];
        }
        if(user.getMotdepasse().split(",").length > 2){
            mdpConfirmation = user.getMotdepasse().split(",")[2];
        }

        if(mdpNouveau.equals("") && mdpConfirmation.equals("")){

            try {
                if(utilisateurService.verifPassword(mdpActuel, user)) {
                    user.setMotdepasse(mdpActuel);
                    utilisateurService.ModifyProfil(user);
                    return "redirect:/users/detail";
                }else{
                    System.out.println("avant le throw new");

                    be.add(BusinessCode.VALIDATION_USER_MDP);

                    throw be;
                }
            } catch (BusinessException e) {
                e.getClefsExternalisations().forEach(key -> {
                    ObjectError error = new ObjectError("global", key);
                    bindingResult.addError(error);
                });

                model.addAttribute("postValue", "/users/modify");

                return "view_user_edit";
            }
        }else if(mdpNouveau.equals(mdpConfirmation) && !mdpNouveau.equals("") && !mdpConfirmation.equals("")) {
            if(utilisateurService.verifPassword(mdpActuel, user)){
                user.setMotdepasse(mdpNouveau);
                utilisateurService.ModifyProfil(user);
                return "redirect:/users/detail";
            }else{
                return "view_user_edit";
            }


        }else if(!mdpNouveau.equals(mdpConfirmation)){
            return "view_user_edit";
        }else if(mdpNouveau.equals("") || mdpConfirmation.equals("")){
            return "view_user_edit";
        }

        return "view_user_edit";
    }


    @GetMapping("/detail")
    public String getDetailUsers(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getDetailUsers ");
        if(UtilisateurConnecte.getNoUtilisateur() > 0){
            model.addAttribute("user", UtilisateurConnecte);
        }
        return "view_user_detail";
    }

    @GetMapping("/delete")
    public String getDeleteUsers(@RequestParam(name = "id", required = true) int id) {
        Logger.log("Trace_ENI.log","Controlleur : getDeleteUsers ");
        utilisateurService.DeleteProfil(id);
        return "redirect:/users";
    }

    @GetMapping("/deactivation")
    public String getDesactivationUsers(@RequestParam(name = "id", required = true) int id) {
        Logger.log("Trace_ENI.log","Controlleur : getDesactivationUsers ");
        utilisateurService.DesactiveProfil(utilisateurService.ViewProfil(id));

        return "redirect:/users";
    }

    @GetMapping("/activation")
    public String getActivationUsers(@RequestParam(name = "id", required = true) int id) {
        Logger.log("Trace_ENI.log","Controlleur : getActivationUsers ");
        utilisateurService.ActiveProfil(utilisateurService.ViewProfil(id));
        return "redirect:/users";
    }
    @GetMapping("/administrateur")
    public String getAdministrateurUsers(@RequestParam(name = "id", required = true) int id,Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getAdministrateurUsers ");
        if(utilisateurService.ViewProfil(id).isAdministrateur() == 1){
            utilisateurService.ModifyRoleUtilisateur(id,0);
        }
        else if(utilisateurService.ViewProfil(id).isAdministrateur() == 0){
            utilisateurService.ModifyRoleUtilisateur(id,1);
        }

        if(UtilisateurConnecte.getNoUtilisateur() > 0){
            model.addAttribute("user", UtilisateurConnecte);
        }
        return "redirect:/users";
    }
    @PostMapping("/buycredits")
    public String postbuycredits(@RequestParam(name = "id", required = true) int id,
                                 @RequestParam(name = "NumberPoint", required = true) int NumberPoint) {
        Logger.log("Trace_ENI.log","Controlleur : postbuycredits ");
        CUtilisateur users = utilisateurService.ViewProfil(id);
        utilisateurService.achatCredits(users,NumberPoint);
        return "redirect:/users";
    }
    @PostMapping("/delete")
    public String postUsersDelete() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersDelete ");
        return "redirect:/users";
    }
    @PostMapping("/deactivation")
    public String postUsersdeactivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersdeactivation ");
        return "redirect:/users";
    }
    @PostMapping("/activation")
    public String postUsersActivation() {
        Logger.log("Trace_ENI.log","Controlleur : postUsersActivation ");
        return "redirect:/users";
    }

    @PostMapping("/deleteMultiUsers")
    public String postDeleteMultiUsers(@RequestParam(name = "supprimeMultiUtilisateur", required = false) int[] userIds) {
        List<CUtilisateur> users = new ArrayList<>();
        if (userIds != null && userIds.length > 0) {
            for (int userId : userIds) {
                users.add(utilisateurService.ViewProfil(userId));
            }
            utilisateurService.DeleteMultiProfil(users);
        }
        return "redirect:/users";
    }
}
