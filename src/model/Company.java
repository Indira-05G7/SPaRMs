package model;

public class Company {
    private int id;
    private String name;
    private String website;
    private String industry;
    private String description;

    public Company() {}

    public Company(int id, String name, String website, String industry, String description) {
        this.id = id;
        this.name = name;
        this.website = website;
        this.industry = industry;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name;
    }
}
