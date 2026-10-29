package com.example.homestay.web;

import com.example.homestay.web.form.LoginForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.display-name}")
    private String displayName;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("adminUser") != null) {
            return "redirect:/";
        }
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginForm loginForm,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (adminUsername.equals(loginForm.getUsername())
                && adminPassword.equals(loginForm.getPassword())) {
            session.setAttribute("adminUser", displayName);
            String target = (String) session.getAttribute("loginRedirect");
            session.removeAttribute("loginRedirect");
            return "redirect:" + (target == null || !target.startsWith("/") ? "/" : target);
        }
        redirectAttributes.addFlashAttribute("loginError", "账号或密码不正确");
        redirectAttributes.addFlashAttribute("loginForm", loginForm);
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
