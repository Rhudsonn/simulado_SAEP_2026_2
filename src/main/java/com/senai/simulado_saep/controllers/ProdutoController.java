package com.senai.simulado_saep.controllers;

import com.senai.simulado_saep.entity.Produto;
import com.senai.simulado_saep.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Produto> produtos = produtoService.listarComFiltros(
                nome, dataInicio, dataFim,
                PageRequest.of(page, size, Sort.by("nome").ascending()));

        model.addAttribute("produtos", produtos);
        model.addAttribute("nome", nome);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produtos.getTotalPages());
        return "produto/lista";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("titulo", "Novo Produto");
        return "produto/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return produtoService.buscarPorId(id)
                .map(p -> {
                    model.addAttribute("produto", p);
                    model.addAttribute("titulo", "Editar Produto");
                    return "produto/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("erro", "Produto não encontrado");
                    return "redirect:/produtos";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Produto produto, BindingResult result,
                         Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", produto.getId() == null ? "Novo Produto" : "Editar Produto");
            return "produto/form";
        }
        produtoService.salvar(produto);
        ra.addFlashAttribute("sucesso", "Produto salvo com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/excluir/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        try {
            produtoService.excluir(id);
            ra.addFlashAttribute("sucesso", "Produto excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }
}

