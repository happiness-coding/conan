package com.happiness.conan.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data transfer object for creating a new task")
public class TaskCreateDTO {
    @Schema(description = "Task title", example = "Implement user authentication", required = true)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Task detailed description", example = "Implement OAuth2 authentication with JWT tokens")
    private String description;

    @Schema(description = "Due date for the task", example = "2025-04-01T12:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Task priority level", example = "high", allowableValues = {"low", "medium", "high"})
    private String priority;

    @JsonProperty("isCompleted") // Add this annotation
    @Schema(description = "Whether the task is completed", example = "false", defaultValue = "false")
    private Boolean isCompleted;

    @Schema(description = "IDs of labels associated with the task", example = "[1, 3, 5]")
    private List<LabelDTO> labels;
}