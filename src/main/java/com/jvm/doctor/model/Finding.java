package com.jvm.doctor.model;

import java.util.ArrayList;
import java.util.List;

public class Finding implements Comparable<Finding> {
    public enum Severity {
        INFO, WARN, ERROR
    }

    public enum Confidence {
        LOW, MED, HIGH
    }

    private String id;
    private String title;
    private String description;
    private Severity severity;
    private int impact;
    private Confidence confidence;
    private double evidenceStrength;
    private double score;
    private List<String> evidence = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private List<String> links = new ArrayList<>();

    // Constructor, Getters, Setters
    public Finding(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public int getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public double getEvidenceStrength() {
        return evidenceStrength;
    }

    public void setEvidenceStrength(double evidenceStrength) {
        this.evidenceStrength = evidenceStrength;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    @Override
    public int compareTo(Finding other) {
        // DESC score, DESC impact, ASC id
        int scoreCmp = Double.compare(other.score, this.score);
        if (scoreCmp != 0)
            return scoreCmp;

        int impactCmp = Integer.compare(other.impact, this.impact);
        if (impactCmp != 0)
            return impactCmp;

        return this.id.compareTo(other.id);
    }
}
