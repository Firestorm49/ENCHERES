package fr.eni.encheres.Controlleur;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.CEnchere;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Encheres")
public class EnchereControlleur {
    private final EnchereService enchereService;

    public EnchereControlleur(EnchereService enchereService)
    {
        this.enchereService = enchereService;
    }

  /*  @ModelAttribute("genreSession")
    public List<Genre> chargerSession() {
        System.out.println("liste de genre");
        return filmService.consulterGenres();
    }

    @ModelAttribute("acteursSession")
    public List<Participant> chargerActeurs() {
        System.out.println("liste de acteurs");
        return filmService.consulterParticipants();
    }

    @ModelAttribute("MembreSession")
    public Membre MembreAuthenticate(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            membreConnecte	= membreService.getMemberByEmail(authentication.getName());
            if(membreConnecte.getId() > 0 && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                membreConnecte.setAdmin(true);
            }
            else {
                membreConnecte.setAdmin(false);
            }
        } else {
            membreConnecte = null;
        }
        return membreConnecte;
    }*/

    @GetMapping("/")
    public String getCreateFilm(Model model) {
        CEnchere film = new CEnchere(); // Créer un nouvel objet Film
        model.addAttribute("isNew",true);
        model.addAttribute("isNotModify",false);
        model.addAttribute("film", film);

        return "Create_Film";
    }

    @GetMapping("/Create")
    public String getFilms(Model model) {
        var films = enchereService.consulterFilms();
        model.addAttribute("films", films);
        return "films";
    }


    @GetMapping("/detail")
    public String getDetail(@RequestParam(name = "id", required = true) int id, Model model) {
        CEnchere film=enchereService.consulterFilmParId(id);
        // Rajoute au model de la vue l'objet formateur
        model.addAttribute("isNotModify",true);
        model.addAttribute("isNew",false);
        model.addAttribute("film",film);

        return "view_film";
    }

    @GetMapping("/modify")
    public String getModify(@RequestParam(name = "id", required = true) int id,Model model) {
        CEnchere film=enchereService.consulterFilmParId(id);
        model.addAttribute("film",film);
        model.addAttribute("isNotModify",false);
        model.addAttribute("isNew",false);
        return "view_film";
    }

    @GetMapping("/delete")
    public String getDelete(@RequestParam(name = "id", required = true) int id) {
        enchereService.deleteFilmParId(id);
        return "redirect:/films";
    }



    @GetMapping("/acteurs")
    public String getActeur(Model model) {
        model.addAttribute("isNotModify",true);
        return "acteurs";
    }


    @GetMapping("/acteurs/delete")
    public String getDeleteActeur(@RequestParam(name = "id", required = true) int id) {
        enchereService.deleteParticipantParId(id);
        return "redirect:/acteurs";
    }


    @GetMapping("/acteurs/modify")
    public String getModifyActeur(@RequestParam(name = "id", required = true) int id, Model model) {
        CEnchere participant = enchereService.consulterParticipantParId(id);
        model.addAttribute("acteurs", participant);
        model.addAttribute("isNotModify",false);
        return "/acteurs";
    }


    @GetMapping("/films/avis")
    public String getAvis(@RequestParam(name = "id", required = true) int id,Model model) {
        CEnchere film=enchereService.consulterFilmParId(id);
        model.addAttribute("film",film);
        model.addAttribute("isNotModify",true);
        return "view_avis";
    }




    @PostMapping("/acteurs/modify")
    public String postModifyActeur(@RequestParam(name = "id", required = true) int id,
                                   @RequestParam(name = "Nom", required = true) String Nom,
                                   @RequestParam(name = "Prenom", required = true) String Prenom)
    {
        enchereService.modifierActeurParId(id, Nom,Prenom);
        return "redirect:/acteurs";
    }


    @PostMapping("/films/avisNew")
    public String postNewAvis(@RequestParam(name = "titre", required = true) String titre,
                              @RequestParam(name = "nouvelAvis", required = true) String nouvelAvis,
                              @RequestParam(name = "note", required = true) int note) {
        CEnchere a = new CEnchere();
        a.setCommentaire(nouvelAvis);
        a.setNote(note);
        a.setMembre(membreConnecte);
        filmService.NewAvis(a,enchereService.consulterFilms().stream().filter(avis -> avis.getTitre().equals(titre)).findFirst().get().getId());
        return "redirect:/films";
    }




    @PostMapping("/acteurs/create")
    public String postNewActeur(@RequestParam(name = "Nom", required = true) String Nom,
                                @RequestParam(name = "Prenom", required = true) String Prenom)
    {
        enchereService.NewParticipant(Nom,Prenom);
        return "redirect:/acteurs";
    }


    @PostMapping("/films/detail")
    public String postFilmsModify(@Validated @ModelAttribute("film") CEnchere film,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "view_film";
        } else {
            enchereService.modifierFilmParId(film);
            return "redirect:/films";
        }
    }


    @PostMapping("/create")
    public String postFilmCreate(@Validated @ModelAttribute("Enchere") CEnchere Enchere,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "Enchere";
        } else {
            enchereService.faireEnchere(Enchere);
            return "redirect:/Enchere";
        }
    }
}
