package com.lms.mainpages.users.entity;

public class InstructorProfile {
    private int instructorId;   // = users.user_id (FK)
    private String affiliation;
    private String bio;

    public InstructorProfile() {}

    public InstructorProfile(int instructorId, String affiliation, String bio) {
        this.instructorId = instructorId;
        this.affiliation = affiliation;
        this.bio = bio;
    }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public String getAffiliation() { return affiliation; }
    public void setAffiliation(String affiliation) { this.affiliation = affiliation; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}
