package com.JobHafen.PostgreSQLService.service;
import com.JobHafen.PostgreSQLService.entity.JobEntity;
import com.JobHafen.PostgreSQLService.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    public List<JobEntity> getAllJobs() {
        return jobRepository.findAllByOrderByCompanyCompanyNameAsc();
    }
    public void updateApplied(boolean applied, String company, String jobTitle){
        JobEntity job = jobRepository.findByJobTitleAndCompanyCompanyName(jobTitle,company);
        if(job != null) {
            job.setApplied(applied);
            jobRepository.save(job);
        }
    }
}
