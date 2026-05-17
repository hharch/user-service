package com.example.userservice.controller;

import com.example.userservice.dto.AssignRolesRequest;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.dto.RoleResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.RoleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final RoleService roleService;

    public AdminController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public List<RoleResponse> listRoles() {
        return roleService.listRoles();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public RoleResponse upsertRole(@Valid @RequestBody RoleRequest request) {
        return roleService.upsertRole(request);
    }

    @PutMapping("/users/{userId}/roles")
    @PreAuthorize("hasAuthority('USER_ROLE_WRITE')")
    public UserResponse assignRoles(@PathVariable Long userId, @Valid @RequestBody AssignRolesRequest request) {
        return roleService.assignRoles(userId, request);
    }
}
