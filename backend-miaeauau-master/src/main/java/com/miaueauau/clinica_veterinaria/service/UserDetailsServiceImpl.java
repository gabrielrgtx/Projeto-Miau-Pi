// src/main/java/com/miaueauau/clinica_veterinaria/service/UserDetailsServiceImpl.java
package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.User; // Importa sua entidade User
import com.miaueauau.clinica_veterinaria.repository.UserRepository; // Importa UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Para Collections.singletonList
import java.util.List;

@Service // Este é o UserDetailsService principal que o Spring Security vai usar
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Injete UserRepository para buscar o User

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // --- INÍCIO DOS PRINTS PARA DEBUG ---
        System.out.println("DEBUG: Tentando autenticar o e-mail: " + email);
        // --- FIM DOS PRINTS PARA DEBUG ---

        // Busca o User diretamente pelo e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: Usuário NÃO encontrado com o e-mail: " + email);
                    return new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email);
                });

        System.out.println("DEBUG: Usuário encontrado: " + user.getEmail());
        System.out.println("DEBUG: Senha do usuário (do banco): '" + user.getPassword() + "'");
        System.out.println("DEBUG: Role do usuário: " + user.getRole().name()); // Loga a role

        // Converte a role do seu User (UserRole) para GrantedAuthority do Spring Security
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().name()) // Ex: "ROLE_ADMIN", "ROLE_TUTOR"
        );

        // Retorna um objeto UserDetails do Spring Security
        // Senha em texto plano, para NoOpPasswordEncoder
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}