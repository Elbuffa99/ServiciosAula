package com.aulas;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBcrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String passwordPlano = "Macros32*";
        String hashEnBD = "$2a$10$huE97ZjXCqKIOzqEuTivxemYPI5PXtoOqLKRBRkk7lYLrXvUaXbha";

        boolean coincide = encoder.matches(passwordPlano, hashEnBD);
        System.out.println("¿El hash corresponde al password? " + coincide);

        // Generamos uno nuevo a propósito para comparar
        String hashNuevo = encoder.encode(passwordPlano);
        System.out.println("Hash recién generado: " + hashNuevo);
        System.out.println("¿El nuevo hash matchea? " + encoder.matches(passwordPlano, hashNuevo));
    }
}