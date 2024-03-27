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
			@RequestParam(name = "mesVentesEnCours", required = false) Boolean mesVentesEnCours,
			@RequestParam(name = "ventesNonCommencees", required = false) Boolean ventesNonCommencees,
			@RequestParam(name = "ventesTerminees", required = false) Boolean ventesTerminees,
			@RequestParam(name = "mesEncheresRemportees", required = false) Boolean mesEncheresRemportees,
			@RequestParam(name = "mesEncheresEnCours", required = false) Boolean mesEncheresEnCours,
			@RequestParam(name = "encheresOuvertes", required = false) Boolean encheresOuvertes,
			@RequestParam(name = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", required = false) Integer pageSize,
			Model model, HttpSession session) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : getEnchere ");


		if (nomArticle != null && nomArticle.trim() != "") {
			session.setAttribute("nom_Article", nomArticle);
		}
		if (categorie != null) {
			session.setAttribute("dropbox", categorie);
		}
		if (pageNumber != null) {
			session.setAttribute("pageNumber", pageNumber);
		}
		if (pageSize != null) {
			session.setAttribute("pageSize", pageSize);
		}
		if (radioButton != null) {
			session.setAttribute("radioButton", radioButton);
		}
		if (mesVentesEnCours != null) {
			session.setAttribute("mesVentesEnCours", mesVentesEnCours);
		}
		if (ventesNonCommencees != null) {
			session.setAttribute("ventesNonCommencees", ventesNonCommencees);
		}
		if (ventesTerminees != null) {
			session.setAttribute("ventesTerminees", ventesTerminees);
		}
		if (mesEncheresRemportees != null) {
			session.setAttribute("mesEncheresRemportees", mesEncheresRemportees);
		}
		if (mesEncheresEnCours != null) {
			session.setAttribute("mesEncheresEnCours", mesEncheresEnCours);
		}
		if (encheresOuvertes != null) {
			session.setAttribute("encheresOuvertes", encheresOuvertes);
		}
		List<CArticleVendu> listArticlesVendus = new ArrayList<>();
		UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
		if (UtilisateurConnecte != null && UtilisateurConnecte.getNoUtilisateur() > 0) {
		 listArticlesVendus = enchereService.listerEncheresConnecteByFilters(nomArticle,
					categorie != null ? categorie.intValue() : 0, UtilisateurConnecte.getNoUtilisateur(), radioButton != null ? radioButton.intValue() : 0,
					mesVentesEnCours != null ? mesVentesEnCours.booleanValue() : false, ventesNonCommencees != null ? ventesNonCommencees.booleanValue() : false,
				 ventesTerminees != null ? ventesTerminees.booleanValue() : false, mesEncheresRemportees != null ? mesEncheresRemportees.booleanValue() : false,
				 mesEncheresEnCours != null ? mesEncheresEnCours.booleanValue() : false, encheresOuvertes != null ? encheresOuvertes.booleanValue() : false, pageNumber != null ? pageNumber.intValue() : 1, pageSize != null ? pageSize.intValue() : 5);
		} else {
		listArticlesVendus = enchereService.listerEncheresDeconnecteByFilters(nomArticle,
				categorie != null ? categorie.intValue() : 0, pageNumber != null ? pageNumber.intValue() : 1, pageSize != null ? pageSize.intValue() : 5);
		}
		Integer storedPageNumber = (Integer) session.getAttribute("pageNumber");
		Integer storedPageSize = (Integer) session.getAttribute("pageSize");
		String storedNomArticle = (String) session.getAttribute("nom_Article");
		Integer storedcategorie = (Integer) session.getAttribute("dropbox");

		Integer storedRadioButton = (Integer) session.getAttribute("radioButton");
		Boolean storedMesVentesEnCours = (Boolean) session.getAttribute("mesVentesEnCours");
		Boolean storedVentesNonCommencees = (Boolean) session.getAttribute("ventesNonCommencees");
		Boolean storedVentesTerminees = (Boolean) session.getAttribute("ventesTerminees");
		Boolean storedMesEncheresRemportees = (Boolean) session.getAttribute("mesEncheresRemportees");
		Boolean storedMesEncheresEnCours = (Boolean) session.getAttribute("mesEncheresEnCours");
		Boolean storedEncheresOuvertes = (Boolean) session.getAttribute("encheresOuvertes");

		int finalPageNumber = storedPageNumber != null ? storedPageNumber : 1;
		int finalPageSize = storedPageSize != null ? storedPageSize : 5;
		String finalNomArticle = storedNomArticle;
		int finalcategorie = storedcategorie != null ? storedcategorie : 0;

		int finalRadioButton = storedRadioButton != null ? storedRadioButton : 0;
		boolean finalMesVentesEnCours = storedMesVentesEnCours  != null ? storedMesVentesEnCours : false;
		boolean finalVentesNonCommencees = storedVentesNonCommencees  != null ? storedVentesNonCommencees :false;
		boolean finalVentesTerminees = storedVentesTerminees  != null ? storedVentesTerminees : false;
		boolean finalMesEncheresRemportees = storedMesEncheresRemportees  != null ? storedMesEncheresRemportees : false;
		boolean finalMesEncheresEnCours = storedMesEncheresEnCours  != null ? storedMesEncheresEnCours : false;
		boolean finalEncheresOuvertes = storedEncheresOuvertes != null ? storedEncheresOuvertes : false;

		model.addAttribute("pageNumber", finalPageNumber);
		model.addAttribute("pageSize", finalPageSize);
		model.addAttribute("nom_Article", finalNomArticle);
		model.addAttribute("dropbox", finalcategorie);
		model.addAttribute("encheres",listArticlesVendus);

		model.addAttribute("radioButton", finalRadioButton);
		model.addAttribute("mesVentesEnCours", finalMesVentesEnCours);
		model.addAttribute("ventesNonCommencees", finalVentesNonCommencees);
		model.addAttribute("ventesTerminees", finalVentesTerminees);
		model.addAttribute("mesEncheresRemportees",finalMesEncheresRemportees);
		model.addAttribute("mesEncheresEnCours", finalMesEncheresEnCours);
		model.addAttribute("encheresOuvertes", finalEncheresOuvertes);
		return "view_bid_list";
	}

	@GetMapping("/detail")
	public String getEnchere(@RequestParam(name = "id", required = true) int id,
			Model model, HttpSession session, RedirectAttributes attributes) {
		Logger.log("Trace_ENI.log", "EnchereControlleur : getDetailEncheres ");
		UtilisateurConnecte = (CUtilisateur) session.getAttribute("membreEnSession");
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		model.addAttribute("Vente", ArticleVendu);
		CEnchere Enchere = new CEnchere();
		Enchere.setArticle(ArticleVendu);
		int MOffre = enchereService.IsMaxOffre(Enchere);
		model.addAttribute("MOffre", MOffre);
		if (MOffre > 0) {
			CUtilisateur utilisateur = utilisateurService.ViewProfil(enchereService.IsUserMaxOffre(Enchere, MOffre));
			model.addAttribute("MOffreUser", utilisateur.getPseudo());
		} else {
			model.addAttribute("MOffreUser", null);
		}
		if (enchereService.IsVenteFinish(id) == 2) {
			if(enchereService.WinnerOffre(id) > 0){
				CUtilisateur winner = utilisateurService.ViewProfil(enchereService.WinnerOffre(id));
				model.addAttribute("winner",winner);

			if (winner.getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vous avez remporté la vente");
				model.addAttribute("Type","W");
				return "view_bid_detail";
			} else if (ArticleVendu.getVendeur().getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", winner.getPseudo() + " a remporté l'enchere");
				model.addAttribute("Type","V");
				return "view_bid_detail";
			}
			}
		} else if (enchereService.IsVenteFinish(id) == 1) {
			model.addAttribute("winner",null);
			if (ArticleVendu.getVendeur().getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				model.addAttribute("Msg_FinVente", "Vente encore en cours");
				return "view_bid_detail";
			} else {
				return "view_bid_add";
			}
		}
		else if (enchereService.IsVenteFinish(id) == 0) {
			model.addAttribute("winner",null);
			if (ArticleVendu.getVendeur().getNoUtilisateur() == UtilisateurConnecte.getNoUtilisateur()) {
				attributes.addAttribute("id", id);
				return "redirect:/sale/modify";
			}
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
		return "view_bid_add";
	}

	@GetMapping("/Encherisseurs")
	public String postEncherisseursDelete( @ModelAttribute("id") int id, Model model) {
		Logger.log("Trace_ENI.log","VenteControlleur : postEncherisseursDelete ");
		CArticleVendu ArticleVendu = enchereService.AfficherArticleById(id);
		model.addAttribute("Vente", ArticleVendu);
		List<CEnchere> Enchere = enchereService.listEncheresByArticleId(id);
		model.addAttribute("Encheres", Enchere);
		return "view_bid_list_users";

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
