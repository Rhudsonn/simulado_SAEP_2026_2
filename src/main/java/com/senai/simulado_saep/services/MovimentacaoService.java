package com.senai.simulado_saep.services;

import com.senai.simulado_saep.entity.Movimentacao;
import com.senai.simulado_saep.entity.Produto;
import com.senai.simulado_saep.entity.Usuario;
import com.senai.simulado_saep.enums.TipoMovimentacao;
import com.senai.simulado_saep.exception.EstoqueInsuficienteException;
import com.senai.simulado_saep.repositories.MovimentacaoRepository;
import com.senai.simulado_saep.repositories.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public Page<Movimentacao> listarTodas(Pageable pageable) {
        return movimentacaoRepository.findAllByOrderByDataHoraDesc(pageable);
    }

    @Transactional
    public Movimentacao registrar(Long produtoId, TipoMovimentacao tipo, Integer quantidade,
                                  String observacao, Usuario usuario) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (tipo == TipoMovimentacao.SAIDA) {
            if (quantidade > produto.getQuantidadeEstoque()) {
                throw new EstoqueInsuficienteException(
                        produto.getQuantidadeEstoque(), quantidade);
            }
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        } else {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        }

        produtoRepository.save(produto);

        Movimentacao mov = new Movimentacao();
        mov.setProduto(produto);
        mov.setTipo(tipo);
        mov.setQuantidade(quantidade);
        mov.setObservacao(observacao);
        mov.setUsuarioResponsavel(usuario);

        return movimentacaoRepository.save(mov);
    }
}

