package io.scriptServer.service;

import io.scriptServer.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@AllArgsConstructor
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private UserService userService;

    private RoleService roleService;


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userService.findByUsernameEquals(username);
        if (user == null) {
            return new org.springframework.security.core.userdetails.User(
                    username, " ", true, true, true, true,
                    roleService.getAuthorities(roleService.findByNameEquals("ROLE_USER")));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), user.isEnabled(), true, true,
                true, roleService.getAuthorities(user.getRoles()));
    }


}

