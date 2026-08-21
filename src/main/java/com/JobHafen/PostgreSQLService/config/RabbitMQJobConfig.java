package com.JobHafen.PostgreSQLService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQJobConfig {
    public static final String EXCHANGE = "jobs.exchange";
    public static final String REQUEST_QUEUE = "jobs.request.queue";
    public static final String ROUTING_KEY = "jobs.request";

    @Bean
    public Queue replyJobs() {
        return new Queue(REQUEST_QUEUE);
    }

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

        classMapper.setIdClassMapping(idClassMapping);

        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding jobBinding() {
        return BindingBuilder
                .bind(replyJobs())
                .to(jobExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate jobTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jobMessageConverter());
        template.setReplyTimeout(10000);
        return template;
    }
}
