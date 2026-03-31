package com.dwes.todo.shared.init;


import com.dwes.todo.category.model.Category;
import com.dwes.todo.category.model.CategoryRepository;
import com.dwes.todo.task.dto.CreateTaskRequest;
import com.dwes.todo.task.service.TaskService;
import com.dwes.todo.user.dto.CreatedUserRequest;
import com.dwes.todo.user.model.User;
import com.dwes.todo.user.model.UserRole;
import com.dwes.todo.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeed {

    private final CategoryRepository categoryRepository;
    private final TaskService taskService;
    private final UserService userService;

    @PostConstruct
    public void init() {

        List<User> users =insertUsers();
        insertCategories();
        insertTasks(users.get(0));
    }

    /*
    Solamente devuelve aquellos que son UserRole.USER
    para poder usarlos como autores de Task
     */
    private List<User> insertUsers() {
        List<User> result = new ArrayList<>();

        CreatedUserRequest req = CreatedUserRequest.builder()
                .username("user")
                .email("user@user.com")
                .password("1234")
                .verifyPassword("1234")
                .fullname("The user")
                .build();
        User user = userService.registerUser(req);
        result.add(user);

        CreatedUserRequest req2 = CreatedUserRequest.builder()
                .username("admin")
                .email("admin@dwes.com")
                .password("1234")
                .verifyPassword("1234")
                .fullname("Administrador")
                .build();
        User user2 = userService.registerUser(req2);

        userService.changeRole(user2, UserRole.ADMIN);

        return result;
    }

    private void insertCategories() {
        categoryRepository.save(Category.builder().title("Main").build());
    }

    private void insertTasks(User author) {

        CreateTaskRequest req1 = CreateTaskRequest.builder()
                .title("First task!")
                .description("Lorem ipsum dolor sit amet")
                .tags("tag1, tag2, tag3")
                .build();

        taskService.createTask(req1, author);

        CreateTaskRequest req2 = CreateTaskRequest.builder()
                .title("Second task!")
                .description("Lorem ipsum dolor sit amet")
                .tags("tag1, tag2, tag4")
                .build();

        taskService.createTask(req2, author);

    }
}
