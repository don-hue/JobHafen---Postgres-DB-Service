package com.JobHafen.PostgreSQLService.consumer;

import com.JobHafen.PostgreSQLService.config.RabbitMQJobConfig;
import de.TheDonJuan.dto.ResponseDto;
import de.TheDonJuan.dto.job.JobDto;
import de.TheDonJuan.dto.job.JobEntityDto;
import de.TheDonJuan.dto.job.JobUpdateAppliedDto;
import com.JobHafen.PostgreSQLService.service.JobService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobConsumer {
    @Autowired
    JobService jobService;


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
            queues = RabbitMQJobConfig.PUT_JOB_APPLIED_REQUEST_QUEUE,
            containerFactory = "jobListenerFactory"
    )
    public ResponseDto putJobApplied(JobUpdateAppliedDto jobUpdateAppliedDto) {
        try{
            jobService.putJobApplied(jobUpdateAppliedDto);
            return new ResponseDto(true);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            return new ResponseDto(false);
        }
    }
}
