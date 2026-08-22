package com.JobHafen.PostgreSQLService.service;
import com.JobHafen.PostgreSQLService.dto.JobDto;
import com.JobHafen.PostgreSQLService.dto.JobEntityDto;
import com.JobHafen.PostgreSQLService.entity.CompanyEntity;
import com.JobHafen.PostgreSQLService.entity.JobEntity;
import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;
import com.JobHafen.PostgreSQLService.repository.CompanyRepository;
import com.JobHafen.PostgreSQLService.repository.JobRepository;
import com.JobHafen.PostgreSQLService.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private SearchRepository searchRepository;
    @Autowired
    private CompanyRepository companyRepository;

    public List<JobEntityDto> getAllJobs() {
        List<JobEntityDto> jobs = new ArrayList<>();
        List<JobEntity> jobEntityList = jobRepository.findAllByOrderByCompanyCompanyNameAsc();
        jobEntityList.forEach( jobEntity -> {
                JobEntityDto job = new JobEntityDto(
                        jobEntity.getId(),
                        jobEntity.getJobTitle(),
                        jobEntity.getApplied(),
                        jobEntity.getCompany().getCompanyName(),
                        jobEntity.getCompany().getUrl()
                );
                jobs.add(job);
                }
        );
        return jobs;
    }
    public void updateApplied(boolean applied, String company, String jobTitle){
        JobEntity job = jobRepository.findByJobTitleAndCompanyCompanyName(jobTitle,company);
        if(job != null) {
            job.setApplied(applied);
            jobRepository.save(job);
        }
    }

    public void saveJob(JobDto jobDto) {
        if (jobDto != null && jobDto.company() != null) {
            SearchUrlEntity search = searchRepository.findById(jobDto.searchId())
                    .orElseThrow(() -> new RuntimeException(
                            "Search nicht gefunden: " + jobDto.searchId()
                    ));
            CompanyEntity company = companyRepository.findByCompanyName(jobDto.company().companyName());
            if(company == null) {
                company = new CompanyEntity();
                company.setCompanyName(jobDto.company().companyName());
                company.setUrl(jobDto.company().homepage());
                company.setApi(jobDto.company().api());
                company.setShowCompany(jobDto.company().showCompany());
                companyRepository.save(company);
            }


            JobEntity job = jobRepository.findByJobTitleAndCompanyCompanyName(jobDto.jobTitle(), jobDto.company().companyName());
            if(job == null) {
                job = new JobEntity();
                job.setJobTitle(jobDto.jobTitle());
                job.setApplied(jobDto.applied());
                job.setCompany(company);
                job.setSearch(search);
                jobRepository.save(job);
            }
        }
    }
}
