package com.JobHafen.PostgreSQLService.controller;

import com.JobHafen.PostgreSQLService.config.RabbitMQConfig;
import com.JobHafen.PostgreSQLService.dto.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;

@Component
public class JobController {
    public JobController() {
        System.out.println("JobController created");
    }
    @RabbitListener(
            queues = RabbitMQConfig.REQUEST_QUEUE
    )
    public Message consume(Message message) {
        Message test = new Message();
        test.setMessage("XXX I have received the Message");
        System.out.println(" XXX Message empfangen");
        return test;
    }
}
