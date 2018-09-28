package se.addq.notifysales.utils;

public class NoSerializableClass {

    private String text;

    private NoSerializableClass() {
    }

    NoSerializableClass(String text) {
        this.text = text;
    }

    private String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NoSerializableClass{" +
                "text='" + text + '\'' +
                '}';
    }

}


