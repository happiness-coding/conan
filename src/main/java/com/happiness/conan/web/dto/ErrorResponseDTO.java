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
@Schema(description = "Error response")
public class ErrorResponseDTO {
    @Schema(description = "Error details")
    private Error error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Error information")
    public static class Error {
        @Schema(description = "Error code", example = "VALIDATION_ERROR")
        private String code;

        @Schema(description = "Error message", example = "Validation failed")
        private String message;

        @Schema(description = "Detailed error information")
        private List<ErrorDetail> details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Detailed error information")
    public static class ErrorDetail {
        @Schema(description = "Field name with error", example = "title")
        private String field;

        @Schema(description = "Error message for the field", example = "Title is required")
        private String message;
    }
}