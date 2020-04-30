package ru.protei.scriptServer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.context.WebApplicationContext;
import ru.protei.scriptServer.service.CustomUserDetailsService;

import javax.sql.DataSource;
import javax.annotation.PostConstruct;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebApplicationContext applicationContext;
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void completeSetup() {
        userDetailsService = applicationContext.getBean(CustomUserDetailsService.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .authenticationProvider(authenticationProvider())
                .jdbcAuthentication()
                .dataSource(dataSource);
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=Users,dc=protei,dc=ru") // + ldaps - [LDAP: error code 50 - Insufficient Access Rights]
//                .groupSearchBase("cn=Users,ou=Groups,dc=protei,dc=ru") // + ldaps - [LDAP: error code 50 - Insufficient Access Rights]
                .contextSource()
//                .url("ldaps://192.168.100.143/dc=protei,dc=ru")
//                .url("ldaps://192.168.100.143")
                .url("ldaps://ldap1.protei.ru")
//                .url("ldaps://ldap2.protei.ru")
//                .url("ldaps://ldap.protei.ru")
//                .url("ldap://ldap.protei.ru")
                .port(636)
                .and()
                .passwordCompare()
                .passwordEncoder(passwordEncoder())
                .passwordAttribute("userPassword");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .rememberMe()
                .tokenRepository(persistentTokenRepository())
                .userDetailsService(userDetailsService)
//                .tokenValiditySeconds(1209600) // Discuss? IMHO always - ok.
                .alwaysRemember(true)
                .and()
                .authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ADMIN_PAGE_USAGE")
                .antMatchers("/admin/update_scripts").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/scripts/**").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/scripts").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/roles").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/users").hasAuthority("ROLES_SETTING")

                .antMatchers("/admin/update_user").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/invite_user").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/delete_user").hasAuthority("ROLES_SETTING")

                .antMatchers("/admin/update_role").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/delete_role").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/create_role").hasAuthority("ROLES_SETTING")

                .antMatchers("/admin/server_control").hasAuthority("SERVER_CONTROL")
                .antMatchers("/api/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/vendor/**").permitAll()
                .antMatchers("/login*").permitAll()
                .antMatchers("/index/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .usernameParameter("username")
                .passwordParameter("password")
                .failureForwardUrl("/login?error=true")
                .defaultSuccessUrl("/index", true)
                .failureUrl("/login?error=true")
//                .failureHandler(authenticationFailureHandler())
                .and()
                .logout()
                .logoutUrl("/perform_logout")
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/ErrorCodes/403");
//        .and()
//        .oauth2Login()
//        .authorizationEndpoint()
//        .baseUri("/oauth2/authorize")
//        .and()
//        .redirectionEndpoint()
//        .baseUri("/oauth2/callback/*")
//        .and()
//        .userInfoEndpoint()
//        .userService(gitLabOAuth2UserService);
//                .logoutSuccessHandler(logoutSuccessHandler());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

}