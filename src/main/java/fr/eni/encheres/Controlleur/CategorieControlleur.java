package fr.eni.encheres.Controlleur;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.CCategorie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/category")
public class CategorieControlleur {
    private final CategorieService categorieService;

    public CategorieControlleur(CategorieService categorieService) {
        this.categorieService = categorieService;
    }
    @GetMapping("/")
    public String getCategorie(Model model) {
        model.addAttribute("isNotModify",true);
        return "Categorie";
    }
    @GetMapping("/delete")
    public String getDeleteCategorie(@RequestParam(name = "id", required = true) int id) {
        categorieService.DeleteCategorie(id);
        return "redirect:/Categorie";
    }
    @GetMapping("/modify")
    public String getModifyCategorie(@RequestParam(name = "id", required = true) int id, Model model) {
        CCategorie Categorie = categorieService.SearchCategorie(id);
        model.addAttribute("genreId", Categorie);
        model.addAttribute("isNotModify",false);
        return "/Categorie";
    }
    @PostMapping("/modify")
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
    @PostMapping("/create")
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
}
