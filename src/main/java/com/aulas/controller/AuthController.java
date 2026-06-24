package com.aulas.controller;

import com.aulas.dto.LoginRequestDTO;
import com.aulas.dto.LoginResponseDTO;
import com.aulas.dto.UsuarioDTO;
import com.aulas.model.Usuario;
import com.aulas.security.JwtTokenUtil;
import com.aulas.security.JwtUserDetailsService;
import com.aulas.service.IUsuarioService;
import com.aulas.util.MapperUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final IUsuarioService usuarioService;
    private final MapperUtil mapperUtil;

    // ===== LOGIN =====
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) throws Exception {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            dto.getCorreoInstitucional(),
                            dto.getContrasena()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getCorreoInstitucional());
        String token = jwtTokenUtil.generateToken(userDetails);

        Usuario usuario = usuarioService.findByCorreoConRol(dto.getCorreoInstitucional())
                .orElseThrow();

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .expiresIn(jwtTokenUtil.getJwtTokenValidity())
                .idUsuario(usuario.getIdUsuario())
                .correoInstitucional(usuario.getCorreoInstitucional())
                .nombreUsuario(usuario.getNombreUsuario())
                .apellidoUsuario(usuario.getApellidoUsuario())
                .rol(usuario.getRol().getNombreRol())
                .build();
    }

    // ===== REGISTRO =====
    @PostMapping("/registro")
    public UsuarioDTO registrar(@Valid @RequestBody UsuarioDTO dto) throws Exception {
        Usuario entity = mapperUtil.map(dto, Usuario.class);
        Usuario saved = usuarioService.registrar(entity);
        return mapperUtil.map(saved, UsuarioDTO.class);
    }
}