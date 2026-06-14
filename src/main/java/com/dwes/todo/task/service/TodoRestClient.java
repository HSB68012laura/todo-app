package com.dwes.todo.task.service;

import com.dwes.todo.task.dto.CategoryDto;
import com.dwes.todo.task.dto.CreateTaskRequest;
import com.dwes.todo.task.dto.TaskResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TodoRestClient {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HttpSession session;

    @Value("${todo.rest.url:http://localhost:8080}")
    private String apiBaseUrl;

    private String getUsername() {
        return (String) session.getAttribute("username");
    }
    private String getPassword() {
        return (String) session.getAttribute("password");
    }

    public Map<String, Object> getDashboard() {
        String url = apiBaseUrl + "/task/dashboard";
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<Map<String, Object>> response =restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error al obtener dashboard: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String username = getUsername();
        String password = getPassword();
        if (username != null && password != null) {
            headers.setBasicAuth(username, password);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // GET /task - Listar todas las tareas
    public List<TaskResponseDto> getTasks() {
        String url = apiBaseUrl + "/task";

        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            System.out.println("Respuesta código: " + response.getStatusCode());
            System.out.println("Tareas recibidas: " + (response.getBody() != null ? response.getBody().size() : 0));
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // GET /task/{id} - Obtener una tarea por ID
    public TaskResponseDto getTaskById(Long id) {
        String url = apiBaseUrl + "/task/" + id;
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TaskResponseDto.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error al obtener tarea " + id + ": " + e.getMessage());
            return null;
        }
    }

    // POST /task - Crear una nueva tarea
    public TaskResponseDto createTask(CreateTaskRequest request) {
        String url = apiBaseUrl + "/task";

        // Si el request no tiene priority, asignar "MEDIA" por defecto
        if (request.getPriority() == null || request.getPriority().isEmpty()) {
            request.setPriority("MEDIA");
        }

        HttpEntity<CreateTaskRequest> entity = new HttpEntity<>(request, createAuthHeaders());

        try {
            ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity(url, entity, TaskResponseDto.class);
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error al crear tarea: " + e.getMessage());
            return null;
        }
    }

    // PUT /task/{id} - Editar una tarea
    public TaskResponseDto updateTask(Long id, CreateTaskRequest request) {
        String url = apiBaseUrl + "/task/" + id;
        if (request.getDeadline() == null) {
            request.setDeadline(LocalDateTime.now().plusDays(7));
        }
        if (request.getCategoryId() == null || request.getCategoryId() == -1) {
            request.setCategoryId(null);
        }
        HttpEntity<CreateTaskRequest> entity = new HttpEntity<>(request, createAuthHeaders());

        try {
            restTemplate.put(url, entity);
            return getTaskById(id);
        } catch (Exception e) {
            System.out.println("Error al editar tarea " + id + ": " + e.getMessage());
            return null;
        }
    }

    // DELETE /task/{id} - Eliminar una tarea
    public boolean deleteTask(Long id) {
        String url = apiBaseUrl + "/task/" + id;
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar tarea " + id + ": " + e.getMessage());
            return false;
        }
    }

    public List<CategoryDto> getCategories() {
        String url = apiBaseUrl + "/categories";
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<List<CategoryDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error al obtener categorías: " + e.getMessage());
            return List.of();
        }
    }

    public List<TaskResponseDto> searchTasks(String title, String priority, Boolean completed, Long categoryId, Long tagId) {
        String url = apiBaseUrl + "/task/search?";

        if (title != null && !title.isEmpty()) url += "title=" + title + "&";
        if (priority != null && !priority.isEmpty()) url += "priority=" + priority + "&";
        if (completed != null) url += "completed=" + completed + "&";
        if (categoryId != null) url += "categoryId=" + categoryId + "&";
        if (tagId != null) url += "tagId=" + tagId + "&";

        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error en búsqueda: " + e.getMessage());
            return List.of();
        }
    }

    public List<TaskResponseDto> getOverdueTasks() {
        String url = apiBaseUrl + "/task/overdue";
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        try {
            ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error obtener tareas vencidas: " + e.getMessage());
            return List.of();
        }
    }

}