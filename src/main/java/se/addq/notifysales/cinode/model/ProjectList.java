package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectList {

    private int companyId;

    private int customerId;

    private int id;

    private String title;

    private String description;

    public ProjectList() {
    }

    public ProjectList(int id) {
        this.id = id;
    }

    private List<LinksResponse> links;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return "ProjectList{" +
                "companyId=" + companyId +
                ", customerId=" + customerId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", links=" + links +
                '}';
    }
}
