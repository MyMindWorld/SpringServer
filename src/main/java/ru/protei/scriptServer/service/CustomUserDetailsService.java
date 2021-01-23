package ru.protei.scriptServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsernameEquals(username);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
                    username, " ", true, true, true, true,
                    getAuthorities(Collections.singletonList(roleRepository.findByNameEquals("ROLE_USER"))));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(), true, true,
                true, getAuthorities(user.getRoles()));
    }

    private List<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream().flatMap(role -> role.getPrivileges().stream()).collect(Collectors.toList()).stream().map(Privilege::getName).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}

