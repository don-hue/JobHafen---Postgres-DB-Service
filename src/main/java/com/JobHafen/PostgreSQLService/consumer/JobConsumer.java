package com.JobHafen.PostgreSQLService.consumer;

import com.JobHafen.PostgreSQLService.config.RabbitMQJobConfig;
import com.JobHafen.PostgreSQLService.dto.JobDto;
import com.JobHafen.PostgreSQLService.dto.JobEntityDto;
import com.JobHafen.PostgreSQLService.dto.SearchToCrawlDto;
import com.JobHafen.PostgreSQLService.service.JobService;
import com.JobHafen.PostgreSQLService.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobConsumer {
    @Autowired
    SearchService searchService;
    @Autowired
    JobService jobService;
    @RabbitListener(
            queues = RabbitMQJobConfig.GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE,
            containerFactory = "jobListenerFactory"
    )
    public List<SearchToCrawlDto> getSearchesToCrawl() {
        try {
            return searchService.getSearchToCrawl();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(
            queues = RabbitMQJobConfig.SAVE_JOB_QUEUE,
            containerFactory = "jobListenerFactory"
    )
    public void saveJob(JobDto jobDto){
        try {
            System.out.println("Job received in postgres:" + jobDto.jobTitle());
            jobService.saveJob(jobDto);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(
            queues = RabbitMQJobConfig.GET_ALL_JOBS_REQUEST_QUEUE,
            containerFactory = "jobListenerFactory"
    )
    public List<JobEntityDto> getAllJobs(){
        try {
            return jobService.getAllJobs();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(
            queues = RabbitMQJobConfig.CONFIRM_CRAWL_REQUEST_QUEUE,
            containerFactory = "jobListenerFactory"
    )
    public void confirmCrawled(Long searchId) {
        searchService.updateCrawlerAt(searchId);
    }
}
