package com.seap.domain;

import java.util.List;

public record ContractJSON(
        Enterprise empresa,
        GeneralData data,
        List<Vacancy> vagas
) {
}
