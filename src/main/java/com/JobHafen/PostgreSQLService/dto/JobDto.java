package com.JobHafen.PostgreSQLService.dto;

public record JobDto(
        String jobTitle,
        boolean applied,
        CompanyDto company,
        Long searchId
        ) {}
