package urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import urlshortener.service.SecureUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecureUserService jwtUserDetailsService;

    @Autowired
    private JWTRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
        .antMatchers("/panel", "/logout").hasRole("USER")
        .and()
        .authorizeRequests().antMatchers("/delete").hasRole("ADMIN")
        .and()
        .formLogin().loginPage("/login").defaultSuccessUrl("/panel").permitAll()
        .and().authorizeRequests().antMatchers("/authenticate", "/", "/index", "/login",
        "/singup", "/error", "error_no", "*.html", "/apidoc_files/**", "/contactform/**", "/test",
        "/css/**", "/img/**", "/js/**", "/lib/**", "/images", "/v3/**").permitAll().anyRequest().authenticated()
        .and()
        .logout().addLogoutHandler(((request, response, auth) -> {
            for (Cookie cookie : request.getCookies()) {
                String cookieName = cookie.getName();
                Cookie cookieToDelete = new Cookie(cookieName, null);
                cookieToDelete.setMaxAge(0);
                response.addCookie(cookieToDelete);
            }
        })).logoutUrl("/logout").logoutSuccessUrl("/")
        .and()
        .exceptionHandling().accessDeniedPage("/login?=unauthorized")
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
