package com.JobHafen.PostgreSQLService.repository;

import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<SearchUrlEntity, Long> {

}
