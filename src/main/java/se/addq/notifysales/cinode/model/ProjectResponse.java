package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectResponse {

    @JsonProperty(value = "assignments")
    private List<AssignmentResponse> assignmentResponses;

    private Customer customer;

    public List<AssignmentResponse> getAssignmentResponses() {
        return assignmentResponses;
    }

    public void setAssignmentResponses(List<AssignmentResponse> assignmentResponses) {
        this.assignmentResponses = assignmentResponses;
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
                "assignmentResponses=" + assignmentResponses +
                ", customer=" + customer +
                '}';
    }
}


