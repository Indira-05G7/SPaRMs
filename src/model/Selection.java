package model;

import java.sql.Date;

public class Selection {
    private int id;
    private int studentId;
    private int driveId;
    private int applicationId;
    private double offeredPackageLpa;
    private Date selectionDate;
    private String status; // ACCEPTED, DECLINED, PENDING

    // Joined fields
    private String studentName;
    private String studentRoll;
    private String departmentName;
    private String driveTitle;
    private String companyName;
    private String jobRole;

    public Selection() {}

    public Selection(int id, int studentId, int driveId, int applicationId, double offeredPackageLpa, Date selectionDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.driveId = driveId;
        this.applicationId = applicationId;
        this.offeredPackageLpa = offeredPackageLpa;
        this.selectionDate = selectionDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getDriveId() { return driveId; }
    public void setDriveId(int driveId) { this.driveId = driveId; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public double getOfferedPackageLpa() { return offeredPackageLpa; }
    public void setOfferedPackageLpa(double offeredPackageLpa) { this.offeredPackageLpa = offeredPackageLpa; }

    public Date getSelectionDate() { return selectionDate; }
    public void setSelectionDate(Date selectionDate) { this.selectionDate = selectionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentRoll() { return studentRoll; }
    public void setStudentRoll(String studentRoll) { this.studentRoll = studentRoll; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDriveTitle() { return driveTitle; }
    public void setDriveTitle(String driveTitle) { this.driveTitle = driveTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }
}
