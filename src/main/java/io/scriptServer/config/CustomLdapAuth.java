package io.scriptServer.config;

import io.scriptServer.model.User;
import io.scriptServer.repository.UserRepository;
import io.scriptServer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
@RequiredArgsConstructor
public class CustomLdapAuth implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(CustomLdapAuth.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Value("#{new Boolean('${enableLdapAuth:false}')}")
    private Boolean enableLdapAuth;
    @Value("${ldapUrl}")
    private String ldapUrl;
    @Value("${ldapAuthBase}")
    private String ldapAuthBase;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (!enableLdapAuth || !ldapAuth(authentication.getName(), authentication.getCredentials().toString())) {
            logger.info("Skipping ldap auth.");
            return null;
        }

        User user = getUserRolesOrCreateNew(authentication);

        return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), userService.getAuthorities(user));

    }

    public User getUserRolesOrCreateNew(Authentication authentication) {
        User user = userRepository.findByUsernameEquals(authentication.getName());
        if (user != null) {
            logger.error("Ldap user found in database. Changing user password to LDAP");
            userService.changeUserPassword(user, authentication.getCredentials().toString());
        } else {
            user = userService.createUser(authentication.getName(), authentication.getCredentials().toString());
        }
        return user;
    }

    public Boolean ldapAuth(String username, String password) {
        if (ldapUrl == null || ldapAuthBase == null) {
            throw new IllegalStateException("Ldap is not set up correctly!");
        }
        String dn = "uid=" + username + "," + ldapAuthBase;

        // Setup environment for authenticating

        Hashtable<String, String> environment =
                new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, ldapUrl);
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

