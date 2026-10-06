package com.senai.simulado_saep.controllers;


import com.senai.simulado_saep.entity.Movimentacao;
import com.senai.simulado_saep.entity.Usuario;
import com.senai.simulado_saep.enums.TipoMovimentacao;
import com.senai.simulado_saep.exception.EstoqueInsuficienteException;
import com.senai.simulado_saep.services.MovimentacaoService;
import com.senai.simulado_saep.services.ProdutoService;
import com.senai.simulado_saep.services.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    public MovimentacaoController(MovimentacaoService movimentacaoService,
                                  ProdutoService produtoService,
                                  UsuarioService usuarioService) {
        this.movimentacaoService = movimentacaoService;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        Page<Movimentacao> movimentacoes = movimentacaoService.listarTodas(PageRequest.of(page, size));
        model.addAttribute("movimentacoes", movimentacoes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movimentacoes.getTotalPages());
        return "movimentacao/lista";
    }

    @GetMapping("/nova")
    public String novaForm(Model model) {
        model.addAttribute("produtos", produtoService.listarComFiltros(null, null, null,
                PageRequest.of(0, 1000)).getContent());
        model.addAttribute("tipos", TipoMovimentacao.values());
        return "movimentacao/form";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam Long produtoId,
                            @RequestParam TipoMovimentacao tipo,
                            @RequestParam Integer quantidade,
                            @RequestParam(required = false) String observacao,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            movimentacaoService.registrar(produtoId, tipo, quantidade, observacao, usuario);
            ra.addFlashAttribute("sucesso", "Movimentação registrada com sucesso!");
            return "redirect:/movimentacoes";
        } catch (EstoqueInsuficienteException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/movimentacoes/nova";
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/movimentacoes/nova";
        }
    }
}

