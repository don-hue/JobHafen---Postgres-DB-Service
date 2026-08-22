package com.JobHafen.PostgreSQLService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
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
    public static final String GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE = "jobs.request.searchToCrawl.queue";
    public static final String GET_SEARCHES_TO_CRAWL_ROUTING_KEY = "jobs.searchToCrawl.request";
    public static final String SAVE_JOB_QUEUE = "jobs.request.saveJob.queue";
    public static final String SAVE_JOB_ROUTING_KEY = "jobs.saveJob.request";
    public static final String GET_ALL_JOBS_REQUEST_QUEUE = "jobs.request.getAllJobs.queue";
    public static final String GET_ALL_JOBS_ROUTING_KEY = "jobs.getAllJobs.request";
    public static final String CONFIRM_CRAWL_REQUEST_QUEUE = "jobs.request.confirmCrawl.queue";
    public static final String CONFIRM_CRAWL_ROUTING_KEY = "jobs.confirmCrawl.request";
    @Bean
    public Queue getSearchesToCrawlQueue() {
        return new Queue(GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE);
    }
    @Bean
    public Queue saveJobQueue() {
        return new Queue(SAVE_JOB_QUEUE);
    }
    @Bean
    public Queue getAllJobsQueue(){
        return new Queue(GET_ALL_JOBS_REQUEST_QUEUE);
    }
    @Bean
    public Queue confirmCrawlQueue(){return new Queue(CONFIRM_CRAWL_REQUEST_QUEUE);}

    @Bean
    public MessageConverter jobMessageConverter() {
        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();

        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put(
                "com.JobHafen.Proxy.dto.SearchDto",
                com.JobHafen.PostgreSQLService.dto.SearchDto.class
        );

        idClassMapping.put(
                "com.JobHafen.Crawler.dto.JobDto",
                com.JobHafen.PostgreSQLService.dto.JobDto.class
        );

        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding getSearchesToCrawlBinding(
            @Qualifier("getSearchesToCrawlQueue") Queue queue,
            DirectExchange jobExchange) {
        return BindingBuilder
                .bind(queue)
                .to(jobExchange)
                .with(GET_SEARCHES_TO_CRAWL_ROUTING_KEY);
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
    public Binding confirmCrawlBinding(
            @Qualifier("confirmCrawlQueue") Queue queue,
            DirectExchange jobExchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(jobExchange)
                .with(CONFIRM_CRAWL_ROUTING_KEY);
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
