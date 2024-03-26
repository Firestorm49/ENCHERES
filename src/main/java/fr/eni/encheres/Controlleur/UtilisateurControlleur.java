package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CUtilisateur;
import fr.eni.encheres.exceptions.BusinessCode;
import fr.eni.encheres.exceptions.BusinessException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/users")
@SessionAttributes({ "membreEnSession" })
public class UtilisateurControlleur {

    private final UtilisateurService utilisateurService;

    BusinessException be = new BusinessException();

    public UtilisateurControlleur(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }
    public CUtilisateur UtilisateurConnecte;

    @GetMapping
    public String getUsers(Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getUsers ");
        List<CUtilisateur> users = utilisateurService.ViewAllUtilisateurs();
        model.addAttribute("users",users);
        return "view_user_list";
    }

    @GetMapping("/create")
    public String getCreateUsers(Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getCreateUsers ");
        model.addAttribute("postValue", "/users/create");
        model.addAttribute("user", new CUtilisateur());
        return "view_user_edit";
    }

    @PostMapping("/create")
    public String postCreateUsers(@Valid @ModelAttribute("user") CUtilisateur user, BindingResult bindingResult, Model model ) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postCreateUsers ");

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
            String result = utilisateurService.Inscription(user);
            if(result != ErrorCode.NO_ERROR){
                Logger.log("Trace_ERROR.log","UtilisateurControlleur: postCreateUsers - " + result);
                model.addAttribute("ErrorStringCode",result);
                return "view_user_edit";
            }
            else{
                return "redirect:/users/detail";
            }

        }
    }
    @GetMapping("/modify/password")
    public String getModifyPasswordUsers(@RequestParam(name = "email", required = false) String email,
                                        @RequestParam(name = "mdp", required = false) String mdp,
                                        @RequestParam(name = "mdpConfirmer", required = false) String mdpConfirmer, Model model, HttpSession session) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getModifyPasswordUsers ");
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
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
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postModifyUsersPassword ");
        try {
            CUtilisateur user = utilisateurService.getUtilisateurByEmail(email);

            if(user != null) {
                if (mdp.equals(mdpConfirmer)) {
                    user.setMotdepasse(mdp);
                    String result = utilisateurService.ModifyProfil(user);
                    if(result != ErrorCode.NO_ERROR){
                        Logger.log("Trace_ERROR.log","UtilisateurControlleur: postModifyUsersPassword - " + result);
                        model.addAttribute("ErrorStringCode",result);
                        return "view_user_password";
                    }
                    else{
                        return "redirect:/login";
                    }
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
    public String getModifyUsers(Model model, HttpSession session) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getModifyUsers ");
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
        if(UtilisateurConnecte != null && UtilisateurConnecte.getNoUtilisateur() > 0){
            model.addAttribute("postValue", "/users/modify");
            model.addAttribute("user", UtilisateurConnecte);
            return "view_user_edit";
        }else{
            return "redirect:/create";
        }
    }

    @PostMapping("/modify")
    public String postModifyUsers(@Validated @ModelAttribute("user") CUtilisateur user, BindingResult bindingResult, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postModifyUsers ");

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
                    String result = utilisateurService.ModifyProfil(user);
                    if(result != ErrorCode.NO_ERROR){
                        Logger.log("Trace_ERROR.log","UtilisateurControlleur: postModifyUsers - " + result);
                        model.addAttribute("ErrorStringCode",result);
                        return "view_user_edit";
                    }
                    else{
                        return "redirect:/users/detail";
                    }

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
                String result = utilisateurService.ModifyProfil(user);
                if(result != ErrorCode.NO_ERROR){
                    Logger.log("Trace_ERROR.log","UtilisateurControlleur: postModifyUsers - " + result);
                    model.addAttribute("ErrorStringCode",result);
                    return "view_user_edit";
                }
                else{
                    return "redirect:/users/detail";
                }

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
    public String getDetailUsers(Model model, HttpSession session) {
        Logger.log("Trace_ENI.log", "UtilisateurControlleur : getDetailUsers " );
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
            model.addAttribute("user", UtilisateurConnecte);

        return "view_user_detail";
    }

    @GetMapping("/delete")
    public String getDeleteUsers(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getDeleteUsers ");
       String result = utilisateurService.DeleteProfil(id);
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","UtilisateurControlleur: getDeleteUsers - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_user_list";
        }
        else{
            return "redirect:/users";
        }
    }

    @GetMapping("/deactivation")
    public String getDesactivationUsers(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getDesactivationUsers ");
        String result = utilisateurService.DesactiveProfil(utilisateurService.ViewProfil(id));
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","UtilisateurControlleur: getDesactivationUsers - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_user_list";
        }
        else{
            return "redirect:/users";
        }
    }

    @GetMapping("/activation")
    public String getActivationUsers(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getActivationUsers ");
        String result = utilisateurService.ActiveProfil(utilisateurService.ViewProfil(id));
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","UtilisateurControlleur: getActivationUsers - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_user_list";
        }
        else{
            return "redirect:/users";
        }
    }
    @GetMapping("/administrateur")
    public String getAdministrateurUsers(@RequestParam(name = "id", required = true) int id,Model model, HttpSession session) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : getAdministrateurUsers ");
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
        if(UtilisateurConnecte != null && UtilisateurConnecte.getNoUtilisateur() > 0){
            model.addAttribute("user", UtilisateurConnecte);
        }
        if(utilisateurService.ViewProfil(id).isAdministrateur() == 1){
            String result = utilisateurService.ModifyRoleUtilisateur(id,0);
            if(result != ErrorCode.NO_ERROR){
                Logger.log("Trace_ERROR.log","UtilisateurControlleur: getAdministrateurUsers - " + result);
                model.addAttribute("ErrorStringCode",result);
                return "view_user_list";
            }
            else{
                return "redirect:/users";
            }
        }
        else if(utilisateurService.ViewProfil(id).isAdministrateur() == 0){
            String result = utilisateurService.ModifyRoleUtilisateur(id,1);
            if(result != ErrorCode.NO_ERROR){
                Logger.log("Trace_ERROR.log","UtilisateurControlleur: getAdministrateurUsers - " + result);
                model.addAttribute("ErrorStringCode",result);
                return "view_user_list";
            }
            else{
                return "redirect:/users";
            }
        }
        else{
            return "redirect:/users";
        }
    }
    @PostMapping("/buycredits")
    public String postbuycredits(@RequestParam(name = "id", required = true) int id,
                                 @RequestParam(name = "NumberPoint", required = true) int NumberPoint, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postbuycredits ");
        CUtilisateur users = utilisateurService.ViewProfil(id);
        String result = utilisateurService.achatCredits(users,NumberPoint);
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","UtilisateurControlleur: postbuycredits - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_user_detail";
        }
        else{
            return "redirect:/users";
        }
    }
    @PostMapping("/delete")
    public String postUsersDelete() {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postUsersDelete ");
        return "redirect:/users";
    }
    @PostMapping("/deactivation")
    public String postUsersdeactivation() {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postUsersdeactivation ");
        return "redirect:/users";
    }
    @PostMapping("/activation")
    public String postUsersActivation() {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postUsersActivation ");
        return "redirect:/users";
    }

    @PostMapping("/deleteMultiUsers")
    public String postDeleteMultiUsers(@RequestParam(name = "supprimeMultiUtilisateur", required = false) int[] userIds, Model model) {
        Logger.log("Trace_ENI.log","UtilisateurControlleur : postDeleteMultiUsers ");
        List<CUtilisateur> users = new ArrayList<>();
        if (userIds != null && userIds.length > 0) {
            for (int userId : userIds) {
                users.add(utilisateurService.ViewProfil(userId));
            }
            String result = utilisateurService.DeleteMultiProfil(users);
            if(result != ErrorCode.NO_ERROR){
                Logger.log("Trace_ERROR.log","UtilisateurControlleur: postDeleteMultiUsers - " + result);
                model.addAttribute("ErrorStringCode",result);
                return "view_user_detail";
            }
            else{
                return "redirect:/users";
            }
        }
        else{
            return "redirect:/users";
        }
    }
}
