package com.ts.saude.service;

import com.ts.saude.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    Optional<User> findById(Long id);

    void deleteById(Long id);
}
