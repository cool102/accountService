package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class Security extends WebSecurityConfigurerAdapter {
    @Autowired
    UserInfoDetailsService userInfoDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userInfoDetailsService)
                .passwordEncoder(getEncoder());


    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers(HttpMethod.PUT, "/api/admin/user/role").hasRole("Administrator")
                .mvcMatchers(HttpMethod.DELETE, "/api/admin/user").hasRole("Administrator")
                .mvcMatchers(HttpMethod.GET, "/api/admin/user").hasRole("Administrator")

                .mvcMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("Accountant")

                .mvcMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("Accountant")
                .mvcMatchers("/api/auth/signup").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("User", "Accountant")
                .mvcMatchers("/api/empl/payment").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyRole("User",
                        "Accountant", "Administrator")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no session
                .and()
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .httpBasic(); // (3)
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }


}
