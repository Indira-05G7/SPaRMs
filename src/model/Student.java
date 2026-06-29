package model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private int id;
    private String rollNumber;
    private String username;
    private String password;
    private String email;
    private String name;
    private String phone;
    private int departmentId;
    private double cgpa;
    private double tenthPercentage;
    private double twelfthPercentage;
    private int activeBacklogs;
    private String resumeUrl;
    private List<Skill> skills = new ArrayList<>();

    public Student() {}

    public Student(int id, String rollNumber, String username, String password, String email, String name,
                   String phone, int departmentId, double cgpa, double tenthPercentage, double twelfthPercentage,
                   int activeBacklogs, String resumeUrl) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.departmentId = departmentId;
        this.cgpa = cgpa;
        this.tenthPercentage = tenthPercentage;
        this.twelfthPercentage = twelfthPercentage;
        this.activeBacklogs = activeBacklogs;
        this.resumeUrl = resumeUrl;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public double getTenthPercentage() { return tenthPercentage; }
    public void setTenthPercentage(double tenthPercentage) { this.tenthPercentage = tenthPercentage; }

    public double getTwelfthPercentage() { return twelfthPercentage; }
    public void setTwelfthPercentage(double twelfthPercentage) { this.twelfthPercentage = twelfthPercentage; }

    public int getActiveBacklogs() { return activeBacklogs; }
    public void setActiveBacklogs(int activeBacklogs) { this.activeBacklogs = activeBacklogs; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }
}
