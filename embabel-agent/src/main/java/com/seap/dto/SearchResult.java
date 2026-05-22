package com.seap.dto;

import java.util.Map;

public record SearchResult(
        String content,
        Double score,
        Map<String, Object> metadata
) {}
