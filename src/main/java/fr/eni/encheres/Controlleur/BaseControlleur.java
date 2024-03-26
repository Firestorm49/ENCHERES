package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseControlleur {

    protected CUtilisateur UtilisateurConnecte;

    @Autowired
    private UtilisateurService utilisateurService;

    @ModelAttribute("membreEnSession")
    public CUtilisateur MembreAuthenticate(Authentication authentication) {
        System.out.println("MembreAuthenticate Enchere 1");
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("MembreAuthenticate Enchere 2");
            UtilisateurConnecte = utilisateurService.getUtilisateurByEmail(authentication.getName());
            if (UtilisateurConnecte != null) {
                System.out.println("MembreAuthenticate Enchere 3" + UtilisateurConnecte);
                if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                    UtilisateurConnecte.setAdministrateur(1);
                } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
                    UtilisateurConnecte.setAdministrateur(2);
                } else {
                    UtilisateurConnecte.setAdministrateur(0);
                }
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


}
