package com.JobHafen.PostgreSQLService.consumer;
import com.JobHafen.PostgreSQLService.config.RabbitMQSearchConfig;
import de.TheDonJuan.dto.ResponseDto;
import de.TheDonJuan.dto.search.SearchDto;
import de.TheDonJuan.dto.search.SearchEntityDto;
import de.TheDonJuan.dto.search.SearchToCrawlDto;
import com.JobHafen.PostgreSQLService.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchConsumer {
    @Autowired
    SearchService searchService;
    @RabbitListener(
            queues = RabbitMQSearchConfig.SAVE_SEARCH_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public List<SearchEntityDto> saveSearch(SearchDto searchDto) {
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
        try{
            return searchService.getAllSearches();
        } catch (Exception e) {
            System.out.println("Error in getSearch" + e.getMessage());
            throw e;
        }
    }
    @RabbitListener(
            queues = RabbitMQSearchConfig.CONFIRM_CRAWL_REQUEST_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public void confirmCrawled(Long searchId) {
        try{
            searchService.updateCrawlerAt(searchId);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e;
        }
    }
    @RabbitListener(
            queues = RabbitMQSearchConfig.GET_SEARCHES_TO_CRAWL_REQUEST_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public List<SearchToCrawlDto> getSearchesToCrawl() {
        try {
            return searchService.getSearchToCrawl();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
            throw e;
        }
    }

    @RabbitListener(
            queues = RabbitMQSearchConfig.DELETE_SEARCH_QUEUE,
            containerFactory = "searchListenerFactory"
    )
    public ResponseDto deleteSearch(Long id) {
        try{
            searchService.deleteSearch(id);
            return new ResponseDto(true);
        } catch (Exception e) {
            System.out.println("Error in Consumer" + e.getMessage());
            return new ResponseDto(false);
        }
    }
}
