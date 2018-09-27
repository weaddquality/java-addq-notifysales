package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {

    private int id;

    private String name;

    private List<LinksResponse> links;

    public Customer() {
    }

    public Customer(String customerName) {
        this.name = customerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LinksResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinksResponse> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", links=" + links +
                '}';
    }
}
