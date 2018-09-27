package se.addq.notifysales.cinode.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LinksResponse {

    private String href;
    private String rel;
    private List<String> methods;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        return "LinksResponse{" +
                "href='" + href + '\'' +
                ", rel='" + rel + '\'' +
                ", methods=" + methods +
                '}';
    }
}
