package com.myshop.controller;

import com.myshop.dto.LoginDto;
import com.myshop.dto.RegisterDto;
import com.myshop.dto.TokenResponseDto;
import com.myshop.dto.UserDto;
import com.myshop.global.context.TokenContext;
import com.myshop.global.context.TokenContextHolder;
import com.myshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/join")
    public void join(@RequestBody RegisterDto registerDto) {
        authService.join(registerDto);
    }

    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }

}