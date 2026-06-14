package com.dwes.todo.task.controller;

import com.dwes.todo.task.dto.CategoryDto;
import com.dwes.todo.task.dto.CreateTaskRequest;
import com.dwes.todo.task.dto.TaskResponseDto;
import com.dwes.todo.task.service.TodoRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TodoRestClient todoRestClient;


    //index
    @GetMapping("/")
    public String index(Model model) {
        return "redirect:/login";
    }


    // Listar todas las tareas
    @GetMapping
    public String listTasks(Model model) {
        List<TaskResponseDto> tasks = todoRestClient.getTasks();
        List<CategoryDto> categories = todoRestClient.getCategories();

        Map<String, Object> dashboard = todoRestClient.getDashboard();

        model.addAttribute("taskList", tasks);
        model.addAttribute("categoryList", categories);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("newTask", new CreateTaskRequest());
        return "task-list";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(@PathVariable Long id, @ModelAttribute("taskRequest") CreateTaskRequest request) {
        todoRestClient.updateTask(id, request);
        return "redirect:/task";
    }

    // Mostrar formulario para crear nueva tarea
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("newTask", new CreateTaskRequest());
        model.addAttribute("categoryList", todoRestClient.getCategories());
        return "task-form";
    }

    // Crear nueva tarea
    @PostMapping("/submit")
    public String createTask(@ModelAttribute CreateTaskRequest request) {
        System.out.println("Creando task");
        System.out.println("Title: " + request.getTitle());
        System.out.println("Description: " + request.getDescription());
        System.out.println("Priority: " + request.getPriority());

        todoRestClient.createTask(request);
        return "redirect:/task";
    }

    // Ver detalle de una tarea
    @GetMapping("/{id}")
    public String viewTask(@PathVariable Long id, Model model) {
        TaskResponseDto task = todoRestClient.getTaskById(id);
        model.addAttribute("task", task);
        return "view-task";
    }

    // Mostrar formulario para editar tarea
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        TaskResponseDto task = todoRestClient.getTaskById(id);
        model.addAttribute("task", task);

        List<CategoryDto> categories = todoRestClient.getCategories();
        model.addAttribute("categoryList", categories);

        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(task.getTitle());
        request.setDescription(task.getDescription());
        request.setPriority(task.getPriority());
        request.setCategoryId(task.getCategoryId());
        model.addAttribute("taskRequest", request);

        return "show-task";
    }

    // Cambiar a completado
    @GetMapping("/{id}/toggle")
    public String toggleTask(@PathVariable Long id) {
        TaskResponseDto task = todoRestClient.getTaskById(id);
        if (task != null) {
            System.out.println("   Completed antes: " + task.getCompleted());
            task.setCompleted(!task.getCompleted());
            System.out.println("   Completed después: " + task.getCompleted());

            CreateTaskRequest request = new CreateTaskRequest();
            request.setTitle(task.getTitle());
            request.setDescription(task.getDescription());
            request.setPriority(task.getPriority());
            request.setCategoryId(task.getCategoryId());
            request.setCompleted(task.getCompleted());

            todoRestClient.updateTask(id, request);
        }
        return "redirect:/task";
    }

    // Eliminar tarea
    @PostMapping("/{id}/del")
    public String deleteTask(@PathVariable Long id) {
        todoRestClient.deleteTask(id);
        return "redirect:/task";
    }

    //Buscar tarea
    @GetMapping("/search")
    public String searchTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String completed,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            Model model) {

        System.out.println("=== BÚSQUEDA ===");
        System.out.println("title: " + title);
        System.out.println("priority: " + priority);
        System.out.println("categoryId: " + categoryId);
        System.out.println("completed: " + completed);
        System.out.println("tagId: " + tagId);

        List<TaskResponseDto> tasks;

        if ("overdue".equals(completed)) {
            tasks = todoRestClient.getOverdueTasks();
        } else  {
            Boolean completedBool = completed != null ? Boolean.parseBoolean(completed) : null;
            tasks = todoRestClient.searchTasks(title, priority, completedBool, categoryId, tagId);
        }

        List<CategoryDto> categories = todoRestClient.getCategories();
        Map<String, Object> dashboard = todoRestClient.getDashboard();

        model.addAttribute("taskList", tasks);
        model.addAttribute("categoryList", categories);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("newTask", new CreateTaskRequest());
        return "task-list";
    }

    /*private final TaskService taskService;
    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.findAll(); }

    @GetMapping ({"/", "/list", "/task"})
    public String taskList(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("taskList", taskService.findAllByUser(user));
        model.addAttribute("newTask", new CreateTaskRequest());
        return "task-list";
    }

    @GetMapping(value = {"/", "/list", "/task"}, params = "emptyListError")
    public String createTask(Model model) {
        model.addAttribute("newTask", new CreateTaskRequest());
        return "task-list";
    }

    @PostMapping("/task/submit")
    public String taskSubmit(
            @Valid @ModelAttribute("newTask") CreateTaskRequest req,
            BindingResult bindingResult,
            @AuthenticationPrincipal User author,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("taskList", taskService.findAllByUser(author));
            return "task-list";
        }

        taskService.createTask(req, author);

        return "redirect:/";
    }


    @GetMapping("/task/{id}")
    public String viewOrEditTask(@PathVariable Long id, Model model) {

        Task task = taskService.findById(id);
        EditTaskRequest editTask = EditTaskRequest.of(task);
        model.addAttribute("task", editTask);
        return "show-task";

    }

    @PostMapping("/task/edit/submit")
    public String taskEditSubmit(
            @Valid @ModelAttribute("task") EditTaskRequest req,
            BindingResult bindingResult,
            Model model) {


        if (bindingResult.hasErrors()) {
            return "show-task";
        }

        taskService.editTask(req);

        return "redirect:/";
    }

    @GetMapping("/task/{id}/toggle")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleComplete(id);
        return "redirect:/";
    }

    @PostMapping("/task/{id}/del")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return "redirect:/";
    }

    @Autowired
    private TodoRestClient todoRestClient;

    @GetMapping("/tasks-externas")
    public String getExternalTasks(Model model) {
        List<TaskResponseDto> tasks = todoRestClient.getTasks();
        model.addAttribute("tasks", tasks);
        return "task-list";
    }*/
}
