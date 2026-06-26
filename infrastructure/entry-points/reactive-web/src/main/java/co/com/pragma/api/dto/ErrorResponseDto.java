package co.com.pragma.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ErrorResponseDto {
    private String message;
    private List<FieldErrorDto> errors;

    @Getter
    @Builder
    public static class FieldErrorDto {
        private String field;
        private String message;
    }
}
