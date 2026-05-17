package com.example.userservice.service;

import com.example.userservice.dto.AssignRolesRequest;
import com.example.userservice.dto.RoleRequest;
import com.example.userservice.dto.RoleResponse;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.entity.AppUser;
import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.repository.PermissionRepository;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       UserRepository userRepository,
                       UserMapper userMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(userMapper::toRoleResponse)
                .toList();
    }

    @Transactional
    public RoleResponse upsertRole(RoleRequest request) {
        String roleName = Role.normalizeName(request.name());
        Role role = roleRepository.findByName(roleName).orElseGet(() -> new Role(roleName));
        role.setPermissions(resolvePermissions(request.permissions()));
        return userMapper.toRoleResponse(roleRepository.save(role));
    }

    @Transactional
    public UserResponse assignRoles(Long userId, AssignRolesRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Set<Role> roles = request.roles().stream()
                .map(Role::normalizeName)
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public Role ensureRole(String name, Set<String> permissions) {
        String roleName = Role.normalizeName(name);
        Role role = roleRepository.findByName(roleName).orElseGet(() -> new Role(roleName));
        role.setPermissions(resolvePermissions(permissions));
        return roleRepository.save(role);
    }

    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        if (permissionNames == null) {
            return Set.of();
        }
        return permissionNames.stream()
                .map(this::normalizePermission)
                .filter(permissionName -> !permissionName.isBlank())
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .orElseGet(() -> permissionRepository.save(new Permission(permissionName))))
                .collect(Collectors.toSet());
    }

    private String normalizePermission(String permissionName) {
        return permissionName == null ? "" : permissionName.trim().toUpperCase();
    }
}
