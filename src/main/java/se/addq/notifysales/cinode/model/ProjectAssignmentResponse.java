package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectAssignmentResponse {

    private Assigned assigned;

    private Customer customer;

    private List<LinksResponse> links;

    public Assigned getAssigned() {
        return assigned;
    }

    public void setAssigned(Assigned assigned) {
        this.assigned = assigned;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<LinksResponse> getLinks() {
        return links;
    }

    public void setLinks(List<LinksResponse> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "ProjectAssignmentResponse{" +
                "assigned=" + assigned +
                ", customer=" + customer +
                ", links=" + links +
                '}';
    }
}
