package com.ts.saude.service.impl;

import com.ts.saude.model.Role;
import com.ts.saude.model.User;
import com.ts.saude.repository.RoleRepository;
import com.ts.saude.repository.UserRepository;
import com.ts.saude.service.UserService;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void initDefaultRolesAndAdmin() {
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
        if (roleRepository.findByName("ROLE_RECEPTION").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_RECEPTION"));
        }
        if (roleRepository.findByName("ROLE_DOCTOR").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_DOCTOR"));
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrador");
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
        }
    }

    @Override
    public User save(User user) {
        if (user.getId() != null) {
            // Atualização: buscar usuário atual no banco
            User existente = userRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (user.getPassword() == null || user.getPassword().isBlank()) {
                user.setPassword(existente.getPassword()); // mantém a senha atual
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword())); // nova senha
            }
        } else {
            // Novo usuário
            if (user.getPassword() == null || user.getPassword().isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória para novo usuário");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Método necessário para autenticação no Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities);
    }
}
