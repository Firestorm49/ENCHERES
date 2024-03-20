package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.CCategorie;
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
        System.out.println("liste de categorie");
        return categorieService.ListCategorie();
    }

    @GetMapping
    public String getCategorie(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCategorie ");
        model.addAttribute("isNotModify",true);
    return "view_categorie";

    }
    @GetMapping("/delete")
    public String getDeleteCategorie(@RequestParam(name = "id", required = true) int id) {
        Logger.log("Trace_ENI.log","Controlleur : getDeleteCategorie ");
        categorieService.DeleteCategorie(id);
        return "redirect:/category";
    }
    @GetMapping("/create")
    public String getCreateCategorie(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateCategorie ");
        CCategorie categorie = new CCategorie();
        model.addAttribute("CategorieId", categorie);
        model.addAttribute("isNotModify",true);
        return "redirect:/category";
    }
    @GetMapping("/modify")
    public String getModifyCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyCategorie ");
        CCategorie Categorie = categorieService.SearchCategorie(id);
        model.addAttribute("CategorieId", Categorie);
        model.addAttribute("isNotModify",false);
        return "view_categorie";
    }
    @PostMapping("/modify")
    public String postModifyCategorie(@RequestParam(name = "id", required = true) int id,
                                      @RequestParam(name = "libelle", required = true) String libelle) {
        Logger.log("Trace_ENI.log","Controlleur : postModifyCategorie ");
        CCategorie categorie = new CCategorie();
        categorie.setNoCategorie(id);
        categorie.setLibelle(libelle);
            categorieService.ModifyCategorie(categorie);
            return "redirect:/category";
    }
    @PostMapping("/create")
    public String postCategorieCreate(@RequestParam(name = "libelle", required = true) String Categorie) {
        Logger.log("Trace_ENI.log","Controlleur : postCategorieCreate ");
        CCategorie categorie = new CCategorie();
        categorie.setLibelle(Categorie);
            categorieService.CreateCategorie(categorie);
            return "redirect:/category";
    }

    @PostMapping("/delete")
    public String postCategorieDelete(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                      BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postCategorieDelete ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_categorie";
        } else {
            categorieService.DeleteCategorie(Categorie.getNoCategorie());
            return "redirect:/category";
        }
    }
}
