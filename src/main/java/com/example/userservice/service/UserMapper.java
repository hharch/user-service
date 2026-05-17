package com.example.userservice.service;

import com.example.userservice.dto.RoleResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.AppUser;
import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(AppUser user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toCollection(TreeSet::new));
        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toCollection(TreeSet::new));
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                user.getCreatedAt(),
                roles,
                permissions
        );
    }

    public RoleResponse toRoleResponse(Role role) {
        Set<String> permissions = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toCollection(TreeSet::new));
        return new RoleResponse(role.getId(), role.getName(), permissions);
    }
}
