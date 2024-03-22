package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.FileUploadUtil;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/sale")
@SessionAttributes({ "membreEnSession" })
public class VenteControlleur {

    private final EnchereService enchereService;
    private final UtilisateurService utilisateurService;
    private final CategorieService categorieService;
    private CUtilisateur UtilisateurConnecte;
    public VenteControlleur(EnchereService enchereService,UtilisateurService utilisateurService,CategorieService categorieService)
    {
        this.enchereService = enchereService;
        this.utilisateurService = utilisateurService;
        this.categorieService = categorieService;
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

    @ModelAttribute("CategorieSession")
    public List<CCategorie> chargerSession() {
        System.out.println("liste de categorie");
        return categorieService.ListCategorie();
    }
    @GetMapping("/create")
    public String getCreateArticleVendu(Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getCreateEncheres ");
        CArticleVendu ArticleVendu= new CArticleVendu();
        CRetrait retrait = new CRetrait();
        model.addAttribute("retrait",retrait);
        model.addAttribute("ArticleVendu",ArticleVendu);
        model.addAttribute("isCreate",true);
        model.addAttribute("postValue","/sale/create");
        return "view_sale";
    }
    @GetMapping("/modify")
    public String getModifyArticleVendu(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getModifyEncheres ");
        CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
        CRetrait retrait = enchereService.SearchRetraitByArticleID(id);
        model.addAttribute("retrait",retrait);
        model.addAttribute("ArticleVendu",ArticleVendu);
        model.addAttribute("isCreate",false);
        model.addAttribute("postValue","/sale/modify");
        String imageArticle = enchereService.SearchPhotoByArticleId(id);
        model.addAttribute("imageArticle","./../" + imageArticle);
        return "view_sale";
    }
    @GetMapping("/cancel")
    public String getAnnuleArticleVendu(@RequestParam(name = "idAnnule", required = true) int id,Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getAnnuleArticleVendu ");
        CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
        model.addAttribute("ArticleVendu",ArticleVendu);
        model.addAttribute("postValue","/sale/cancel");
        return "view_sale";
    }

    @PostMapping("/create")
    public String postArticleVenduCreate(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                         @ModelAttribute("Retrait") CRetrait Retrait,
                                     BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postArticleVenduCreate " +ArticleVendu );
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "sale/create";
        } else {
            if(UtilisateurConnecte.isActive()) {
                ArticleVendu.setRetrait(Retrait);
                ArticleVendu.setVendeur(UtilisateurConnecte);
                enchereService.vendreArticle(ArticleVendu);
            }
            return "redirect:/bid";
        }
    }

    @PostMapping("/modify")
    public String postArticleVenduModify(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                         @ModelAttribute("Retrait") CRetrait Retrait,
                                     BindingResult bindingResult) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresModify ");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_bid_list";
        } else {
            ArticleVendu.setRetrait(Retrait);
            ArticleVendu.setVendeur(UtilisateurConnecte);
            enchereService.modifierVente(ArticleVendu);
            return "redirect:/bid";
        }
    }
    @PostMapping("/cancel")
    public String postArticleVenduDelete( @ModelAttribute("idAnnule") int id) {
        Logger.log("Trace_ENI.log","Controlleur : postEncheresDelete ");
            enchereService.annulerVente(id);
            return "redirect:/bid";
    }

    @GetMapping("/upload")
    public String getuploadArticleVendu(@RequestParam(name = "idPhoto", required = true) int id,
                                        Model model) {
        Logger.log("Trace_ENI.log","Controlleur : getuploadArticleVendu ");
        return "redirect:/bid";
    }

    @PostMapping("/upload")
    public String postuploadArticleVendu(@RequestParam("idPhoto") int id,
                                         @RequestParam("upload") MultipartFile uploadFile) {
        Logger.log("Trace_ENI.log","Controlleur : postuploadArticleVendu " + id + uploadFile);

        String fileName = StringUtils.cleanPath(uploadFile.getOriginalFilename());

        String relativePath = "/images/" + fileName;

        try {
            // Récupération du dossier images dans les ressources statiques
            File imagesFolder = new ClassPathResource("static/images").getFile();
            // Création d'un nouveau fichier dans le dossier images
            File imageFile = new File(imagesFolder, fileName);
            // Transfert du fichier téléchargé vers le nouveau fichier dans le dossier images
            uploadFile.transferTo(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CArticleVendu articleVendu = enchereService.AfficherArticleById(id);
        articleVendu.setPhoto(relativePath);
        enchereService.ajouterPhotoVente(articleVendu);

        return "redirect:/bid";
    }
}
