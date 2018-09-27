package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse {

    private List<Assignment> assignments;

    private Customer customer;

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "ProjectResponse{" +
                "assignments=" + assignments +
                ", customer=" + customer +
                '}';
    }
}


