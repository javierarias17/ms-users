package co.com.pragma.api;

import co.com.pragma.api.dto.UserInDto;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.usecase.createuser.CreateUserUseCase;
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
    private final UserMapper userMapper;

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserInDto.class)
                .map(userMapper::toModel)
                .flatMap(createUserUseCase::execute)
                .map(userMapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }
}
