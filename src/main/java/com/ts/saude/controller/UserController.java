package com.ts.saude.controller;

import com.ts.saude.model.MedicoAgenda;
import com.ts.saude.model.Role;
import com.ts.saude.model.User;
import com.ts.saude.repository.RoleRepository;
import com.ts.saude.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    // Listar todos os usuários
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    // Exibir formulário para novo usuário
    @GetMapping("/new")
    public String showUserForm(Model model) {
        User user = new User();
        user.setMedicoAgenda(new MedicoAgenda()); // inicializa para não ser null

        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/form";
    }

    // Salvar novo usuário
    @PostMapping
    public String saveUser(@Valid User user,
            BindingResult result,
            @RequestParam(value = "rolesSelected", required = false) List<Long> rolesSelected,
            @RequestParam(value = "availableDays", required = false) List<String> availableDays,
            @RequestParam(value = "availableHours", required = false) List<String> availableHours,
            @RequestParam(value = "appointmentDuration", required = false) Integer appointmentDuration,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/form";
        }

        // Carregar roles selecionadas
        Set<Role> roles = new HashSet<>();
        if (rolesSelected != null) {
            for (Long roleId : rolesSelected) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new IllegalArgumentException("Role inválida: " + roleId));
                roles.add(role);
            }
        }
        user.setRoles(roles);

        boolean isDoctor = roles.stream().anyMatch(role -> role.getName().equals("ROLE_DOCTOR"));
        if (isDoctor) {
            MedicoAgenda agenda = new MedicoAgenda();
            agenda.setDiasAtendimento(availableDays);
            agenda.setHorariosDisponiveis(availableHours);
            agenda.setDuracaoConsulta(appointmentDuration);
            agenda.setMedico(user);
            user.setMedicoAgenda(agenda); // importantíssimo para persistência em cascata
        }

        userService.save(user);
        return "redirect:/users";
    }

    // Exibir formulário de edição
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/form";
    }

    // Atualizar usuário
    @PostMapping("/{id}")
    public String updateUser(@PathVariable("id") Long id,
            @Valid User user,
            BindingResult result,
            @RequestParam(value = "rolesSelected", required = false) List<Long> rolesSelected,
            Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/form";
        }

        // Carregar roles selecionadas
        Set<Role> roles = new HashSet<>();
        if (rolesSelected != null) {
            for (Long roleId : rolesSelected) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new IllegalArgumentException("Role inválida: " + roleId));
                roles.add(role);
            }
        }
        user.setId(id);
        user.setRoles(roles);

        userService.save(user);
        return "redirect:/users";
    }

    // Excluir usuário
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
