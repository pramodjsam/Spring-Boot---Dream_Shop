package com.pramod.dreamshops.service.user;

import com.pramod.dreamshops.dto.UserDto;
import com.pramod.dreamshops.model.User;
import com.pramod.dreamshops.request.CreateUserRequest;
import com.pramod.dreamshops.request.UserUpdateRequest;

public interface IUserService {
    User getUserById(Long id);

    User createUser(CreateUserRequest request);

    User updateUser(UserUpdateRequest request, Long userId);

    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
