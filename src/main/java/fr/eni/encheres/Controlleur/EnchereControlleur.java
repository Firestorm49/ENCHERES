package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.Tools.ErrorCode;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/bid")
@SessionAttributes({ "membreEnSession", "CategorieSession" })
public class EnchereControlleur {

	private final EnchereService enchereService;
	private final UtilisateurService utilisateurService;
	private final CategorieService categorieService;

	public EnchereControlleur(EnchereService enchereService,
			UtilisateurService utilisateurService,
			CategorieService categorieService) {
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
	@GetMapping
	public String getEnchere(@RequestParam(name = "nom_Article", required = false) String nomArticle,
							 @RequestParam(name = "dropbox", required = false) Integer categorie,
			@RequestParam(name = "radioButton", required = false) Integer radioButton,
			@RequestParam(name = "mesVentesEnCours", required = false) boolean mesVentesEnCours,
			@RequestParam(name = "ventesNonCommencees", required = false) boolean ventesNonCommencees,
			@RequestParam(name = "ventesTerminees", required = false) boolean ventesTerminees,
			@RequestParam(name = "mesEncheresRemportees", required = false) boolean mesEncheresRemportees,
			@RequestParam(name = "mesEncheresEnCours", required = false) boolean mesEncheresEnCours,
			@RequestParam(name = "encheresOuvertes", required = false) boolean encheresOuvertes,
			@RequestParam(name = "numeroPage", required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			Model model, HttpSession session) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : getEnchere ");
		List<CArticleVendu> listArticlesVendus = new ArrayList<>();
		UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
		if (UtilisateurConnecte != null && UtilisateurConnecte.getNoUtilisateur() > 0) {
		 listArticlesVendus = enchereService.listerEncheresConnecteByFilters(nomArticle,
					categorie != null ? categorie.intValue() : 0, UtilisateurConnecte.getNoUtilisateur(), radioButton != null ? radioButton.intValue() : 0,
					mesVentesEnCours, ventesNonCommencees, ventesTerminees, mesEncheresRemportees, mesEncheresEnCours,
					encheresOuvertes, pageNumber != null ? pageNumber.intValue() : 1, pageSize != null ? pageSize.intValue() : 5);
		} else {
		listArticlesVendus = enchereService.listerEncheresDeconnecteByFilters(nomArticle,
				categorie != null ? categorie.intValue() : 0, pageNumber != null ? pageNumber.intValue() : 1, pageSize != null ? pageSize.intValue() : 5);
		}

		model.addAttribute("encheres",listArticlesVendus);
		return "view_bid_list";
	}

	@GetMapping("/detail")
	public String getEnchere(@RequestParam(name = "id", required = true) int id,
			Model model, HttpSession session, RedirectAttributes attributes) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : getDetailEncheres ");
		UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		model.addAttribute("Vente", ArticleVendu);
		CUtilisateur winner = utilisateurService.ViewProfil(enchereService.WinnerOffre(id));
		if (enchereService.IsVenteFinish(id) == 2) {
			if (winner.getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vous avez remporté la vente");
				return "view_bid_detail";
			} else {
				model.addAttribute("Msg_FinVente", winner.getPseudo() + " a remporté l'enchere");
				return "view_bid_detail";
			}
		} else if (enchereService.IsVenteFinish(id) == 1) {
			if (ArticleVendu.getVendeur().getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vente encore en cours");
				return "view_bid_detail";
			} else {
				return "view_bid_add";
			}
		}
		else if (enchereService.IsVenteFinish(id) == 0) {
			attributes.addAttribute("id", id);
			return "redirect:/sale/modify";
		}
		return "redirect:/bid";
	}

	@GetMapping("/purpose")
	public String getProposeEncheres(@RequestParam(name = "id", required = true) int id, Model model) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : getProposeEncheres ");
		CEnchere Enchere = new CEnchere();
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		Enchere.setArticle(ArticleVendu);
		model.addAttribute("Vente", ArticleVendu);
		int MOffre = enchereService.IsMaxOffre(Enchere);
		model.addAttribute("MOffre", MOffre);
		if (MOffre > 0) {
			CUtilisateur utilisateur = utilisateurService.ViewProfil(enchereService.IsUserMaxOffre(Enchere, MOffre));
			model.addAttribute("MOffreUser", utilisateur.getPseudo());
		} else {
			model.addAttribute("MOffreUser", null);
		}
		String imageArticle = enchereService.SearchPhotoByArticleId(id);
		model.addAttribute("imageArticle", "./../" + imageArticle);
		return "view_bid_add";
	}

	@PostMapping("/purpose")
	public String postEncheresPropose(@ModelAttribute("id") int id,
			@ModelAttribute("Proposition") int Enchere, HttpSession session, Model model) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : postEncheresPropose ");
		UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
		CEnchere enchere = new CEnchere();
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		enchere.setMontant_enchere(Enchere);
		enchere.setArticle(ArticleVendu);
		enchere.setUtilisateur(UtilisateurConnecte);
		enchere.setDateEnchere(LocalDateTime.now());
		String result = enchereService.faireEnchere(enchere);
		if(result != ErrorCode.NO_ERROR){
			Logger.log("Trace_ERROR.log","Controlleur Enchere: postEncheresPropose - " + result);
			model.addAttribute("ErrorStringCode",result);
			return "view_bid_add";
		}
		else{
			return "redirect:/bid";
		}
	}
}
