package fr.eni.encheres.Controlleur;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginControlleur {
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false) String error,
                        @RequestParam(name = "logoutSuccess", required = false) String logoutSuccess,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("logoutSuccess", logoutSuccess);
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            SecurityContextHolder.clearContext();
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "Vous avez été déconnecté avec succès.");
        return "login";
    }
}
