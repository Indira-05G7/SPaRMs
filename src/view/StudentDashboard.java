package view;

import dao.DepartmentDAO;
import dao.SkillDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.*;
import service.StudentService;
import utility.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDashboard extends JPanel {
    private final StudentService studentService = new StudentService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private Student currentStudent;

    // Profile Components
    private JTextField txtName, txtEmail, txtPhone, txtRoll, txtCgpa, txt10th, txt12th, txtBacklogs, txtResume;
    private JComboBox<Department> comboDept;
    
    // Skills Components
    private JPanel skillsListPanel;
    private List<JCheckBox> skillCheckBoxes = new ArrayList<>();
    
    // Tables
    private JTable tblDrives;
    private DefaultTableModel modelDrives;
    private JTable tblApplications;
    private DefaultTableModel modelApplications;
    private JTable tblInterviews;
    private DefaultTableModel modelInterviews;
    private JTable tblNotifications;
    private DefaultTableModel modelNotifications;

    public StudentDashboard() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_SOFT);

        // Load logged in student
        currentStudent = SessionManager.getStudent();

        add(tabbedPane, BorderLayout.CENTER);

        initProfileTab();
        initSkillsTab();
        initDrivesTab();
        initApplicationsTab();
        initInterviewsTab();
        initNotificationsTab();

        refreshData();
    }

    public void refreshData() {
        if (currentStudent == null) return;
        
        // Refresh Current Student object from database to ensure fresh data
        try {
            dao.StudentDAO sdao = new dao.StudentDAO();
            currentStudent = sdao.getById(currentStudent.getId());
            SessionManager.login(SessionManager.Role.STUDENT, currentStudent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        populateProfile();
        populateSkills();
        populateDrivesTable();
        populateApplicationsTable();
        populateInterviewsTable();
        populateNotificationsTable();
    }

    private void initProfileTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIHelper.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 0.5;

        // Heading
        JLabel heading = UIHelper.createSubHeader("Manage Academic & Personal Details");
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(heading, gbc);
        gbc.gridwidth = 1;

        // Fields
        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Full Name*:"), gbc);
        txtName = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtName, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Email*:"), gbc);
        txtEmail = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtEmail, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Phone Number:"), gbc);
        txtPhone = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtPhone, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Roll Number*:"), gbc);
        txtRoll = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtRoll, gbc);

        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Department:"), gbc);
        comboDept = new JComboBox<>();
        comboDept.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1; formPanel.add(comboDept, gbc);

        gbc.gridy = 6; gbc.gridx = 0; formPanel.add(new JLabel("Current CGPA*:"), gbc);
        txtCgpa = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtCgpa, gbc);

        gbc.gridy = 7; gbc.gridx = 0; formPanel.add(new JLabel("10th Standard %*:"), gbc);
        txt10th = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txt10th, gbc);

        gbc.gridy = 8; gbc.gridx = 0; formPanel.add(new JLabel("12th Standard %*:"), gbc);
        txt12th = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txt12th, gbc);

        gbc.gridy = 9; gbc.gridx = 0; formPanel.add(new JLabel("Active Backlogs*:"), gbc);
        txtBacklogs = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtBacklogs, gbc);

        gbc.gridy = 10; gbc.gridx = 0; formPanel.add(new JLabel("Resume Path / URL:"), gbc);
        
        JPanel resumePanel = new JPanel(new BorderLayout(5, 0));
        resumePanel.setOpaque(false);
        txtResume = UIHelper.createTextField();
        resumePanel.add(txtResume, BorderLayout.CENTER);
        JButton btnBrowse = new JButton("Browse");
        resumePanel.add(btnBrowse, BorderLayout.EAST);
        gbc.gridx = 1; formPanel.add(resumePanel, gbc);

        // Save Button
        gbc.gridy = 11; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        JButton btnSave = UIHelper.createPrimaryButton("Save Profile Details");
        formPanel.add(btnSave, gbc);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("My Profile", panel);

        // Load Departments combo
        try {
            comboDept.removeAllItems();
            List<Department> list = departmentDAO.getAll();
            for (Department d : list) {
                comboDept.addItem(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Action Listeners
        btnSave.addActionListener(e -> saveProfile());
        btnBrowse.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                txtResume.setText(file.getAbsolutePath());
            }
        });
    }

    private void populateProfile() {
        if (currentStudent == null) return;
        txtName.setText(currentStudent.getName());
        txtEmail.setText(currentStudent.getEmail());
        txtPhone.setText(currentStudent.getPhone());
        txtRoll.setText(currentStudent.getRollNumber());
        txtCgpa.setText(String.format("%.2f", currentStudent.getCgpa()));
        txt10th.setText(String.format("%.2f", currentStudent.getTenthPercentage()));
        txt12th.setText(String.format("%.2f", currentStudent.getTwelfthPercentage()));
        txtBacklogs.setText(String.valueOf(currentStudent.getActiveBacklogs()));
        txtResume.setText(currentStudent.getResumeUrl());

        // Select department
        for (int i = 0; i < comboDept.getItemCount(); i++) {
            Department d = comboDept.getItemAt(i);
            if (d.getId() == currentStudent.getDepartmentId()) {
                comboDept.setSelectedIndex(i);
                break;
            }
        }
    }

    private void saveProfile() {
        try {
            currentStudent.setName(txtName.getText().trim());
            currentStudent.setEmail(txtEmail.getText().trim());
            currentStudent.setPhone(txtPhone.getText().trim());
            currentStudent.setRollNumber(txtRoll.getText().trim());
            currentStudent.setCgpa(Double.parseDouble(txtCgpa.getText().trim()));
            currentStudent.setTenthPercentage(Double.parseDouble(txt10th.getText().trim()));
            currentStudent.setTwelfthPercentage(Double.parseDouble(txt12th.getText().trim()));
            currentStudent.setActiveBacklogs(Integer.parseInt(txtBacklogs.getText().trim()));
            currentStudent.setResumeUrl(txtResume.getText().trim());

            Department dept = (Department) comboDept.getSelectedItem();
            if (dept != null) {
                currentStudent.setDepartmentId(dept.getId());
            }

            studentService.updateProfile(currentStudent);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for academic percentages, CGPA, and backlogs.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initSkillsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Select Your Technical Skills"), BorderLayout.NORTH);

        skillsListPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        skillsListPanel.setBackground(UIHelper.WHITE);
        
        JScrollPane scroll = new JScrollPane(skillsListPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnSaveSkills = UIHelper.createPrimaryButton("Save Skills");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnSaveSkills);
        panel.add(btnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Technical Skills", panel);

        btnSaveSkills.addActionListener(e -> saveSkills());
    }

    private void populateSkills() {
        try {
            skillsListPanel.removeAll();
            skillCheckBoxes.clear();

            List<Skill> allSkills = skillDAO.getAll();
            List<Skill> studSkills = studentService.getStudentSkills(currentStudent.getId());

            for (Skill s : allSkills) {
                JCheckBox cb = new JCheckBox(s.getName());
                cb.setFont(UIHelper.FONT_BODY);
                cb.setBackground(UIHelper.WHITE);
                cb.putClientProperty("skillObj", s);

                // Check if student has this skill
                if (studSkills.contains(s)) {
                    cb.setSelected(true);
                }

                skillCheckBoxes.add(cb);
                skillsListPanel.add(cb);
            }
            skillsListPanel.revalidate();
            skillsListPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSkills() {
        List<Skill> selectedSkills = new ArrayList<>();
        for (JCheckBox cb : skillCheckBoxes) {
            if (cb.isSelected()) {
                selectedSkills.add((Skill) cb.getClientProperty("skillObj"));
            }
        }

        try {
            studentService.updateStudentSkills(currentStudent.getId(), selectedSkills);
            JOptionPane.showMessageDialog(this, "Technical skills saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initDrivesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Eligible Placement Drives"), BorderLayout.NORTH);

        String[] cols = {"Drive ID", "Company", "Job Role", "Package (LPA)", "Min CGPA", "Max Backlogs", "Date", "Eligibility"};
        modelDrives = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tblDrives = new JTable(modelDrives);
        tblDrives.setRowHeight(35);
        tblDrives.setFont(UIHelper.FONT_BODY);
        
        JScrollPane scroll = UIHelper.createTable(modelDrives);
        tblDrives = (JTable) scroll.getViewport().getView(); // Capture the configured table
        
        panel.add(scroll, BorderLayout.CENTER);

        // Control Panel below the table
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);

        JButton btnApply = UIHelper.createPrimaryButton("Apply to Selected Drive");
        controlPanel.add(btnApply);
        panel.add(controlPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Placement Drives", panel);

        btnApply.addActionListener(e -> {
            int selectedRow = tblDrives.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a drive from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int driveId = (int) tblDrives.getValueAt(selectedRow, 0);
            try {
                studentService.applyToDrive(currentStudent.getId(), driveId);
                JOptionPane.showMessageDialog(this, "Applied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (ValidationException | DatabaseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void populateDrivesTable() {
        try {
            modelDrives.setRowCount(0);
            dao.PlacementDriveDAO pdDAO = new dao.PlacementDriveDAO();
            List<PlacementDrive> list = pdDAO.getAll();

            for (PlacementDrive drive : list) {
                if (!"UPCOMING".equalsIgnoreCase(drive.getStatus()) && !"ONGOING".equalsIgnoreCase(drive.getStatus())) {
                    continue; // Only show active drives
                }
                
                boolean eligible = studentService.isEligible(currentStudent, drive);
                String eligibilityStr = eligible ? "Eligible" : "Not Eligible";

                modelDrives.addRow(new Object[]{
                    drive.getId(),
                    drive.getCompanyName(),
                    drive.getJobRole(),
                    drive.getPackageLpa(),
                    drive.getMinCgpa(),
                    drive.getMaxBacklogs(),
                    drive.getDriveDate(),
                    eligibilityStr
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initApplicationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("My Applications & Selection History"), BorderLayout.NORTH);

        String[] cols = {"Application ID", "Company", "Job Role", "Package (LPA)", "Applied Date", "Status"};
        modelApplications = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelApplications);
        tblApplications = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Applied Drives", panel);
    }

    private void populateApplicationsTable() {
        try {
            modelApplications.setRowCount(0);
            List<Application> list = studentService.getAppliedDrives(currentStudent.getId());
            for (Application app : list) {
                modelApplications.addRow(new Object[]{
                    app.getId(),
                    app.getCompanyName(),
                    app.getJobRole(),
                    app.getPackageLpa(),
                    app.getAppliedDate(),
                    app.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initInterviewsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("My Interview Schedule"), BorderLayout.NORTH);

        String[] cols = {"Company", "Drive/Job", "Round Number", "Round Name", "Date & Time", "Status", "Feedback"};
        modelInterviews = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelInterviews);
        tblInterviews = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Interview Schedule", panel);
    }

    private void populateInterviewsTable() {
        try {
            modelInterviews.setRowCount(0);
            List<InterviewRound> list = studentService.getInterviewSchedule(currentStudent.getId());
            for (InterviewRound r : list) {
                modelInterviews.addRow(new Object[]{
                    r.getCompanyName(),
                    r.getDriveTitle(),
                    r.getRoundNumber(),
                    r.getRoundName(),
                    r.getRoundDate(),
                    r.getStatus(),
                    r.getFeedback() == null ? "-" : r.getFeedback()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Notifications & Alerts"), BorderLayout.NORTH);

        String[] cols = {"Notification ID", "Title", "Message", "Received At", "Status"};
        modelNotifications = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelNotifications);
        tblNotifications = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        JButton btnRead = UIHelper.createPrimaryButton("Mark as Read");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRead);
        panel.add(btnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Notifications", panel);

        btnRead.addActionListener(e -> {
            int row = tblNotifications.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a notification first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) tblNotifications.getValueAt(row, 0);
            try {
                dao.NotificationDAO ndao = new dao.NotificationDAO();
                ndao.markAsRead(id);
                refreshData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void populateNotificationsTable() {
        try {
            modelNotifications.setRowCount(0);
            dao.NotificationDAO ndao = new dao.NotificationDAO();
            List<Notification> list = ndao.getForUser("STUDENT", currentStudent.getId());
            for (Notification n : list) {
                modelNotifications.addRow(new Object[]{
                    n.getId(),
                    n.getTitle(),
                    n.getMessage(),
                    n.getCreatedAt(),
                    n.isRead() ? "Read" : "Unread"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
