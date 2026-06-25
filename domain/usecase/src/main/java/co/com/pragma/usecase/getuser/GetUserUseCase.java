package co.com.pragma.usecase.getuser;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUserUseCase {
    private final UserRepository userRepository;

    public Mono<User> execute(Long id) {
        return userRepository.findById(id);
    }
}
