package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.CCategorie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/Category")
public class CategorieControlleur {
    private final CategorieService categorieService;

    public CategorieControlleur(CategorieService categorieService) {

        this.categorieService = categorieService;
    }


    @ModelAttribute("CategorieSession")
    public List<CCategorie> chargerSession() {
        System.out.println("liste de categorie");
        return categorieService.ListCategorie();
    }

    @GetMapping
    public String getCategorie(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCategorie ");
        model.addAttribute("isNotModify",true);
    return "Categorie";

    }
    @GetMapping("/Delete")
    public String getDeleteCategorie(@RequestParam(name = "id", required = true) int id) {
        Logger.log("Trace_ENI.log","Controlleur : getDeleteCategorie ");
        categorieService.DeleteCategorie(id);
        return "redirect:/Category";
    }
    @GetMapping("/Create")
    public String getCreateCategorie(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateCategorie ");
        CCategorie categorie = new CCategorie();
        model.addAttribute("CategorieId", categorie);
        model.addAttribute("isNotModify",true);
        return "redirect:/Category";
    }
    @GetMapping("/Modify")
    public String getModifyCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyCategorie ");
        CCategorie Categorie = categorieService.SearchCategorie(id);
        model.addAttribute("CategorieId", Categorie);
        model.addAttribute("isNotModify",false);
        return "/Categorie";
    }
    @PostMapping("/Modify")
    public String postModifyCategorie(@RequestParam(name = "id", required = true) int id,
                                      @RequestParam(name = "libelle", required = true) String libelle) {
        Logger.log("Trace_ENI.log","Controlleur : postModifyCategorie ");
        CCategorie categorie = new CCategorie();
        categorie.setNoCategorie(id);
        categorie.setLibelle(libelle);
            categorieService.ModifyCategorie(categorie);
            return "redirect:/Category";
    }
    @PostMapping("/Create")
    public String postCategorieCreate(@RequestParam(name = "libelle", required = true) String Categorie) {
        Logger.log("Trace_ENI.log","Controlleur : postCategorieCreate ");
        CCategorie categorie = new CCategorie();
        categorie.setLibelle(Categorie);
            categorieService.CreateCategorie(categorie);
            return "redirect:/Category";
    }

    @PostMapping("/Delete")
    public String postCategorieDelete(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                      BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postCategorieDelete ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "Categorie";
        } else {
            categorieService.DeleteCategorie(Categorie.getNoCategorie());
            return "redirect:/Category";
        }
    }
}
