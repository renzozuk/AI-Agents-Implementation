package com.seap.domain;

public record Address(
        String logadouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String cidade,
        String UF
        ) {
}
