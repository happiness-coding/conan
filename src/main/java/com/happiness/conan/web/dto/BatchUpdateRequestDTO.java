package com.happiness.conan.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for batch updating multiple tasks")
public class BatchUpdateRequestDTO {
    @Schema(description = "List of task updates")
    @Valid
    private List<TaskBatchUpdateDTO> updates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Single task update in batch")
    public static class TaskBatchUpdateDTO {
        @Schema(description = "ID of the task to update", example = "1", required = true)
        @NotNull
        private Long id;

        @Schema(description = "Updated task details", required = true)
        @NotNull
        private TaskUpdateDTO task;
    }
}