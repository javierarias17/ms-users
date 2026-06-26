package co.com.pragma.api;

import co.com.pragma.api.dto.UserInDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.validator.ValidationHandler;
import co.com.pragma.usecase.createuser.CreateUserUseCase;
import co.com.pragma.usecase.getuser.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UserMapper userMapper;
    private final ValidationHandler validationHandler;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserInDto.class)
                .flatMap(validationHandler::validate)
                .map(userMapper::toModel)
                .flatMap(createUserUseCase::execute)
                .map(userMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return getUserUseCase.execute(id)
                .map(userMapper::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
