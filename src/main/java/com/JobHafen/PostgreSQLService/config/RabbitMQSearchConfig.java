package com.JobHafen.PostgreSQLService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQSearchConfig {
    public static final String EXCHANGE = "search.exchange";
    public static final String SAVE_SEARCH_QUEUE = "search.save.queue";
    public static final String ROUTING_KEY = "search.request";

    @Bean
    public Queue replySearch() {return new Queue(SAVE_SEARCH_QUEUE);}

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public DirectExchange exchange() {return new DirectExchange(EXCHANGE);}

    @Bean
    public Binding bindung(){
        return BindingBuilder
                .bind(replySearch())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setReplyTimeout(10000);
        return template;
    }
}
