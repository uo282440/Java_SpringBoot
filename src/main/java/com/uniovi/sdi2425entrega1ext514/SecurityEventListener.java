package com.uniovi.sdi2425entrega1ext514;

import com.uniovi.sdi2425entrega1ext514.services.LogService;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener implements ApplicationListener<ApplicationEvent> {

    private final LogService logService;

    public SecurityEventListener(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AuthenticationSuccessEvent successEvent) {

            Authentication authentication = successEvent.getAuthentication();

            if (authentication.getPrincipal() instanceof UserDetails) {
                String username = authentication.getName();
                logService.saveLog(username, "LOGIN-EX", "Inicio de sesión exitoso");
            }

        } else if (event instanceof AuthenticationFailureBadCredentialsEvent failureEvent) {

            Authentication authentication = failureEvent.getAuthentication();
            String username = (String) authentication.getPrincipal();
            logService.saveLog(username, "LOGIN-ERR", "Intento de inicio de sesión fallido: " + username);
        }
    }
}
