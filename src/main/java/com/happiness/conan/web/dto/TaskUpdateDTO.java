package com.happiness.conan.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for updating an existing task")
public class TaskUpdateDTO {
    @Schema(description = "Updated task title", example = "Implement improved user authentication")
    private String title;

    @Schema(description = "Updated task description", example = "Implement OAuth2 authentication with JWT tokens and refresh token support")
    private String description;

    @Schema(description = "Updated due date", example = "2025-04-15T12:00:00")
    private LocalDateTime dueDate;

    @Schema(description = "Updated priority level", example = "high", allowableValues = {"low", "medium", "high"})
    private String priority;

    @Schema(description = "Updated completion status", example = "true")
    private Boolean isCompleted;

    @Schema(description = "Updated list of label IDs", example = "[{\"id\": 1}, {\"id\": 3}, {\"id\": 7}]")
    private List<LabelDTO> labels;}