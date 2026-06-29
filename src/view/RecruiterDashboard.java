package view;

import dao.CompanyDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.*;
import service.RecruiterService;
import utility.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class RecruiterDashboard extends JPanel {
    private final RecruiterService recruiterService = new RecruiterService();
    private final CompanyDAO companyDAO = new CompanyDAO();
    
    private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    private Recruiter currentRecruiter;
    private Company currentCompany;

    // Company Profile Components
    private JTextField txtCompName, txtCompWeb, txtCompInd;
    private JTextArea txtCompDesc;
    
    // Create Drive Components
    private JTextField txtDriveTitle, txtDriveRole, txtDrivePkg, txtDriveMinCgpa, txtDriveMaxBacklogs, txtDriveDate;
    private JTextArea txtDriveDesc;

    // Tables
    private JTable tblDrives;
    private DefaultTableModel modelDrives;
    private JTable tblApplications;
    private DefaultTableModel modelApplications;
    private JTable tblRounds;
    private DefaultTableModel modelRounds;
    private JTable tblSelections;
    private DefaultTableModel modelSelections;

    public RecruiterDashboard() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_SOFT);

        currentRecruiter = SessionManager.getRecruiter();
        loadCompany();

        add(tabbedPane, BorderLayout.CENTER);

        initCompanyProfileTab();
        initCreateDriveTab();
        initManageDrivesTab();
        initApplicationsTab();
        initInterviewsTab();
        initSelectionsTab();

        refreshData();
    }

    private void loadCompany() {
        if (currentRecruiter == null) return;
        try {
            currentCompany = companyDAO.getById(currentRecruiter.getCompanyId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        loadCompany();
        populateCompanyProfile();
        populateDrivesTable();
        populateApplicationsTable();
        populateRoundsTable();
        populateSelectionsTable();
    }

    private void initCompanyProfileTab() {
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
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelper.createSubHeader("Manage Corporate Profile"), gbc);
        gbc.gridwidth = 1;

        // Fields
        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Company Name*:"), gbc);
        txtCompName = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtCompName, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Website:"), gbc);
        txtCompWeb = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtCompWeb, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Industry / Domain:"), gbc);
        txtCompInd = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtCompInd, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Description:"), gbc);
        txtCompDesc = new JTextArea(6, 20);
        txtCompDesc.setFont(UIHelper.FONT_BODY);
        txtCompDesc.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER));
        gbc.gridx = 1; formPanel.add(new JScrollPane(txtCompDesc), gbc);

        // Save Button
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        JButton btnSave = UIHelper.createPrimaryButton("Save Corporate Details");
        formPanel.add(btnSave, gbc);

        panel.add(formPanel, BorderLayout.NORTH);
        tabbedPane.addTab("Company Profile", panel);

        btnSave.addActionListener(e -> saveCompanyProfile());
    }

    private void populateCompanyProfile() {
        if (currentCompany == null) return;
        txtCompName.setText(currentCompany.getName());
        txtCompWeb.setText(currentCompany.getWebsite());
        txtCompInd.setText(currentCompany.getIndustry());
        txtCompDesc.setText(currentCompany.getDescription());
    }

    private void saveCompanyProfile() {
        if (currentCompany == null) return;
        try {
            String name = txtCompName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentCompany.setName(name);
            currentCompany.setWebsite(txtCompWeb.getText().trim());
            currentCompany.setIndustry(txtCompInd.getText().trim());
            currentCompany.setDescription(txtCompDesc.getText().trim());

            boolean success = companyDAO.update(currentCompany);
            if (success) {
                JOptionPane.showMessageDialog(this, "Corporate details saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save corporate details.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCreateDriveTab() {
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
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(UIHelper.createSubHeader("Post a New Placement Drive"), gbc);
        gbc.gridwidth = 1;

        // Fields
        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Drive Title*:"), gbc);
        txtDriveTitle = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtDriveTitle, gbc);

        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Job Role*:"), gbc);
        txtDriveRole = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtDriveRole, gbc);

        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Package (LPA)*:"), gbc);
        txtDrivePkg = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtDrivePkg, gbc);

        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Minimum CGPA*:"), gbc);
        txtDriveMinCgpa = UIHelper.createTextField(); txtDriveMinCgpa.setText("0.00"); gbc.gridx = 1; formPanel.add(txtDriveMinCgpa, gbc);

        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Maximum Backlogs Allowed*:"), gbc);
        txtDriveMaxBacklogs = UIHelper.createTextField(); txtDriveMaxBacklogs.setText("0"); gbc.gridx = 1; formPanel.add(txtDriveMaxBacklogs, gbc);

        gbc.gridy = 6; gbc.gridx = 0; formPanel.add(new JLabel("Drive Date (YYYY-MM-DD)*:"), gbc);
        txtDriveDate = UIHelper.createTextField(); gbc.gridx = 1; formPanel.add(txtDriveDate, gbc);

        gbc.gridy = 7; gbc.gridx = 0; formPanel.add(new JLabel("Job Description:"), gbc);
        txtDriveDesc = new JTextArea(4, 20);
        txtDriveDesc.setFont(UIHelper.FONT_BODY);
        txtDriveDesc.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER));
        gbc.gridx = 1; formPanel.add(new JScrollPane(txtDriveDesc), gbc);

        // Submit Button
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        JButton btnPublish = UIHelper.createPrimaryButton("Publish Drive & Broadcast to Students");
        formPanel.add(btnPublish, gbc);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Post Placement Drive", panel);

        btnPublish.addActionListener(e -> publishDrive());
    }

    private void publishDrive() {
        if (currentCompany == null) {
            JOptionPane.showMessageDialog(this, "Company context not loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            PlacementDrive pd = new PlacementDrive();
            pd.setCompanyId(currentCompany.getId());
            pd.setTitle(txtDriveTitle.getText().trim());
            pd.setJobRole(txtDriveRole.getText().trim());
            pd.setPackageLpa(Double.parseDouble(txtDrivePkg.getText().trim()));
            pd.setMinCgpa(Double.parseDouble(txtDriveMinCgpa.getText().trim()));
            pd.setMaxBacklogs(Integer.parseInt(txtDriveMaxBacklogs.getText().trim()));
            pd.setDriveDate(Date.valueOf(txtDriveDate.getText().trim()));
            pd.setJobDescription(txtDriveDesc.getText().trim());
            pd.setStatus("UPCOMING");

            recruiterService.createDrive(pd);
            JOptionPane.showMessageDialog(this, "Placement drive published and broadcasted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            txtDriveTitle.setText("");
            txtDriveRole.setText("");
            txtDrivePkg.setText("");
            txtDriveMinCgpa.setText("0.00");
            txtDriveMaxBacklogs.setText("0");
            txtDriveDate.setText("");
            txtDriveDesc.setText("");

            // Switch to drives list tab
            tabbedPane.setSelectedIndex(2);
            refreshData();

        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, "Please verify numbers and make sure drive date follows YYYY-MM-DD.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initManageDrivesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Manage Active Placement Drives"), BorderLayout.NORTH);

        String[] cols = {"Drive ID", "Title", "Job Role", "Package (LPA)", "Min CGPA", "Max Backlogs", "Date", "Status"};
        modelDrives = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelDrives);
        tblDrives = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controls.setOpaque(false);
        JButton btnCloseDrive = UIHelper.createSecondaryButton("Complete/Close Drive");
        controls.add(btnCloseDrive);
        panel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("Manage Drives", panel);

        btnCloseDrive.addActionListener(e -> {
            int row = tblDrives.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a drive from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) tblDrives.getValueAt(row, 0);
            try {
                dao.PlacementDriveDAO pdao = new dao.PlacementDriveDAO();
                PlacementDrive pd = pdao.getById(id);
                pd.setStatus("COMPLETED");
                recruiterService.updateDrive(pd);
                JOptionPane.showMessageDialog(this, "Drive completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void populateDrivesTable() {
        if (currentCompany == null) return;
        try {
            modelDrives.setRowCount(0);
            List<PlacementDrive> list = recruiterService.getDrivesByCompany(currentCompany.getId());
            for (PlacementDrive pd : list) {
                modelDrives.addRow(new Object[]{
                    pd.getId(),
                    pd.getTitle(),
                    pd.getJobRole(),
                    pd.getPackageLpa(),
                    pd.getMinCgpa(),
                    pd.getMaxBacklogs(),
                    pd.getDriveDate(),
                    pd.getStatus()
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

        panel.add(UIHelper.createSubHeader("Applications Received for Your Drives"), BorderLayout.NORTH);

        String[] cols = {"App ID", "Drive Title", "Student Name", "Roll Number", "CGPA", "Status", "Applied Date"};
        modelApplications = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelApplications);
        tblApplications = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        controls.setOpaque(false);
        JButton btnShortlist = UIHelper.createSecondaryButton("Shortlist");
        JButton btnReject = UIHelper.createSecondaryButton("Reject");
        JButton btnSchedule = UIHelper.createPrimaryButton("Schedule Interview");
        JButton btnSelect = UIHelper.createPrimaryButton("Offer Job Selection");
        
        controls.add(btnShortlist);
        controls.add(btnReject);
        controls.add(btnSchedule);
        controls.add(btnSelect);
        panel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("Received Applications", panel);

        // Listeners
        btnShortlist.addActionListener(e -> updateAppStatus(true));
        btnReject.addActionListener(e -> updateAppStatus(false));
        btnSchedule.addActionListener(e -> scheduleInterviewInline());
        btnSelect.addActionListener(e -> makeSelectionInline());
    }

    private void populateApplicationsTable() {
        if (currentCompany == null) return;
        try {
            modelApplications.setRowCount(0);
            List<Application> list = recruiterService.getApplicationsForCompany(currentCompany.getId());
            for (Application app : list) {
                modelApplications.addRow(new Object[]{
                    app.getId(),
                    app.getDriveTitle(),
                    app.getStudentName(),
                    app.getStudentRoll(),
                    app.getStudentCgpa(),
                    app.getStatus(),
                    app.getAppliedDate()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAppStatus(boolean shortlist) {
        int row = tblApplications.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate application first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tblApplications.getValueAt(row, 0);
        try {
            if (shortlist) {
                recruiterService.shortlistCandidate(id);
                JOptionPane.showMessageDialog(this, "Candidate shortlisted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                recruiterService.rejectCandidate(id);
                JOptionPane.showMessageDialog(this, "Candidate application rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshData();
        } catch (DatabaseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void scheduleInterviewInline() {
        int row = tblApplications.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate application first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appId = (int) tblApplications.getValueAt(row, 0);

        JTextField numField = new JTextField("1");
        JTextField nameField = new JTextField("Technical Coding Round");
        JTextField dateField = new JTextField("YYYY-MM-DD HH:MM:SS");

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Round Number:"));
        form.add(numField);
        form.add(new JLabel("Round Name:"));
        form.add(nameField);
        form.add(new JLabel("Date & Time (YYYY-MM-DD HH:MM:SS):"));
        form.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, form, "Schedule Interview Round", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                InterviewRound ir = new InterviewRound();
                ir.setApplicationId(appId);
                ir.setRoundNumber(Integer.parseInt(numField.getText().trim()));
                ir.setRoundName(nameField.getText().trim());
                ir.setRoundDate(Timestamp.valueOf(dateField.getText().trim()));
                ir.setStatus("SCHEDULED");

                recruiterService.scheduleInterviewRound(ir);
                JOptionPane.showMessageDialog(this, "Interview scheduled and candidate notified!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error scheduling: Make sure date/time format is exact YYYY-MM-DD HH:MM:SS", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void makeSelectionInline() {
        int row = tblApplications.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a candidate application first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int appId = (int) tblApplications.getValueAt(row, 0);

        try {
            dao.ApplicationDAO adao = new dao.ApplicationDAO();
            Application app = adao.getById(appId);

            JTextField pkgField = new JTextField(String.valueOf(app.getPackageLpa()));
            JTextField dateField = new JTextField(new java.sql.Date(System.currentTimeMillis()).toString());

            JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
            form.add(new JLabel("Offered Package (LPA):"));
            form.add(pkgField);
            form.add(new JLabel("Offer Date (YYYY-MM-DD):"));
            form.add(dateField);

            int result = JOptionPane.showConfirmDialog(this, form, "Extend Job Selection Offer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Selection sel = new Selection();
                sel.setStudentId(app.getStudentId());
                sel.setDriveId(app.getDriveId());
                sel.setApplicationId(appId);
                sel.setOfferedPackageLpa(Double.parseDouble(pkgField.getText().trim()));
                sel.setSelectionDate(Date.valueOf(dateField.getText().trim()));
                sel.setStatus("ACCEPTED"); // Accepted by default

                recruiterService.selectCandidate(sel);
                JOptionPane.showMessageDialog(this, "Selection saved and student notified!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error offering selection: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initInterviewsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Manage Interview Rounds"), BorderLayout.NORTH);

        String[] cols = {"Round ID", "Student Name", "Drive Title", "Round No.", "Round Name", "Date/Time", "Status", "Feedback"};
        modelRounds = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelRounds);
        tblRounds = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        // Control Panel
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controls.setOpaque(false);
        JButton btnPass = UIHelper.createPrimaryButton("Mark Passed");
        JButton btnFail = UIHelper.createSecondaryButton("Mark Failed");
        
        controls.add(btnFail);
        controls.add(btnPass);
        panel.add(controls, BorderLayout.SOUTH);

        tabbedPane.addTab("Interview Rounds", panel);

        // Listeners
        btnPass.addActionListener(e -> updateRoundStatus(true));
        btnFail.addActionListener(e -> updateRoundStatus(false));
    }

    private void populateRoundsTable() {
        if (currentCompany == null) return;
        try {
            modelRounds.setRowCount(0);
            dao.InterviewRoundDAO irdao = new dao.InterviewRoundDAO();
            List<InterviewRound> list = irdao.getForCompany(currentCompany.getId());
            for (InterviewRound ir : list) {
                modelRounds.addRow(new Object[]{
                    ir.getId(),
                    ir.getStudentName(),
                    ir.getDriveTitle(),
                    ir.getRoundNumber(),
                    ir.getRoundName(),
                    ir.getRoundDate(),
                    ir.getStatus(),
                    ir.getFeedback() == null ? "-" : ir.getFeedback()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRoundStatus(boolean passed) {
        int row = tblRounds.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an interview round row first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int roundId = (int) tblRounds.getValueAt(row, 0);

        String feedback = JOptionPane.showInputDialog(this, "Enter round feedback:", "Round Feedback", JOptionPane.PLAIN_MESSAGE);
        if (feedback == null) return; // cancelled

        try {
            dao.InterviewRoundDAO irdao = new dao.InterviewRoundDAO();
            // Fetch round object
            InterviewRound irObj = null;
            if (currentCompany != null) {
                List<InterviewRound> list = irdao.getForCompany(currentCompany.getId());
                for (InterviewRound r : list) {
                    if (r.getId() == roundId) {
                        irObj = r;
                        break;
                    }
                }
            }

            if (irObj != null) {
                irObj.setStatus(passed ? "PASSED" : "FAILED");
                irObj.setFeedback(feedback);
                recruiterService.updateInterviewRound(irObj);
                JOptionPane.showMessageDialog(this, "Round results updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating round: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initSelectionsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.WHITE);
        panel.setBorder(new EmptyBorder(25, 40, 25, 40));

        panel.add(UIHelper.createSubHeader("Corporate Job Selections List"), BorderLayout.NORTH);

        String[] cols = {"Student Name", "Roll Number", "Department", "Drive Job Title", "Package (LPA)", "Offer Date", "Status"};
        modelSelections = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JScrollPane scroll = UIHelper.createTable(modelSelections);
        tblSelections = (JTable) scroll.getViewport().getView();

        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("Selected Candidates", panel);
    }

    private void populateSelectionsTable() {
        if (currentCompany == null) return;
        try {
            modelSelections.setRowCount(0);
            dao.SelectionDAO sdao = new dao.SelectionDAO();
            List<Selection> list = sdao.getByCompany(currentCompany.getId());
            for (Selection s : list) {
                modelSelections.addRow(new Object[]{
                    s.getStudentName(),
                    s.getStudentRoll(),
                    s.getDepartmentName() == null ? "General" : s.getDepartmentName(),
                    s.getDriveTitle() + " (" + s.getJobRole() + ")",
                    s.getOfferedPackageLpa(),
                    s.getSelectionDate(),
                    s.getStatus()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
