package com.JobHafen.PostgreSQLService.eventListener;

import com.JobHafen.PostgreSQLService.config.RabbitMQJobConfig;
import com.JobHafen.PostgreSQLService.dto.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class JobEventListener {
    @RabbitListener(
            queues = RabbitMQJobConfig.REQUEST_QUEUE
    )
    public Message consume(Message message) {
        Message test = new Message();
        test.setMessage("XXX I have received the Message");
        System.out.println(" XXX Message empfangen");
        return test;
    }
}
