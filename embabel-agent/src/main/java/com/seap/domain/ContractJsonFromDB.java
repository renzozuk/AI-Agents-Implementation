package com.seap.domain;

import java.util.List;

public record ContractJsonFromDB(
        Enterprise empresa,
        GeneralData data,
        List<Vacancy> vagas
) {
}
