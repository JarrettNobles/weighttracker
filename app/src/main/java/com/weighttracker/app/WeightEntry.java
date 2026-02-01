package com.weighttracker.app;

import java.io.Serializable;
import java.util.Date;

/**
 * Data model representing a single weight entry.
 */
public class WeightEntry implements Serializable {

    private Date date;
    private double weight;
    private boolean isMetric; // true = kg, false = lbs

    public WeightEntry(Date date, double weight, boolean isMetric) {
        this.date = date;
        this.weight = weight;
        this.isMetric = isMetric;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isMetric() {
        return isMetric;
    }

    public void setMetric(boolean metric) {
        isMetric = metric;
    }

    /**
     * Returns weight formatted to one decimal place with the appropriate unit.
     */
    public String getFormattedWeight() {
        String unit = isMetric ? "kg" : "lbs";
        return String.format("%.1f %s", weight, unit);
    }
}
