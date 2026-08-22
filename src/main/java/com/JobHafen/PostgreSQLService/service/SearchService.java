package com.JobHafen.PostgreSQLService.service;
import com.JobHafen.PostgreSQLService.config.Constants;
import com.JobHafen.PostgreSQLService.dto.SearchDto;
import com.JobHafen.PostgreSQLService.dto.SearchEntityDto;
import com.JobHafen.PostgreSQLService.dto.SearchToCrawlDto;
import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;
import com.JobHafen.PostgreSQLService.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    @Autowired
    UtilityService util;
    @Autowired
    SearchRepository searchRepository;
    public void saveUrl(SearchDto searchDto){
        List<String> urls = new ArrayList<>();
        List<String> stepstoneUrls = util.buildStepstoneUrl(searchDto);
        List<String> cobaUrls = util.buildCommerzbankApiUrlNoGeo(
                searchDto.keyword(),
                50,
                "10",
                "12",
                searchDto.postal_code()
        );
        List<String> fiUrl = new ArrayList<>(List.of(Constants.FinanzInformatik_Jobpage));
        urls.addAll(stepstoneUrls);
        urls.addAll(cobaUrls);
        urls.addAll(fiUrl);


        SearchUrlEntity searchEntity = new SearchUrlEntity();
        searchEntity.setKeyword(searchDto.keyword());
        searchEntity.setPostal_code(searchDto.postal_code());
        searchEntity.setRadius(searchDto.radius());
        searchEntity.setUrls(urls);
        searchRepository.save(searchEntity);
    }
    public List<SearchEntityDto> getAllSearches(){
        List<SearchEntityDto> searches = new ArrayList<>();
        searchRepository
            .findAll()
            .forEach(search -> {
                searches.add(util.mapSearchEntityToDto(search));
            });
        return searches;
    }

    public List<SearchToCrawlDto> getSearchToCrawl() {
        List<SearchToCrawlDto> searchesToCrawl = new ArrayList<>();
        searchRepository
            .findByCrawledAtIsNullOrCrawledAtBefore(LocalDate.now().minusDays(1))
            .forEach(search -> {
                SearchToCrawlDto searchToCrawlDto = new SearchToCrawlDto(
                        search.getId(),
                        search.getKeyword(),
                        search.getUrls()
                );
                searchesToCrawl.add(searchToCrawlDto);
            });
        return searchesToCrawl;
    }
    public void updateCrawlerAt(Long searchId) {
        SearchUrlEntity search = searchRepository.findById(searchId)
                .orElseThrow(() -> new RuntimeException(
                        "Search nicht gefunden"
                ));
        search.setCrawledAt(LocalDate.now());
        searchRepository.save(search);
    }
}
