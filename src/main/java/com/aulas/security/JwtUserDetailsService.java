package com.aulas.security;

import com.aulas.model.Usuario;
import com.aulas.repo.IUsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final IUsuarioRepo repo;

    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        Usuario usuario = repo.findByCorreoConRol(correoInstitucional)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + correoInstitucional));

        // Prefijo ROLE_ es la convención de Spring Security
        // Sin él, @PreAuthorize("hasRole('ADMIN')") NO funciona
        String nombreRol = usuario.getRol().getNombreRol().toUpperCase();
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + nombreRol)
        );

        return new User(
                usuario.getCorreoInstitucional(),
                usuario.getContrasena(),
                authorities
        );
    }
}