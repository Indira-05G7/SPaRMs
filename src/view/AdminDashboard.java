package view;

import dao.CompanyDAO;
import dao.StudentDAO;
import exception.DatabaseException;
import model.Company;
import model.Student;
import service.AdminService;
import service.ReportService;
import utility.DatabaseBackup;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends JPanel {
    private final AdminService adminService = new AdminService();
    private final ReportService reportService = new ReportService();
    private final StudentDAO studentDAO = new StudentDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

    // KPI Cards
    private JPanel cardsPanel;
    private JPanel kpiStudents, kpiCompanies, kpiDrives, kpiApplications, kpiSelected, kpiHighestPkg, kpiAvgPkg, kpiPlacementsPct;

    // Report Tables
    private JTable tblDeptPlacements;
    private DefaultTableModel modelDeptPlacements;
    private JTable tblCompanyHiring;
    private DefaultTableModel modelCompanyHiring;
    private JTable tblDrivesStats;
    private DefaultTableModel modelDrivesStats;

    // Student Directory Components
    private JTable tblStudents;
    private DefaultTableModel modelStudents;
    private JTextField txtSearchStudent;
    private TableRowSorter<DefaultTableModel> studentSorter;

    // Company Directory Components
    private JTable tblCompanies;
    private DefaultTableModel modelCompanies;
    private JTextField txtSearchCompany;
    private TableRowSorter<DefaultTableModel> companySorter;

    // Database Backup
    private JTextField txtBackupPath;

    public AdminDashboard() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_SOFT);

        add(tabbedPane, BorderLayout.CENTER);

        initSummaryTab();
        initStudentsTab();
        initCompaniesTab();
        initBackupTab();

        refreshData();
    }

    public void refreshData() {
        refreshKPIs();
        populateReportTables();
        populateStudentsTable();
        populateCompaniesTable();
    }

    private void initSummaryTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // KPI Cards Row (Grid)
        cardsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        cardsPanel.setBackground(UIHelper.WHITE);
        panel.add(cardsPanel, BorderLayout.NORTH);

        // Reports sub-tabbed pane
        JTabbedPane reportsTab = new JTabbedPane(JTabbedPane.BOTTOM);
        reportsTab.setBackground(UIHelper.WHITE);
        
        // Report 1: Department Placements
        JPanel deptPanel = new JPanel(new BorderLayout());
        deptPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        deptPanel.setBackground(UIHelper.WHITE);
        modelDeptPlacements = new DefaultTableModel(new String[]{"Department", "Total Students", "Placed Students", "Placement %"}, 0);
        deptPanel.add(UIHelper.createTable(modelDeptPlacements), BorderLayout.CENTER);
        reportsTab.addTab("Department Wise Placements", deptPanel);

        // Report 2: Company Hiring
        JPanel compPanel = new JPanel(new BorderLayout());
        compPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        compPanel.setBackground(UIHelper.WHITE);
        modelCompanyHiring = new DefaultTableModel(new String[]{"Company Name", "Total Hired", "Highest Package (LPA)", "Average Package (LPA)"}, 0);
        compPanel.add(UIHelper.createTable(modelCompanyHiring), BorderLayout.CENTER);
        reportsTab.addTab("Company Wise Hiring", compPanel);

        // Report 3: Drive statistics
        JPanel drivePanel = new JPanel(new BorderLayout());
        drivePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        drivePanel.setBackground(UIHelper.WHITE);
        modelDrivesStats = new DefaultTableModel(new String[]{"Drive Title", "Company", "Job Role", "Applicants", "Shortlisted", "Selected", "Rejected"}, 0);
        drivePanel.add(UIHelper.createTable(modelDrivesStats), BorderLayout.CENTER);
        reportsTab.addTab("Placement Drives Performance", drivePanel);

        panel.add(reportsTab, BorderLayout.CENTER);

        tabbedPane.addTab("Dashboard Summary", panel);
    }

    private void refreshKPIs() {
        try {
            cardsPanel.removeAll();
            Map<String, Object> stats = adminService.getDashboardStats();

            cardsPanel.add(UIHelper.createKPICard("Total Students", stats.get("totalStudents").toString(), UIHelper.BLUE));
            cardsPanel.add(UIHelper.createKPICard("Total Companies", stats.get("totalCompanies").toString(), UIHelper.NAVY));
            cardsPanel.add(UIHelper.createKPICard("Total Placement Drives", stats.get("totalDrives").toString(), UIHelper.WARNING));
            cardsPanel.add(UIHelper.createKPICard("Applications Received", stats.get("totalApplications").toString(), UIHelper.TEXT_DARK));
            
            cardsPanel.add(UIHelper.createKPICard("Selected Candidates", stats.get("totalSelected").toString(), UIHelper.SUCCESS));
            cardsPanel.add(UIHelper.createKPICard("Highest Package (LPA)", String.format("%.2f", stats.get("highestPackage")), UIHelper.SUCCESS));
            cardsPanel.add(UIHelper.createKPICard("Average Package (LPA)", String.format("%.2f", stats.get("averagePackage")), UIHelper.BLUE));
            cardsPanel.add(UIHelper.createKPICard("Placement Percentage", String.format("%.1f%%", stats.get("placementPercentage")), UIHelper.SUCCESS));

            cardsPanel.revalidate();
            cardsPanel.repaint();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
    }

    private void populateReportTables() {
        try {
            // Dept Placements
            modelDeptPlacements.setRowCount(0);
            List<Map<String, Object>> depts = reportService.getDepartmentWisePlacements();
            for (Map<String, Object> map : depts) {
                modelDeptPlacements.addRow(new Object[]{
                    map.get("department"),
                    map.get("total"),
                    map.get("placed"),
                    String.format("%.1f%%", map.get("percentage"))
                });
            }

            // Company Hiring
            modelCompanyHiring.setRowCount(0);
            List<Map<String, Object>> comps = reportService.getCompanyWiseHiring();
            for (Map<String, Object> map : comps) {
                modelCompanyHiring.addRow(new Object[]{
                    map.get("company"),
                    map.get("hired"),
                    String.format("%.2f LPA", map.get("maxPackage")),
                    String.format("%.2f LPA", map.get("avgPackage"))
                });
            }

            // Drives statistics
            modelDrivesStats.setRowCount(0);
            List<Map<String, Object>> drives = reportService.getDriveWiseStatistics();
            for (Map<String, Object> map : drives) {
                modelDrivesStats.addRow(new Object[]{
                    map.get("driveTitle"),
                    map.get("company"),
                    map.get("role"),
                    map.get("applicants"),
                    map.get("shortlisted"),
                    map.get("selected"),
                    map.get("rejected")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Search Student:"));
        txtSearchStudent = new JTextField();
        txtSearchStudent.setPreferredSize(new Dimension(220, 32));
        toolbar.add(txtSearchStudent);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Student ID", "Name", "Roll Number", "Email", "Phone", "CGPA", "Backlogs"};
        modelStudents = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Double.class;
                if (columnIndex == 6) return Integer.class;
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };

        JScrollPane scroll = UIHelper.createTable(modelStudents);
        tblStudents = (JTable) scroll.getViewport().getView();
        
        // Sorting and Filtering
        studentSorter = new TableRowSorter<>(modelStudents);
        tblStudents.setRowSorter(studentSorter);

        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Students Directory", panel);

        // Filter listener
        txtSearchStudent.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterStudents(); }
            public void removeUpdate(DocumentEvent e) { filterStudents(); }
            public void changedUpdate(DocumentEvent e) { filterStudents(); }
        });
    }

    private void filterStudents() {
        String text = txtSearchStudent.getText();
        if (text.trim().length() == 0) {
            studentSorter.setRowFilter(null);
        } else {
            studentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void populateStudentsTable() {
        try {
            modelStudents.setRowCount(0);
            List<Student> list = studentDAO.getAll();
            for (Student s : list) {
                modelStudents.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getRollNumber(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getCgpa(),
                    s.getActiveBacklogs()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCompaniesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Search Company:"));
        txtSearchCompany = new JTextField();
        txtSearchCompany.setPreferredSize(new Dimension(220, 32));
        toolbar.add(txtSearchCompany);

        JButton btnAddCompany = UIHelper.createPrimaryButton("Register New Company");
        toolbar.add(btnAddCompany);

        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Company ID", "Company Name", "Website", "Industry", "Description"};
        modelCompanies = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 0) return Integer.class;
                return String.class;
            }
        };

        JScrollPane scroll = UIHelper.createTable(modelCompanies);
        tblCompanies = (JTable) scroll.getViewport().getView();
        
        companySorter = new TableRowSorter<>(modelCompanies);
        tblCompanies.setRowSorter(companySorter);

        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Companies Directory", panel);

        // Action Listeners
        txtSearchCompany.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterCompanies(); }
            public void removeUpdate(DocumentEvent e) { filterCompanies(); }
            public void changedUpdate(DocumentEvent e) { filterCompanies(); }
        });

        btnAddCompany.addActionListener(e -> registerNewCompanyInline());
    }

    private void filterCompanies() {
        String text = txtSearchCompany.getText();
        if (text.trim().length() == 0) {
            companySorter.setRowFilter(null);
        } else {
            companySorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void populateCompaniesTable() {
        try {
            modelCompanies.setRowCount(0);
            List<Company> list = companyDAO.getAll();
            for (Company c : list) {
                modelCompanies.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getWebsite(),
                    c.getIndustry(),
                    c.getDescription()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNewCompanyInline() {
        JTextField nameField = new JTextField();
        JTextField webField = new JTextField();
        JTextField indField = new JTextField();
        JTextArea descArea = new JTextArea(4, 20);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("Company Name*:"));
        panel.add(nameField);
        panel.add(new JLabel("Website:"));
        panel.add(webField);
        panel.add(new JLabel("Industry:"));
        panel.add(indField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New Company", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Company Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Company company = new Company();
                company.setName(name);
                company.setWebsite(webField.getText().trim());
                company.setIndustry(indField.getText().trim());
                company.setDescription(descArea.getText().trim());

                boolean success = companyDAO.save(company);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Company registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving company: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initBackupTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Database Administration & Safety"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIHelper.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Backup Target Path (SQL Dump File):"), gbc);

        txtBackupPath = UIHelper.createTextField();
        txtBackupPath.setText(new File(".").getAbsolutePath() + File.separator + "sparms_db_backup.sql");
        gbc.gridy = 1;
        form.add(txtBackupPath, gbc);

        JButton btnBackup = UIHelper.createPrimaryButton("Run System Database Backup Now");
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        form.add(btnBackup, gbc);

        JTextArea logArea = new JTextArea(8, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setText("System Logs:\nReady to backup database.");
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        form.add(new JScrollPane(logArea), gbc);

        panel.add(form, BorderLayout.CENTER);

        tabbedPane.addTab("Database Admin", panel);

        btnBackup.addActionListener(e -> {
            String path = txtBackupPath.getText().trim();
            if (path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid backup destination path.", "Path Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            logArea.append("\nStarting backup to: " + path);
            boolean success = DatabaseBackup.executeBackup(path);
            if (success) {
                logArea.append("\nSUCCESS! Backup generated successfully.");
                JOptionPane.showMessageDialog(this, "Database backup completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                logArea.append("\nERROR: Database backup failed.");
                JOptionPane.showMessageDialog(this, "Failed to perform database backup. Check logs.", "Backup Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
