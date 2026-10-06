package com.senai.simulado_saep.services;

import com.senai.simulado_saep.entity.Usuario;
import com.senai.simulado_saep.enums.PerfilUsuario;
import com.senai.simulado_saep.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            if (usuarioRepository.existsByUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("Username já existe: " + usuario.getUsername());
            }
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            Usuario existente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            if (!existente.getUsername().equals(usuario.getUsername())
                    && usuarioRepository.existsByUsername(usuario.getUsername())) {
                throw new IllegalArgumentException("Username já existe: " + usuario.getUsername());
            }
            if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            } else {
                usuario.setSenha(existente.getSenha());
            }
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void criarUsuariosIniciais() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("Administrador", "admin", "admin123", PerfilUsuario.ADMIN);
            Usuario operador = new Usuario("Operador", "operador", "operador123", PerfilUsuario.OPERADOR);
            salvar(admin);
            salvar(operador);
        }
    }
}
