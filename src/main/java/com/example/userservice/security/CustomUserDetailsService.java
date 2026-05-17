package com.example.userservice.security;

import com.example.userservice.entity.AppUser;
import com.example.userservice.entity.Permission;
import com.example.userservice.entity.Role;
import com.example.userservice.repository.UserRepository;
import java.util.Collection;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isEnabled())
                .authorities(authorities(user))
                .build();
    }

    private Collection<GrantedAuthority> authorities(AppUser user) {
        return user.getRoles().stream()
                .flatMap(this::roleAuthorities)
                .distinct()
                .toList();
    }

    private Stream<GrantedAuthority> roleAuthorities(Role role) {
        Stream<GrantedAuthority> roleAuthority = Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        Stream<GrantedAuthority> permissionAuthorities = role.getPermissions().stream()
                .map(Permission::getName)
                .map(SimpleGrantedAuthority::new);
        return Stream.concat(roleAuthority, permissionAuthorities);
    }
}
