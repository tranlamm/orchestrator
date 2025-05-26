package com.lamt2.orchestrator.controller;

import com.lamt2.orchestrator.exception.MissingFieldException;
import com.lamt2.orchestrator.model.security.CustomUserDetails;
import com.lamt2.orchestrator.request.RequestLogin;
import com.lamt2.orchestrator.response.BaseResponse;
import com.lamt2.orchestrator.service.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {
    @Value("${spring.security.cookie}")
    private String cookieName;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Operation(
            summary = "Login",
            description = "Login operation"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Request missing field"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Wrong username or password"
    )
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<BaseResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request login body",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RequestLogin.class)
                    )
            )
            @Valid @RequestBody RequestLogin requestLogin,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            throw new MissingFieldException(errors);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestLogin.getUsername(), requestLogin.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Wrong username or password!");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long duration = requestLogin.isRememberMe() ? Duration.ofDays(3).toMillis() : Duration.ofHours(1).toMillis();
        String token = jwtService.generateToken(customUserDetails, duration);
        ResponseCookie responseCookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, String> params = new HashMap<>();
        params.put("redirectUrl", "/home");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(new BaseResponse(params));
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/home/train")
    public String homeTrainPage() {
        return "home_train";
    }

    @GetMapping("/home/done")
    public String homeDonePage() {
        return "home_done";
    }
}
