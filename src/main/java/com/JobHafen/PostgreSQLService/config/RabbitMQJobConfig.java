package com.JobHafen.PostgreSQLService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQJobConfig {
    public static final String EXCHANGE = "jobs.exchange";
    public static final String SAVE_JOB_QUEUE = "jobs.request.saveJob.queue";
    public static final String SAVE_JOB_ROUTING_KEY = "jobs.saveJob.request";
    public static final String GET_ALL_JOBS_REQUEST_QUEUE = "jobs.request.getAllJobs.queue";
    public static final String GET_ALL_JOBS_ROUTING_KEY = "jobs.getAllJobs.request";
    public static final String PUT_JOB_APPLIED_REQUEST_QUEUE = "jobs.request.updateJobApplied.queue";
    public static final String PUT_JOB_APPLIED_JOBS_ROUTING_KEY = "jobs.updateJobApplied.request";

    @Bean
    public Queue saveJobQueue() {
        return new Queue(SAVE_JOB_QUEUE);
    }
    @Bean
    public Queue getAllJobsQueue(){
        return new Queue(GET_ALL_JOBS_REQUEST_QUEUE);
    }

    @Bean
    public Queue putJobAppliedQueue() {
        return new Queue(PUT_JOB_APPLIED_REQUEST_QUEUE);
    }

    @Bean
    public MessageConverter jobMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding saveJobBinding(
            @Qualifier("saveJobQueue") Queue queue,
            DirectExchange jobExchange
    ){
        return BindingBuilder
                .bind(queue)
                .to(jobExchange)
                .with(SAVE_JOB_ROUTING_KEY);
    }
    @Bean
    public Binding getAllJobsBinding (
            @Qualifier("getAllJobsQueue") Queue queue,
            DirectExchange jobExchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(jobExchange)
                .with(GET_ALL_JOBS_ROUTING_KEY);

    }


    @Bean
    public Binding putJobAppliedBinding(
            @Qualifier("putJobAppliedQueue") Queue queue,
            DirectExchange jobExchange
    ){
        return BindingBuilder
                .bind(queue)
                .to(jobExchange)
                .with(PUT_JOB_APPLIED_JOBS_ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate jobTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jobMessageConverter());
        template.setReplyTimeout(10000);
        return template;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory jobListenerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("jobMessageConverter")
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
