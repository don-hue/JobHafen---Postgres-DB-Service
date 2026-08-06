package com.JobHafen.PostgreSQLService.repository;
import com.JobHafen.PostgreSQLService.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {
    List<JobEntity> findAllByOrderByCompanyCompanyNameAsc();
    JobEntity findByJobTitleAndCompanyCompanyName(String jobTitle, String companyName);
}
