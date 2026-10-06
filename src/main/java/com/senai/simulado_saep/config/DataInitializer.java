package com.senai.simulado_saep.config;


import com.senai.simulado_saep.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public DataInitializer(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        usuarioService.criarUsuariosIniciais();
    }
}
