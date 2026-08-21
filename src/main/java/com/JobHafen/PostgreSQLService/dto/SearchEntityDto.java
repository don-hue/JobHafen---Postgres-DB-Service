package com.JobHafen.PostgreSQLService.dto;
public record SearchEntityDto(
        Long id,
        String keyword,
        String postal_code,
        String radius
) { }
