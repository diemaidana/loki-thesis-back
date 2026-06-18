package com.loki.tesis.user.service;

import com.loki.tesis.user.dto.UserResponseDTO;
import com.loki.tesis.user.dto.UserUpdateDTO;
import com.loki.tesis.user.entity.User;
import com.loki.tesis.user.enums.AccountStatus;
import com.loki.tesis.user.exception.UserAlreadyInactiveException;
import com.loki.tesis.user.exception.UserNotFoundException;
import com.loki.tesis.user.mapper.UserMapper;
import com.loki.tesis.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Esto hace que por defecto todos los metodos sean de solo lectura, a menos que se especifique lo contrario.
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PersistenceContext
    private EntityManager entityManager;


    public UserResponseDTO getUserByUuid(UUID uuid) {
        User user = findUserEntityByUuid(uuid);
        return userMapper.toUserResponseDTO(user);
    }

    // Creo un nuevo usuario
    @Transactional
    public User createUser(User user) {
        //System.out.println(user.getCreatedAt() + " " + user.getUuid().toString());
        User saved = userRepository.save(user);//  userRepository.saveAndFlush(user);
        //entityManager.refresh(saved);
        System.out.println(user.getCreatedAt().toString() + " " + user.getUuid().toString());

        return saved;
    }

    /* hago un Update del Usuario */
    @Transactional
    public UserResponseDTO updateUser(UUID uuid, UserUpdateDTO  userUpdateDTO) {
        User user = findUserEntityByUuid(uuid);
        userMapper.updateUserFromDTO(userUpdateDTO, user);

        userRepository.save(user);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional
    public void deleteUser(UUID uuid) {
        User user = findUserEntityByUuid(uuid);

        if (user.getStatus() == AccountStatus.INACTIVE) {
            throw new UserAlreadyInactiveException("El usuario con UUID " + uuid + " ya esta inactivo");
        }
        user.setStatus(AccountStatus.INACTIVE); // Aca hacemos la baja logica.
        userRepository.save(user);
    }

    /* Funciones privadas */

    private User findUserEntityByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("El usuario con UUID " + uuid + " no fue encontrado"));
    }

}
