package se.addq.notifysales.notification.model;

public class AssignmentCustomer {

    private int id;

    private String name;

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

    @Override
    public String toString() {
        return "AssignmentCustomer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
