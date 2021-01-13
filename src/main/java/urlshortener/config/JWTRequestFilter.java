package urlshortener.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import io.jsonwebtoken.ExpiredJwtException;
import urlshortener.service.SecureUserService;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    @Autowired
    private SecureUserService jwtUserDetailsService;

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    /**
     * Filter all petitions to retrive a JSON Web Token to
     * identify a user
     *
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String username = null;
        String jwtToken = jwtTokenUtil.getRequestToken(request);

        if (jwtToken != null) {
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                logger.warn("JWT: Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                logger.warn("JWT: Token has expired");
            }
        } else {
            logger.info("JWT is not present or does not begin with Bearer String");
        }

        validateToken(username, jwtToken, request);
        chain.doFilter(request, response);
    }


    /**
     * Check that the JWT is valid: is not expired
     * and is attached to an user
     *
     * @param username
     * @param jwtToken
     * @param request
     */

    private void validateToken(String username, String jwtToken, HttpServletRequest request) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

            // If token is valid configure Spring Security to manually set authentication
            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                logger.info("User authorized with role: " + userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
    }

}