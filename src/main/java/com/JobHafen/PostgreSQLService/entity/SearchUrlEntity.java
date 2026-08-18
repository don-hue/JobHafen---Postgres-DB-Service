package com.JobHafen.PostgreSQLService.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SearchUrlEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "search")
    private List<JobEntity> jobs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "search_urls",
            joinColumns = @JoinColumn(name = "search_id")
    )
    @Column(name = "url", length = 2000)
    private List<String> urls = new ArrayList<>();

    @Column
    private String keyword;

    @Column
    private String postal_code;
    @Column
    private String radius;

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
    public List<String> getUrls() {
        return urls;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getKeyword() {
        return keyword;
    }

    public void setPostal_code(String postalCode) {
        this.postal_code = postalCode;
    }
    public String getPostal_code(){return postal_code;}

    public void setRadius(String radius) {
        this.radius = radius;
    }
    public String getRadius() {return radius;}

    public Long getId(){return id;}
}
