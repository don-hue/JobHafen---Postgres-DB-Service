package com.JobHafen.PostgreSQLService.service;
import com.JobHafen.PostgreSQLService.config.Constants;
import de.TheDonJuan.dto.search.SearchDto;
import de.TheDonJuan.dto.search.SearchEntityDto;
import com.JobHafen.PostgreSQLService.entity.SearchUrlEntity;
import org.htmlunit.WebClient;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UtilityService {
    public List<String> buildStepstoneUrl(SearchDto search){
        try{
            String url = "https://www.stepstone.de/" +
                    "jobs/" + URLEncoder.encode(search.keyword(), StandardCharsets.UTF_8).replace("+", "%20") + "/" +
                    "in-" + search.postal_code() + "?whatType=autosuggest&" +
                    "radius=" + search.radius() + "&" +
                    "q" + URLEncoder.encode(search.keyword(), StandardCharsets.UTF_8).replace("+", "%20") + "&" +
                    "searchOrigin=Resultlist_top-search";

            return stepstoneSearchToUrls(url);
        } catch (Exception e) {
            System.out.println("Error: "+ e.getMessage());
            throw e;
        }

    }
    public List<String> buildCommerzbankApiUrlNoGeo(
            String keyword,
            int distance,
            String jobCategoryCode,
            String channelCode,
            String postalCode
    ) {
        List<String> urls = new ArrayList<>();
        try {
            double[] coordinates = getGeoData(postalCode);
            double latitude = coordinates[0];
            double longitude = coordinates[1];
            ObjectMapper mapper = new ObjectMapper();

            ObjectNode root = mapper.createObjectNode();
            root.put("LanguageCode", "DE");

            ObjectNode sp = root.putObject("SearchParameters");
            sp.put("FirstItem", 1);
            sp.put("CountItem", 10000);

            ArrayNode sort = sp.putArray("Sort");
            ObjectNode sortObj = mapper.createObjectNode();
            sortObj.put("Criterion", "PublicationStartDate");
            sortObj.put("Direction", "DESC");
            sort.add(sortObj);

            ArrayNode fields = sp.putArray("MatchedObjectDescriptor");
            fields.add("ID");
            fields.add("PositionTitle");
            fields.add("PositionURI");
            fields.add("PositionLocation.CityName");


            ArrayNode criteria = root.putArray("SearchCriteria");

            add(criteria, mapper, "PositionFormattedDescription.Content", keyword);
            add(criteria, mapper, "JobCategory.Code", jobCategoryCode);
            add(criteria, mapper, "PublicationChannel.Code", channelCode);
            add(criteria, mapper, "PositionLocation.Distance", String.valueOf(distance));
            add(criteria, mapper, "PositionLocation.PostalCode", String.valueOf(postalCode));
            add(criteria, mapper,
                    "PositionLocation.Latitude",
                    String.valueOf(latitude));

            // longitude
            add(criteria, mapper,
                    "PositionLocation.Longitude",
                    String.valueOf(longitude));

            String json = mapper.writeValueAsString(root);

            urls.add(Constants.COMMERZBANK_API
                    + URLEncoder.encode(json, StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return urls;
    }


    private List<String> stepstoneSearchToUrls(String stepstoneUrl) {
        int pages = howManyPages(stepstoneUrl);
        List<String> stepstoneUrls = new ArrayList<>();

        try {
            for (int i = 1; i < pages + 1; i++) {
                String pagedURL = stepstoneUrl + "&page=" + i;
                stepstoneUrls.add(pagedURL);
            }
        } catch (RuntimeException e) {
            System.out.println("Error in function" + e.getMessage());
            throw new RuntimeException("Error" + e);
        }

        return stepstoneUrls;
    }
    private int howManyPages(String url) {
        int page = 1;
        boolean goOn = true;

        while (goOn) {
            try {
                String pagedURL = url + "&page=" + page;
                Document doc = Jsoup.connect(pagedURL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .header("Accept", "text/html,application/xhtml+xml")
                        .header("Accept-Language", "de-DE,de;q=0.9")
                        .get();
                page++;

            } catch (IOException e) {
                System.out.println("Error: "+ e.getMessage());
                page--;
                goOn = false;
            } catch (RuntimeException e) {
                System.out.println("Error: "+ e.getMessage());
                page--;
                goOn = false;
            }
        }
        return page;
    }
    private double[] getGeoData(String postalCode) {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);

            String url =
                    Constants.GEO_API
                            + "?postalcode=" + postalCode
                            + "&country=Germany"
                            + "&format=json";

            final String json = webClient
                    .getPage(url)
                    .getWebResponse().getContentAsString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode first = root.get(0);
            double lat = first.path("lat").asDouble();
            double lon = first.path("lon").asDouble();
            return new double[]{lat, lon};

        } catch (MalformedURLException e) {
            System.out.println("Error in geoData" + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error in geoData" + e.getMessage());
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            System.out.println("Error in geoData" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void add(
            ArrayNode criteria,
            ObjectMapper mapper,
            String name,
            String value
    ) {
        ObjectNode obj = mapper.createObjectNode();
        obj.put("CriterionName", name);

        ArrayNode arr = mapper.createArrayNode();
        arr.add(value);

        obj.set("CriterionValue", arr);

        criteria.add(obj);
    }
    public SearchEntityDto mapSearchEntityToDto(SearchUrlEntity entity) {
        return new SearchEntityDto(
                entity.getId(),
                entity.getKeyword(),
                entity.getPostal_code(),
                entity.getRadius()
        );
    }
}

