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

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private CustomLdapAuth customLdapAuth;

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
                .dataSource(dataSource)
                .and()
                .authenticationProvider(customLdapAuth);
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
                // Base admin page
                .antMatchers("/admin/**").hasAuthority("ADMIN_PAGE_USAGE")
                .antMatchers("/admin/update_scripts").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/scripts/**").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/scripts").hasAuthority("SCRIPTS_UPDATE")
                .antMatchers("/admin/roles").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/users").hasAuthority("ROLES_SETTING")
                // Users
                .antMatchers("/admin/update_user_roles").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/invite_user").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/delete_user").hasAuthority("ROLES_SETTING")
                // Roles
                .antMatchers("/admin/update_role").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/delete_role").hasAuthority("ROLES_SETTING")
                .antMatchers("/admin/create_role").hasAuthority("ROLES_SETTING")
                // Server control
                .antMatchers("/admin/server_control").hasAuthority("SERVER_CONTROL")
                // Files
                .antMatchers("/files/control").hasAuthority("FILES_UPLOAD")
                .antMatchers("/files/get_all").permitAll()
                .antMatchers("/files/get_all_for_select").permitAll()
                .antMatchers("/files/upload_file").hasAuthority("FILES_UPLOAD")
                .antMatchers("/files/edit_file").hasAuthority("FILES_EDIT")
                .antMatchers("/files/delete_file").hasAuthority("FILES_EDIT")
                // Base endpoints
                .antMatchers("/api/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/vendor/**").permitAll()
                .antMatchers("/login*").permitAll()
                .antMatchers("/user/resetPassword").anonymous()
                .antMatchers("/user/changePassword").anonymous()
                .antMatchers("/user/savePassword").anonymous()
                .antMatchers("/index/**").authenticated()
                .antMatchers("/Planner/**").authenticated()
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