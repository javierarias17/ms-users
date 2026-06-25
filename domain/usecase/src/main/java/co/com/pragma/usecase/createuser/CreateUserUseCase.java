package co.com.pragma.usecase.createuser;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepository userRepository;

    public Mono<User> execute(User user) {
        return userRepository.save(user);
    }
}
