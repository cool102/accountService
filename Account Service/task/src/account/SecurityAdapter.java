package account;

import account.errors.CustomAccessDeniedHandler;
import account.errors.LoginFailureHandler;
import account.errors.LoginSuccessHandler;
import account.errors.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    UserInfoDetailsService userInfoDetailsService;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth


                .userDetailsService(userInfoDetailsService)
                .passwordEncoder(getEncoder());


    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(restAuthenticationEntryPoint).and()

                .csrf().disable().headers().frameOptions().disable()
                .and()
                .authorizeRequests()

                .mvcMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAuthority("Role_Administrator".toUpperCase())
                .mvcMatchers(HttpMethod.PUT, "/api/admin/user/access").hasAuthority("Role_Administrator".toUpperCase())
                .mvcMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("Role_Administrator".toUpperCase())
                .mvcMatchers(HttpMethod.PUT, "/api/acct/payments").hasAuthority("Role_Accountant".toUpperCase())
                .mvcMatchers(HttpMethod.POST, "/api/acct/payments").hasAuthority("Role_Accountant".toUpperCase())
                .mvcMatchers("/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority("Role_User".toUpperCase(), "Role_Accountant".toUpperCase())
                .mvcMatchers(HttpMethod.GET, "/api/security/events").hasAuthority("ROLE_AUDITOR")
                .mvcMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyAuthority("ROLE_USER", "Role_Accountant".toUpperCase(), "Role_Administrator".toUpperCase())
                .mvcMatchers(HttpMethod.GET, "/api/admin/user").hasAuthority("Role_Administrator".toUpperCase())
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session

        ;


    }


}
