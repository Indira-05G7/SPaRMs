package model;

import java.sql.Timestamp;

public class Application {
    private int id;
    private int studentId;
    private int driveId;
    private Timestamp appliedDate;
    private String status; // APPLIED, SHORTLISTED, REJECTED, IN_PROGRESS, SELECTED

    // Joined fields for ease of presentation
    private String studentName;
    private String studentRoll;
    private double studentCgpa;
    private String driveTitle;
    private String companyName;
    private String jobRole;
    private double packageLpa;

    public Application() {}

    public Application(int id, int studentId, int driveId, Timestamp appliedDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.driveId = driveId;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getDriveId() { return driveId; }
    public void setDriveId(int driveId) { this.driveId = driveId; }

    public Timestamp getAppliedDate() { return appliedDate; }
    public void setAppliedDate(Timestamp appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRoll() { return studentRoll; }
    public void setStudentRoll(String studentRoll) { this.studentRoll = studentRoll; }

    public double getStudentCgpa() { return studentCgpa; }
    public void setStudentCgpa(double studentCgpa) { this.studentCgpa = studentCgpa; }

    public String getDriveTitle() { return driveTitle; }
    public void setDriveTitle(String driveTitle) { this.driveTitle = driveTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(double packageLpa) { this.packageLpa = packageLpa; }
}
