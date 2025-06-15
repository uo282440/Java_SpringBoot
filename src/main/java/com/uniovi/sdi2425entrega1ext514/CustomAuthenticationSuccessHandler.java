package com.uniovi.sdi2425entrega1ext514;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().toString();

        if (role.contains("ROLE_ADMIN")) {
            response.sendRedirect("/user/list");

        } else if (role.contains("ROLE_STANDARD")) {
            response.sendRedirect("/path/list");

        } else {
            response.sendRedirect("/");
        }
    }


}
