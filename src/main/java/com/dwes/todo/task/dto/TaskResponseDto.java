package com.dwes.todo.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String priority;
    private String deadline;
    private String createdAt;
    private String author;
}
