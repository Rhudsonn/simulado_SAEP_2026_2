package com.senai.simulado_saep.enums;

public enum PerfilUsuario {
    OPERADOR("Operador de Almoxarifado"),
    ADMIN("Administrador do Sistema");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
