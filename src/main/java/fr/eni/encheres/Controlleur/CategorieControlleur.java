package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.CCategorie;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/category")
public class CategorieControlleur {
    private final CategorieService categorieService;

    public CategorieControlleur(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @ModelAttribute("CategorieSession")
    public List<CCategorie> chargerSession() {
        return categorieService.ListCategorie();
    }

    @GetMapping
    public String getCategorie(Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : getCategorie ");
        model.addAttribute("isNotModify",true);
        return "view_categorie";

    }
    @GetMapping("/delete")
    public String getDeleteCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : getDeleteCategorie ");
        String result = categorieService.DeleteCategorie(id);
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","CategorieControlleur: getDeleteCategorie - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_categorie";
        }
        else{
            return "redirect:/category";
        }
    }
    @GetMapping("/create")
    public String getCreateCategorie(Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : getCreateCategorie ");
        CCategorie categorie = new CCategorie();
        model.addAttribute("CategorieId", categorie);
        model.addAttribute("isNotModify",true);
        return "redirect:/category";
    }
    @GetMapping("/modify")
    public String getModifyCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : getModifyCategorie ");
        CCategorie Categorie = categorieService.SearchCategorie(id);
        model.addAttribute("CategorieId", Categorie);
        model.addAttribute("isNotModify",false);
        return "view_categorie";
    }
    @PostMapping("/modify")
    public String postModifyCategorie(@RequestParam(name = "id", required = true) int id,
                                      @RequestParam(name = "libelle", required = true) String libelle, Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : postModifyCategorie ");
        CCategorie categorie = new CCategorie();
        categorie.setNoCategorie(id);
        categorie.setLibelle(libelle);
        String result = categorieService.ModifyCategorie(categorie);
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","CategorieControlleur: postModifyCategorie - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_categorie";
        }
        else{
            return "redirect:/category";
        }
    }
    @PostMapping("/create")
    public String postCategorieCreate(@Validated @RequestParam(name = "libelle", required = true) String Categorie, Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : postCategorieCreate ");
        CCategorie categorie = new CCategorie();
        categorie.setLibelle(Categorie);
        String result = categorieService.CreateCategorie(categorie);
        if(result != ErrorCode.NO_ERROR){
            Logger.log("Trace_ERROR.log","CategorieControlleur: postCategorieCreate - " + result);
            model.addAttribute("ErrorStringCode",result);
            return "view_categorie";
        }
        else{
            return "redirect:/category";
        }
    }

    @PostMapping("/delete")
    public String postCategorieDelete(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                      BindingResult bindingResult, Model model) {
        Logger.log("Trace_ENI.log","CategorieControlleur : postCategorieDelete ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_categorie";
        } else {
            String result = categorieService.DeleteCategorie(Categorie.getNoCategorie());
            if(result != ErrorCode.NO_ERROR){
                Logger.log("Trace_ERROR.log","CategorieControlleur: postCategorieDelete - " + result);
                model.addAttribute("ErrorStringCode",result);
                return "view_categorie";
            }
            else{
                return "redirect:/category";
            }
        }
    }
}
