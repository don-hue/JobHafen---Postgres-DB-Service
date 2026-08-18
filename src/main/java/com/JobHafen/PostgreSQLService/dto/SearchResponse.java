package com.JobHafen.PostgreSQLService.dto;

import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;

public record SearchResponse(
        boolean success,
        SearchEntityDto searchEntityDto
) {}
