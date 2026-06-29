package model;

import java.sql.Timestamp;

public class InterviewRound {
    private int id;
    private int applicationId;
    private int roundNumber;
    private String roundName; // e.g. Aptitude, Coding, Technical Interview, HR
    private Timestamp roundDate;
    private String status; // SCHEDULED, PASSED, FAILED, PENDING
    private String feedback;

    // Joined fields
    private String studentName;
    private String driveTitle;
    private String companyName;

    public InterviewRound() {}

    public InterviewRound(int id, int applicationId, int roundNumber, String roundName, Timestamp roundDate, String status, String feedback) {
        this.id = id;
        this.applicationId = applicationId;
        this.roundNumber = roundNumber;
        this.roundName = roundName;
        this.roundDate = roundDate;
        this.status = status;
        this.feedback = feedback;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public String getRoundName() { return roundName; }
    public void setRoundName(String roundName) { this.roundName = roundName; }

    public Timestamp getRoundDate() { return roundDate; }
    public void setRoundDate(Timestamp roundDate) { this.roundDate = roundDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDriveTitle() { return driveTitle; }
    public void setDriveTitle(String driveTitle) { this.driveTitle = driveTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
