package com.example.ashish.playbuddy;

public class Interest {

        private  String interestId;
        private  String email;
        private String sportId;
        private int positionOfSport;

    public Interest() {
    }

    public Interest(String email, String sportId, int positionOfSport) {
        this.email = email;
        this.sportId = sportId;
        this.positionOfSport = positionOfSport;
    }

    public String getInterestId() {
        return interestId;
    }

    public void setInterestId(String interestId) {
        this.interestId = interestId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public int getPositionOfSport() {
        return positionOfSport;
    }

    public void setPositionOfSport(int positionOfSport) {
        this.positionOfSport = positionOfSport;
    }
}
