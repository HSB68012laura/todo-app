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

    public List<TaskResponseDto> getTasks(String username, String password) {
        String url = apiBaseUrl + "/task";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<TaskResponseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<TaskResponseDto>>() {}
        );

        return response.getBody();
    }
}

