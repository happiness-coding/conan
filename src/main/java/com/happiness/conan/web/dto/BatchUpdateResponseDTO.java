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
@Schema(description = "Response after batch updating tasks")
public class BatchUpdateResponseDTO {
    @Schema(description = "List of updated tasks")
    private List<TaskDTO> tasks;

    @Schema(description = "Number of tasks successfully updated", example = "5")
    private int updatedCount;
}