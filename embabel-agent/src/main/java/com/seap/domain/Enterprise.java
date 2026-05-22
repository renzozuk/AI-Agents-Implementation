package com.seap.domain;

public record Enterprise (
        String nome,
        String razaoSocial,
        String cnpj,
        String telefone,
        String email,
        String responsavel,
        Address endereco,
        String observacoes
) {}
