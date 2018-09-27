package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompaniesResponse {

    private String name;

    private String description;

    private List<LinksResponse> links;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LinksResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinksResponse> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "CompaniesResponse{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", links=" + links +
                '}';
    }
}
