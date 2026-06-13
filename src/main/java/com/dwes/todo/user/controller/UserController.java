package com.dwes.todo.user.controller;

import com.dwes.todo.user.dto.CreatedUserRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class UserController {

    @Value("${todo.rest.url:http://localhost:8080}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    public UserController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        System.out.println("=== LOGIN INTENTADO ===");
        System.out.println("Usuario: " + username);
        System.out.println("API URL: " + apiUrl);

        try{
            String url = apiUrl + "/auth/validate";
            System.out.println("URL completa: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(username, password);
            System.out.println("Headers creados");

            HttpEntity<?> entity = new HttpEntity<>(headers);
            System.out.println("Entity creado");

            System.out.println("Antes de restTemplate.exchange...");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);
            System.out.println("Después de exchange");

            System.out.println("Código respuesta: " + response.getStatusCode());
            System.out.println("Respuesta body: " + response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                session.setAttribute("username", username);
                session.setAttribute("password", password);
                System.out.println("Login OK, redirigiendo a /task");
                return "redirect:/task";
            }
        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("Error de autenticación", ex.getMessage());
        }
        System.out.println("Login fallido, redirigiendo a /login");
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new CreatedUserRequest());
        model.addAttribute("apiUrl", apiUrl);
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(value = "fullname", required = false) String fullname,
            RedirectAttributes redirectAttributes) {

        try {
            String url = apiUrl + "/auth/register";

            Map<String, String> userData = Map.of(
                    "username", username,
                    "email", email,
                    "password", password
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(userData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al registrar usuario");
            }

            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/register";
        }
    }
}