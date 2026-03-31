package com.dwes.todo.task.dto;

import com.dwes.todo.tag.model.Tag;
import com.dwes.todo.task.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EditTaskRequest extends CreateTaskRequest{

    private Long id;
    private boolean completed;
    private LocalDateTime createdAt;
    private String username;


    public static EditTaskRequest of(Task task) {
        return EditTaskRequest.builder()
                .id(task.getId())
                .completed(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .username(task.getAuthor().getFullname())
                .title(task.getTitle())
                .description(task.getDescription())
                .categoryId(task.getCategory().getId())
                .tags(task.getTags().stream().map(Tag::getText).collect(Collectors.joining(", ")))
                .build();
    }

}
