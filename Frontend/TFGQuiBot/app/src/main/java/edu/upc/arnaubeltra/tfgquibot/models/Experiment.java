package edu.upc.arnaubeltra.tfgquibot.models;

public class Experiment {
    private String title, shortDescription, explanation;

    public Experiment(String title, String shortDescription, String explanation) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.explanation = explanation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
