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

@Configuration
public class RabbitMQSearchConfig {
    public static final String EXCHANGE = "search.exchange";
    public static final String SAVE_SEARCH_QUEUE = "search.save.queue";
    public static final String ROUTING_KEY = "search.request";
    public static final String GET_SEARCH_QUEUE = "search.get.queue";
    public static final String GET_SEARCH_ROUTING_KEY = "search.get.request";
    public static final String CONFIRM_CRAWL_REQUEST_QUEUE = "search.request.confirmCrawl.queue";
    public static final String CONFIRM_CRAWL_ROUTING_KEY = "search.confirmCrawl.request";
    public static final String GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE = "search.request.searchToCrawl.queue";
    public static final String GET_SEARCHES_TO_CRAWL_ROUTING_KEY = "search.searchToCrawl.request";
    public static final String DELETE_SEARCH_QUEUE = "search.delete.queue";
    public static final String DELETE_SEARCH_ROUTING_KEY = "search.delete.request";
    @Bean
    public Queue saveSearchQueue() {return new Queue(SAVE_SEARCH_QUEUE);}

    @Bean
    public Queue getSearchQueue(){return new Queue(GET_SEARCH_QUEUE);}
    @Bean
    public Queue confirmCrawlQueue(){return new Queue(CONFIRM_CRAWL_REQUEST_QUEUE);}
    @Bean
    public Queue getSearchesToCrawlQueue() {
        return new Queue(GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE);
    }
    @Bean
    public Queue deleteSearchQueue(){return new Queue(DELETE_SEARCH_QUEUE);}


    @Bean
    public MessageConverter searchMessageConverter() {
        return new JacksonJsonMessageConverter();
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
    public Binding confirmCrawlBinding(
            @Qualifier("confirmCrawlQueue") Queue queue,
            DirectExchange searchExchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(searchExchange)
                .with(CONFIRM_CRAWL_ROUTING_KEY);
    }
    @Bean
    public Binding getSearchesToCrawlBinding(
            @Qualifier("getSearchesToCrawlQueue") Queue queue,
            DirectExchange searchExchange) {
        return BindingBuilder
                .bind(queue)
                .to(searchExchange)
                .with(GET_SEARCHES_TO_CRAWL_ROUTING_KEY);
    }
    @Bean
    public Binding deleteSearchBinding(
            @Qualifier("deleteSearchQueue") Queue queue,
            DirectExchange searchExchange
    ) {
        return BindingBuilder
                .bind(queue)
                .to(searchExchange)
                .with(DELETE_SEARCH_ROUTING_KEY);
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
