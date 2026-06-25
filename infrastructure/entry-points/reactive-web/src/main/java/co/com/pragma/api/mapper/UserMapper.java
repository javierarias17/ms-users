package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserInDto;
import co.com.pragma.api.dto.UserOutDto;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toModel(UserInDto inDto);

    UserOutDto toResponse(User user);
}
