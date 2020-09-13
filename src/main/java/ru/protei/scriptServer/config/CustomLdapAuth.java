package ru.protei.scriptServer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.controller.AdminPageController;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.UserRepository;
import ru.protei.scriptServer.service.UserService;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Component
public class CustomLdapAuth implements AuthenticationProvider {
    Logger logger = LoggerFactory.getLogger(AdminPageController.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Value("#{new Boolean('${enableLdapAuth:false}')}")
    private Boolean enableLdapAuth;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (!enableLdapAuth) {
            logger.debug("Skipping ldap auth.");
            return null;
        }

        String username = authentication.getName();
        boolean authenticated = ldapAuth(username, authentication.getCredentials().toString());


        if (authenticated) {
            List<GrantedAuthority> grantedAuths = new ArrayList<>();

            User user = userRepository.findByUsernameEquals(username);
            if (user != null) {
                logger.error("Ldap user found in database. Changing user password to LDAP");
                userService.changeUserPassword(user, authentication.getCredentials().toString());
                for (Privilege privilege : userService.getAllPrivilegesFromUser(user)) {
                    grantedAuths.add(new SimpleGrantedAuthority(privilege.getName()));
                }
            } else {
                user = userService.createUser(username, authentication.getCredentials().toString());
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), grantedAuths);
            return auth;
        } else {
            return null;
        }
    }

    public Boolean ldapAuth(String username, String password) {
        String base = "ou=Users,dc=protei,dc=ru";
        String dn = "uid=" + username + "," + base;
        String ldapURL = "ldaps://ldap1.protei.ru";

        // Setup environment for authenticating

        Hashtable<String, String> environment =
                new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, ldapURL);
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");
        environment.put(Context.SECURITY_PRINCIPAL, dn);
        environment.put(Context.SECURITY_CREDENTIALS, password);

        try {
            DirContext authContext =
                    new InitialDirContext(environment);
            logger.info("Auth from user '" + username + "' success");

            return true;

        } catch (javax.naming.AuthenticationException ex) {
            logger.error("Auth from user '" + username + "' failed");

            return false;

        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

