package com.dwes.todo.task.service;

import com.dwes.todo.task.dto.TaskResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TodoRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${todo.rest.url:https://todo-rest-production-0496.up.railway.app}")
    private String apiBaseUrl;

    // 👇 Este método ya no recibe parámetros, usa credenciales fijas
    public List<TaskResponseDto> getTasks() {
        String username = "pepe";
        String password = "12345";

        System.out.println("📡 Llamando a API con usuario: " + username);
        System.out.println("📡 URL: " + apiBaseUrl);

        String url = apiBaseUrl + "/task";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                System.out.println("❌ Error en la respuesta: " + response.getStatusCode());
                return List.of();
            }
        } catch (Exception e) {
            System.out.println("❌ Excepción al llamar a la API: " + e.getMessage());
            return List.of();
        }
    }
}

