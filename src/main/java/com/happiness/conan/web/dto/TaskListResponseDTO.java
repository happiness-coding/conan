package com.happiness.conan.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response containing a list of tasks with pagination information")
public class TaskListResponseDTO {
    @Schema(description = "List of tasks")
    private List<TaskDTO> tasks;

    @Schema(description = "Total number of tasks matching the filter criteria", example = "42")
    private long total;

    @Schema(description = "Current page number", example = "1")
    private int page;

    @Schema(description = "Number of tasks per page", example = "10")
    private int limit;
}