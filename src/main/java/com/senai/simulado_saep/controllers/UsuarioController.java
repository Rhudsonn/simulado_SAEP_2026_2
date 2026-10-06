package com.senai.simulado_saep.controllers;

import com.senai.simulado_saep.entity.Usuario;
import com.senai.simulado_saep.enums.PerfilUsuario;
import com.senai.simulado_saep.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfis", PerfilUsuario.values());
        model.addAttribute("titulo", "Novo Usuário");
        return "usuario/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return usuarioService.buscarPorId(id)
                .map(u -> {
                    u.setSenha(""); // não exibir senha
                    model.addAttribute("usuario", u);
                    model.addAttribute("perfis", PerfilUsuario.values());
                    model.addAttribute("titulo", "Editar Usuário");
                    return "usuario/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Usuário não encontrado");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("perfis", PerfilUsuario.values());
            model.addAttribute("titulo", usuario.getId() == null ? "Novo Usuário" : "Editar Usuário");
            return "usuario/form";
        }
        try {
            usuarioService.salvar(usuario);
            ra.addFlashAttribute("sucesso", "Usuário salvo com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.excluir(id);
            ra.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}

