package model;

import java.sql.Date;

public class PlacementDrive {
    private int id;
    private int companyId;
    private String title;
    private String jobRole;
    private String jobDescription;
    private double packageLpa;
    private double minCgpa;
    private int maxBacklogs;
    private Date driveDate;
    private String status; // UPCOMING, ONGOING, COMPLETED, CANCELLED

    // Joined fields for display
    private String companyName;

    public PlacementDrive() {}

    public PlacementDrive(int id, int companyId, String title, String jobRole, String jobDescription,
                          double packageLpa, double minCgpa, int maxBacklogs, Date driveDate, String status) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.jobRole = jobRole;
        this.jobDescription = jobDescription;
        this.packageLpa = packageLpa;
        this.minCgpa = minCgpa;
        this.maxBacklogs = maxBacklogs;
        this.driveDate = driveDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    public double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(double packageLpa) { this.packageLpa = packageLpa; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public int getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }

    public Date getDriveDate() { return driveDate; }
    public void setDriveDate(Date driveDate) { this.driveDate = driveDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    @Override
    public String toString() {
        return title + " - " + jobRole;
    }
}
