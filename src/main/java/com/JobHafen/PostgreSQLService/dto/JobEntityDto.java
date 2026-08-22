package com.JobHafen.PostgreSQLService.dto;

import java.net.URL;

public record JobEntityDto(
        Long id,
        String jobTitle,
        boolean applied,
        String companyName,
        URL companyHomepage
) { }
