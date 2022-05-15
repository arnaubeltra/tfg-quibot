package edu.upc.arnaubeltra.tfgquibot.models;

public class Action {

    private String name;
    private float quantity = 0;
    private int repetitions = 0;
    private int lastNInstructions = 0;

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getLastNInstructions() {
        return lastNInstructions;
    }

    public void setLastNInstructions(int lastNInstructions) {
        this.lastNInstructions = lastNInstructions;
    }
}
