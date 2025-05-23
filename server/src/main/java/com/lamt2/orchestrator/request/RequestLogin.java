package com.lamt2.orchestrator.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RequestLogin {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private boolean isRememberMe;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRememberMe() {
        return isRememberMe;
    }
}
