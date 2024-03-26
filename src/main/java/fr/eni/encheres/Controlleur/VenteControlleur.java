package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    public VenteControlleur(EnchereService enchereService,UtilisateurService utilisateurService,CategorieService categorieService)
    {
        this.enchereService = enchereService;
        this.utilisateurService = utilisateurService;
        this.categorieService = categorieService;
    }
    public CUtilisateur UtilisateurConnecte;

    @ModelAttribute("membreEnSession")
    public CUtilisateur MembreAuthenticate(Authentication authentication, HttpSession session) {
        UtilisateurConnecte = null;
        session.removeAttribute("membreEnSession");
        if (authentication != null && authentication.isAuthenticated()) {
            UtilisateurConnecte = utilisateurService.getUtilisateurByEmail(authentication.getName());
            if(UtilisateurConnecte != null && UtilisateurConnecte.getNoUtilisateur() > 0){
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    UtilisateurConnecte.setAdministrateur(1);
                } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
                    UtilisateurConnecte.setAdministrateur(2);
                } else {
                    UtilisateurConnecte.setAdministrateur(0);
                }
                session.setAttribute("membreEnSession", UtilisateurConnecte);
                return UtilisateurConnecte;
            } else {
                UtilisateurConnecte = null;
                Logger.log("Trace_ERROR.log","Controlleur Enchere: MembreAuthenticate null");
                return null;
            }
        } else {
            Logger.log("Trace_ERROR.log","Controlleur Enchere: MembreAuthenticate return null");
            return null;
        }
    }
    @ModelAttribute("CategorieSession")
    public List<CCategorie> chargerSession() {
        System.out.println("liste de categorie");
        return categorieService.ListCategorie();
    }
    @GetMapping("/create")
    public String getCreateArticleVendu(Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : getCreateEncheres ");
        CArticleVendu ArticleVendu = new CArticleVendu();
        CRetrait retrait = new CRetrait();
        model.addAttribute("retrait",retrait);
        model.addAttribute("ArticleVendu",ArticleVendu);
        model.addAttribute("isCreate",true);
        model.addAttribute("postValue","/sale/create");
        return "view_sale";
    }
    @GetMapping("/modify")
    public String getModifyArticleVendu(@RequestParam(name = "id", required = true) int id, Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : getModifyEncheres ");
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
        Logger.log("Trace_ENI.log","VenteControlleur : getAnnuleArticleVendu ");
        CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
        model.addAttribute("ArticleVendu",ArticleVendu);
        model.addAttribute("postValue","/sale/cancel");
        return "view_sale";
    }

    @PostMapping("/create")
    public String postArticleVenduCreate(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                         @RequestParam("upload") MultipartFile uploadFile,
                                         @ModelAttribute("Retrait") CRetrait Retrait,
                                     BindingResult bindingResult, HttpSession session, Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : postArticleVenduCreate ");
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "sale/create";
        } else {
            if(UtilisateurConnecte.isActive()) {
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

                ArticleVendu.setPhoto(relativePath);
                ArticleVendu.setRetrait(Retrait);
                ArticleVendu.setVendeur(UtilisateurConnecte);
                String result = enchereService.vendreArticle(ArticleVendu);
                if(result != ErrorCode.NO_ERROR){
                    model.addAttribute("ErrorStringCode",result);
                    return "view_sale";
                }
                else{
                    return "redirect:/bid";
                }
            }
        }
        return "redirect:/bid";
    }

    @PostMapping("/modify")
    public String postArticleVenduModify(@Validated @ModelAttribute("ArticleVendu") CArticleVendu ArticleVendu,
                                         @ModelAttribute("Retrait") CRetrait Retrait,
                                     BindingResult bindingResult, HttpSession session, Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : postEncheresModify ");
        UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_bid_list";
        } else {
            ArticleVendu.setRetrait(Retrait);
            ArticleVendu.setVendeur(UtilisateurConnecte);
            String result = enchereService.modifierVente(ArticleVendu);
            if(result != ErrorCode.NO_ERROR){
                model.addAttribute("ErrorStringCode",result);
                return "view_sale";
            }
            else{
                return "redirect:/bid";
            }
        }
    }
    @PostMapping("/cancel")
    public String postArticleVenduDelete( @ModelAttribute("idAnnule") int id, Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : postEncheresDelete ");
        String result = enchereService.annulerVente(id);
        if(result != ErrorCode.NO_ERROR){
            model.addAttribute("ErrorStringCode",result);
            return "view_sale";
        }
        else{
            return "redirect:/bid";
        }
    }

    @GetMapping("/upload")
    public String getuploadArticleVendu(@RequestParam(name = "idPhoto", required = true) int id,
                                        Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : getuploadArticleVendu ");
        return "redirect:/bid";
    }

    @PostMapping("/upload")
    public String postuploadArticleVendu(@RequestParam("idPhoto") int id,
                                         @RequestParam("upload") MultipartFile uploadFile, Model model) {
        Logger.log("Trace_ENI.log","VenteControlleur : postuploadArticleVendu ");

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
        String result = enchereService.ajouterPhotoVente(articleVendu);
        if(result != ErrorCode.NO_ERROR){
            model.addAttribute("ErrorStringCode",result);
            return "view_sale";
        }
        else{
            return "redirect:/bid";
        }
    }
}
