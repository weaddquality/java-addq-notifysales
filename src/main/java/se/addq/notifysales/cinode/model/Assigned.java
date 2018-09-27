package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Assigned {

    private String userId;
    private int companyId;
    private int id;
    private String seoId;
    private String firstName;
    private String lastName;
    private int companyUserType;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeoId() {
        return seoId;
    }

    public void setSeoId(String seoId) {
        this.seoId = seoId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCompanyUserType() {
        return companyUserType;
    }

    public void setCompanyUserType(int companyUserType) {
        this.companyUserType = companyUserType;
    }

    @Override
    public String toString() {
        return "Assigned{" +
                "userId='" + userId + '\'' +
                ", companyId=" + companyId +
                ", id=" + id +
                ", seoId='" + seoId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", companyUserType=" + companyUserType +
                '}';
    }
}
