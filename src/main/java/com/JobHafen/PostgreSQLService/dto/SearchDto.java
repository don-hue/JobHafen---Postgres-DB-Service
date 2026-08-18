package com.JobHafen.PostgreSQLService.dto;

public record SearchDto (
    String keyword,
    String postal_code,
    String radius
) {}
