package com.JobHafen.PostgreSQLService.service;
import com.JobHafen.PostgreSQLService.config.Constants;
import com.JobHafen.PostgreSQLService.dto.SearchDto;
import com.JobHafen.PostgreSQLService.dto.SearchEntityDto;
import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;
import com.JobHafen.PostgreSQLService.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
}
