package com.JobHafen.PostgreSQLService.repository;

import com.JobHafen.PostgreSQLService.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    CompanyEntity findByCompanyName(String companyName);
}
