package com.seap.domain;

import com.seap.domain.informacoes.MissingData;

public record FinalStep(
        ContractJSON contractJSON,
        MissingData missingData,
        String complete
) {
}
