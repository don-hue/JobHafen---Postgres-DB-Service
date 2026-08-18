package com.JobHafen.PostgreSQLService.eventListener;
import com.JobHafen.PostgreSQLService.config.RabbitMQSearchConfig;
import com.JobHafen.PostgreSQLService.dto.SearchDto;
import com.JobHafen.PostgreSQLService.dto.SearchEntityDto;
import com.JobHafen.PostgreSQLService.dto.SearchResponse;
import com.JobHafen.PostgreSQLService.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SearchUrlEventListener {
    @Autowired
    SearchService searchService;
    @RabbitListener(
            queues = RabbitMQSearchConfig.SAVE_SEARCH_QUEUE
    )
    public SearchResponse saveSearch(SearchDto search) {
        try {
            SearchEntityDto searchDto = searchService.saveUrl(search);
            return new SearchResponse(true, searchDto);
        } catch(Exception e) {
            System.out.println("Error in SaveSearch" + e.getMessage());
            return new SearchResponse(false, null);
        }
    }
}
