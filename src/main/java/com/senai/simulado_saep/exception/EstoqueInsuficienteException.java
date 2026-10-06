package com.senai.simulado_saep.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    private final int disponivel;
    private final int solicitado;

    public EstoqueInsuficienteException(int disponivel, int solicitado) {
        super(String.format(
                "Saída não permitida: estoque insuficiente. Disponível: %d. Solicitado: %d.",
                disponivel, solicitado));
        this.disponivel = disponivel;
        this.solicitado = solicitado;
    }

    public int getDisponivel() {
        return disponivel;
    }

    public int getSolicitado() {
        return solicitado;
    }
}
