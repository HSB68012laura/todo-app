package com.dwes.todo.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Boolean completed;
    private String priority;
    private String deadline;
    private String createdAt;
    private AuthorDto  author;
    private Long categoryId;
    private String categoryName;
    private List<String> tagNames;
    private String tags;
    private String username;
}
