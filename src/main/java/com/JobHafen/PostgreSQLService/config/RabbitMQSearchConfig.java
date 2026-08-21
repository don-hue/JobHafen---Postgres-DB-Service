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
public class RabbitMQSearchConfig {
    public static final String EXCHANGE = "search.exchange";
    public static final String SAVE_SEARCH_QUEUE = "search.save.queue";
    public static final String ROUTING_KEY = "search.request";

    public static final String GET_SEARCH_QUEUE = "search.get.queue";
    public static final String GET_SEARCH_ROUTING_KEY = "search.get.request";
    @Bean
    public Queue saveSearchQueue() {return new Queue(SAVE_SEARCH_QUEUE);}

    @Bean
    public Queue getSearchQueue(){return new Queue(GET_SEARCH_QUEUE);}


    @Bean
    public MessageConverter searchMessageConverter() {
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
    public DirectExchange searchExchange() {return new DirectExchange(EXCHANGE);}

    @Bean
    public Binding saveSearchBinding(
            @Qualifier("saveSearchQueue") Queue queue,
            DirectExchange searchExchange) {

        return BindingBuilder
                .bind(queue)
                .to(searchExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding getSearchBinding(
            @Qualifier("getSearchQueue") Queue queue,
            DirectExchange searchExchange) {

        return BindingBuilder
                .bind(queue)
                .to(searchExchange)
                .with(GET_SEARCH_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate searchTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(searchMessageConverter());
        template.setReplyTimeout(10000);
        return template;
    }
    @Bean
    public SimpleRabbitListenerContainerFactory searchListenerFactory(
            ConnectionFactory connectionFactory,
            @Qualifier("searchMessageConverter")
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
