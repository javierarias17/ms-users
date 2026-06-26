package co.com.pragma.api.config;

import co.com.pragma.api.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalErrorHandler implements WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalErrorHandler.class);

    private static final String VALIDATION_MESSAGE = "Validation business";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred. Please contact the administrator.";
    private static final String MESSAGE_KEY = "message";
    private static final String UNEXPECTED_ERROR_LOG = "Unexpected error: {}";

    private final ObjectMapper objectMapper;

    private final Map<Class<? extends Throwable>, BiFunction<ServerWebExchange, Throwable, Mono<Void>>> handlers = Map.of(
            ConstraintViolationException.class, this::handleConstraintViolation,
            WebExchangeBindException.class, this::handleBindException
    );

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return handlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(ex))
                .findFirst()
                .map(entry -> entry.getValue().apply(exchange, ex))
                .orElseGet(() -> handleUnexpected(exchange, ex));
    }


    private Mono<Void> handleConstraintViolation(ServerWebExchange exchange, Throwable ex) {
        var cve = (ConstraintViolationException) ex;
        List<ErrorResponseDto.FieldErrorDto> fieldErrors = cve.getConstraintViolations().stream()
                .map(v -> ErrorResponseDto.FieldErrorDto.builder()
                        .field(v.getPropertyPath().toString())
                        .message(v.getMessage())
                        .build())
                .toList();

        return writeResponse(exchange, HttpStatus.BAD_REQUEST,
                ErrorResponseDto.builder()
                        .message(VALIDATION_MESSAGE)
                        .errors(fieldErrors)
                        .build());
    }


    private Mono<Void> handleBindException(ServerWebExchange exchange, Throwable ex) {
        var bindEx = (WebExchangeBindException) ex;
        List<ErrorResponseDto.FieldErrorDto> fieldErrors = bindEx.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> ErrorResponseDto.FieldErrorDto.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();

        return writeResponse(exchange, HttpStatus.BAD_REQUEST,
                ErrorResponseDto.builder()
                        .message(VALIDATION_MESSAGE)
                        .errors(fieldErrors)
                        .build());
    }

    private Mono<Void> handleUnexpected(ServerWebExchange exchange, Throwable ex) {
        logger.error(UNEXPECTED_ERROR_LOG, ex.getMessage(), ex);
        return writeResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of(MESSAGE_KEY, UNEXPECTED_ERROR_MESSAGE));
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status, Object body) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
