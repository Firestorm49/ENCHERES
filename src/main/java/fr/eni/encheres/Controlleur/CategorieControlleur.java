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

@Controller
@RequestMapping("/Category")
public class CategorieControlleur {
    private final CategorieService categorieService;

    public CategorieControlleur(CategorieService categorieService) {
        this.categorieService = categorieService;
    }
    @GetMapping
    public String getCategorie() {
    return "Categorie";

    }
    @GetMapping("/Delete")
    public String getDeleteCategorie(@RequestParam(name = "id", required = true) int id) {
        categorieService.DeleteCategorie(id);
        return "redirect:/Categorie";
    }
    @GetMapping("/Create")
    public String getCreateCategorie(Model model) {
        CCategorie categorie = new CCategorie();
        model.addAttribute("categorie",categorie);
        return "redirect:/Categorie";
    }
    @GetMapping("/Modify")
    public String getModifyCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        CCategorie Categorie = categorieService.SearchCategorie(id);
        model.addAttribute("genreId", Categorie);
        model.addAttribute("isNotModify",false);
        return "/Categorie";
    }
    @PostMapping("/Modify")
    public String postModifyCategorie(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "Categorie";
        } else {
            categorieService.ModifyCategorie(Categorie);
            return "redirect:/Categorie";
        }
    }
    @PostMapping("/Create")
    public String postCategorieCreate(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "Categorie";
        } else {
            categorieService.CreateCategorie(Categorie);
            return "redirect:/Categorie";
        }
    }

    @PostMapping("/Delete")
    public String postCategorieDelete(@Validated @ModelAttribute("Categorie") CCategorie Categorie,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "Categorie";
        } else {
            categorieService.DeleteCategorie(Categorie.getNoCategorie());
            return "redirect:/Categorie";
        }
    }
}
