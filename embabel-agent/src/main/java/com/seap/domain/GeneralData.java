package com.seap.domain;

public record GeneralData(
        String orgaoId,
        String valorCentavosContrato,
        String dataInicioVigencia,
        String dataFimVigencia,
        String localTrabalho,
        String tipoTrabalho,
        String cargaHoraria,
        String objetoContrato
) {
}
