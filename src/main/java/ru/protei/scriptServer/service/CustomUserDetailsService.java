package ru.protei.scriptServer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.UserRepository;

import javax.transaction.Transactional;

@Transactional
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsernameEquals(username);
        if (user == null) {
            // TODO Verify that when user with ldap tries to log in, exception is not thrown
            throw new UsernameNotFoundException("User " + username + " is not found!");
        }

        return user.getUserDetails();
    }


}

