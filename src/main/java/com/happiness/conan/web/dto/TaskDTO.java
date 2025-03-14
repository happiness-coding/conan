package com.happiness.conan.web.dto;

import com.happiness.conan.domain.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Task data with all details")
public class TaskDTO {
    @Schema(description = "Task ID", example = "1")
    private Long id;

    @Schema(description = "Task title", example = "Implement user authentication")
    private String title;

    @Schema(description = "Task description", example = "Implement OAuth2 authentication with JWT tokens")
    private String description;

    @Schema(description = "Task due date", example = "2025-04-01T12:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Task priority level", example = "high")
    private String priority;

    @Schema(description = "Task completion status", example = "false")
    private boolean isCompleted;

    @Schema(description = "Labels associated with the task")
    private List<LabelDTO> labels;

    @Schema(description = "Task creation timestamp", example = "2025-03-14T06:20:45")
    private LocalDateTime createdAt;

    @Schema(description = "Task last update timestamp", example = "2025-03-14T06:20:45")
    private LocalDateTime updatedAt;

    public static TaskDTO fromEntity(Task task) {
        if (task == null) {
            return null;
        }

        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .priority(task.getPriority().name())
                .isCompleted(task.isCompleted())
                .labels(task.getLabels().stream().map(LabelDTO::fromEntity).collect(Collectors.toList()))
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}