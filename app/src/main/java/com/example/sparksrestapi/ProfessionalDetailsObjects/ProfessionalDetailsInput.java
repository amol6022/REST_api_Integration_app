package com.example.sparksrestapi.ProfessionalDetailsObjects;

public class ProfessionalDetailsInput {
    String start_date,end_date,organisation,designation;

    public ProfessionalDetailsInput(String start_date, String end_date, String organisation, String designation) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.organisation = organisation;
        this.designation = designation;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
