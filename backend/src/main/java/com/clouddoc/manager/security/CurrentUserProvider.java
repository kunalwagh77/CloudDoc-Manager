package com.clouddoc.manager.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    private final String developmentPrincipal;

    public CurrentUserProvider(@Value("${app.security.development-principal:local-user}") String developmentPrincipal) {
        this.developmentPrincipal = developmentPrincipal;
    }

    public String currentUserId() {
        return developmentPrincipal;
    }
}
