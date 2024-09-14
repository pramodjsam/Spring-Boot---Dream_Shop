package com.pramod.dreamshops.service.user;

import com.pramod.dreamshops.dto.UserDto;
import com.pramod.dreamshops.exception.AlreadyExistsException;
import com.pramod.dreamshops.exception.ResourceNotFoundException;
import com.pramod.dreamshops.model.User;
import com.pramod.dreamshops.repository.UserRepository;
import com.pramod.dreamshops.request.CreateUserRequest;
import com.pramod.dreamshops.request.UserUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request).filter(u -> !this.userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(this.passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            return this.userRepository.save(user);
        }).orElseThrow(() -> new AlreadyExistsException(request.getEmail() + " already exists"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return this.userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return this.userRepository.save(existingUser);
        }).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        this.userRepository.findById(userId).ifPresentOrElse(entity -> this.userRepository.delete(entity), () -> {
            throw new ResourceNotFoundException("User not found");
        });
    }

    @Override
    public UserDto convertUserToDto(User user){
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return this.userRepository.findByEmail(email);
    }
}
