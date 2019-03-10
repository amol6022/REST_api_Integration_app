package com.example.sparksrestapi.PersonalDetailsObjects;

public class PersonalDetailsInput {
    private String skills,mobile_no,name,links,location,email;

    public PersonalDetailsInput(String skills,String mobile_no,String name,String links,String location,String email){
        this.skills=skills;
        this.mobile_no=mobile_no;
        this.name=name;
        this.links=links;
        this.location=location;
        this.email=email;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
