package com.JobHafen.PostgreSQLService.eventListener;
import com.JobHafen.PostgreSQLService.config.RabbitMQSearchConfig;
import com.JobHafen.PostgreSQLService.dto.SearchDto;
import com.JobHafen.PostgreSQLService.dto.SearchEntityDto;
import com.JobHafen.PostgreSQLService.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchUrlEventListener {
    @Autowired
    SearchService searchService;
    @RabbitListener(
            queues = RabbitMQSearchConfig.SAVE_SEARCH_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public List<SearchEntityDto> saveSearch(SearchDto searchDto) {
        System.out.println("!!! SAVE SEARCH RECEIVED !!!");
        try {
            searchService.saveUrl(searchDto);
            return searchService.getAllSearches();
        } catch(Exception e) {
            System.out.println("Error in SaveSearch" + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(
            queues = RabbitMQSearchConfig.GET_SEARCH_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public List<SearchEntityDto> getSearches(){
        System.out.println("XXX in getSearches");
        System.out.println("!!! GET SEARCH RECEIVED !!!");
        try{

            return searchService.getAllSearches();
        } catch (Exception e) {
            System.out.println("Error in getSearch" + e.getMessage());
            throw e;
        }
    }
}
