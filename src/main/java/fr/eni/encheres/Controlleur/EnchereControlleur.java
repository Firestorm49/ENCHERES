package fr.eni.encheres.Controlleur;

import fr.eni.encheres.Logger.Logger;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.CArticleVendu;
import fr.eni.encheres.bo.CCategorie;
import fr.eni.encheres.bo.CEnchere;
import fr.eni.encheres.bo.CUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
	private CUtilisateur UtilisateurConnecte;

	public EnchereControlleur(EnchereService enchereService,
			UtilisateurService utilisateurService,
			CategorieService categorieService) {
		this.enchereService = enchereService;
		this.utilisateurService = utilisateurService;
		this.categorieService = categorieService;
	}

	@ModelAttribute("membreEnSession")
	public CUtilisateur MembreAuthenticate(Authentication authentication) {
		if (authentication != null && authentication.isAuthenticated()) {
			UtilisateurConnecte = utilisateurService.getUtilisateurByEmail(authentication.getName());
			if (UtilisateurConnecte.getNoUtilisateur() > 0
					&& authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
				UtilisateurConnecte.setAdministrateur(1);
			} else if (UtilisateurConnecte.getNoUtilisateur() > 0 && authentication.getAuthorities().stream()
					.anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
				UtilisateurConnecte.setAdministrateur(2);
			} else {
				UtilisateurConnecte.setAdministrateur(0);
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
			/*@RequestParam(name = "numeroPage", required = false) int pageNumber,
			@RequestParam(name = "pageSize", required = false) int pageSize,*/
			Model model,
			Authentication authentication) {
		Logger.log("Trace_ENI.log", "Controlleur : getEnchere ");
		List<CArticleVendu> listArticlesVendus = new ArrayList<>();

		if (authentication != null && authentication.isAuthenticated()) {
		 listArticlesVendus = enchereService.listerEncheresConnecteByFilters(nomArticle,
					categorie != null ? categorie.intValue() : 0, UtilisateurConnecte.getNoUtilisateur(), radioButton != null ? radioButton.intValue() : 0,
					mesVentesEnCours, ventesNonCommencees, ventesTerminees, mesEncheresRemportees, mesEncheresEnCours,
					encheresOuvertes, 1, 10);
		} else {
		listArticlesVendus = enchereService.listerEncheresDeconnecteByFilters(nomArticle,
				categorie != null ? categorie.intValue() : 0, 1, 10);
		}

		model.addAttribute("encheres",listArticlesVendus);
		return "view_bid_list";
	}

	@GetMapping("/detail")
	public String getEnchere(@RequestParam(name = "id", required = true) int id,
			Model model) {
		Logger.log("Trace_ENI.log", "Controlleur : getDetailEncheres ");
		CEnchere Enchere = enchereService.afficherDetailEnchere(id);
		model.addAttribute("enchere", Enchere);
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(Enchere.getArticle().getNoArticle());
		model.addAttribute("Vente", ArticleVendu);
		CUtilisateur winner = utilisateurService.ViewProfil(enchereService.WinnerOffre(ArticleVendu.getNoArticle()));
		if (enchereService.IsVenteFinish(ArticleVendu.getNoArticle()) == 2) {
			if (winner == UtilisateurConnecte) {
				model.addAttribute("Msg_FinVente", "Vous avez remporté la vente");
			} else {
				model.addAttribute("Msg_FinVente", winner.getPseudo() + " a remporté l'enchere");
			}
		} else {
			model.addAttribute("Msg_FinVente", "Enchere toujours en cours");
		}
		return "view_bid_detail";
	}

	@GetMapping("/purpose")
	public String getProposeEncheres(@RequestParam(name = "id", required = true) int id, Model model) {
		Logger.log("Trace_ENI.log", "Controlleur : getProposeEncheres ");
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
			@ModelAttribute("Proposition") int Enchere) {
		Logger.log("Trace_ENI.log", "Controlleur : postEncheresPropose " + id + Enchere);
		CEnchere enchere = new CEnchere();
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		enchere.setMontant_enchere(Enchere);
		enchere.setArticle(ArticleVendu);
		enchere.setUtilisateur(UtilisateurConnecte);
		enchere.setDateEnchere(LocalDateTime.now());
		enchereService.faireEnchere(enchere);
		return "redirect:/bid";
	}

	@PostMapping("/detail")
	public String PostEnchere(@RequestParam(name = "id", required = true) int id,
							 Model model) {
		Logger.log("Trace_ENI.log", "Controlleur : PostEnchere ");
		CEnchere Enchere = enchereService.afficherDetailEnchere(id);
		model.addAttribute("enchere", Enchere);
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(Enchere.getArticle().getNoArticle());
		model.addAttribute("Vente", ArticleVendu);
		CUtilisateur winner = utilisateurService.ViewProfil(enchereService.WinnerOffre(ArticleVendu.getNoArticle()));
		if (enchereService.IsVenteFinish(ArticleVendu.getNoArticle()) == 2) {
			if (winner.getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vous avez remporté la vente");
			} else {
				model.addAttribute("Msg_FinVente", winner.getPseudo() + " a remporté l'enchere");
			}
		} else {
			if (ArticleVendu.getVendeur().getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vente encore en cours");
			} else {
				return "view_bid_add";
			}

		}
		return "view_bid_detail";
	}
}
