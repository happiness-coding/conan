package com.happiness.conan.web.dto;

import com.happiness.conan.domain.model.Label;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Label data")
public class LabelDTO {
    @Schema(description = "Label ID", example = "1")
    private Long id;

    @Schema(description = "Label name", example = "Bug")
    private String name;

    @Schema(description = "Label color in HEX format", example = "#FF0000")
    private String color;

    public static LabelDTO fromEntity(Label label) {
        if (label == null) {
            return null;
        }
        return LabelDTO.builder()
                .id(label.getId())
                .name(label.getName())
                .color(label.getColor())
                .build();
    }
}