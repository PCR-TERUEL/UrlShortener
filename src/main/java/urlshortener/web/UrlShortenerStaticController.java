package urlshortener.web;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UrlShortenerStaticController {

    public UrlShortenerStaticController() { }

    @GetMapping("/error")
    public ModelAndView error() {
        return new ModelAndView("forward:/error_no.html");
    }

    @Operation(summary = "Check if user is logged in and give correspondent html")
    @GetMapping(value = "/login")
    public ModelAndView login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails) {
            return new ModelAndView("redirect:/panel");
        }

        return new ModelAndView("forward:/userlogin.html");
    }

}
