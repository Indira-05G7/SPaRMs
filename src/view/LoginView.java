package view;

import dao.CompanyDAO;
import dao.DepartmentDAO;
import exception.AuthException;
import exception.ValidationException;
import model.Company;
import model.Department;
import model.Recruiter;
import model.Student;
import service.AuthService;
import utility.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class LoginView extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel containerPanel = new JPanel(cardLayout);
    private final LoginCallback callback;
    private final AuthService authService = new AuthService();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    // Login Fields
    private JComboBox<SessionManager.Role> comboRole;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Student Reg Fields
    private JTextField txtStudName, txtStudEmail, txtStudPhone, txtStudUser, txtStudRoll, txtStudCgpa, txtStud10, txtStud12, txtStudBacklogs;
    private JPasswordField txtStudPass;
    private JComboBox<Department> comboStudDept;

    // Recruiter Reg Fields
    private JTextField txtRecName, txtRecEmail, txtRecUser;
    private JPasswordField txtRecPass;
    private JComboBox<Company> comboRecCompany;

    public interface LoginCallback {
        void onLoginSuccess();
    }

    public LoginView(LoginCallback callback) {
        this.callback = callback;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_SOFT);

        // Add visual padding around the login card
        setBorder(new EmptyBorder(50, 80, 50, 80));

        // Container Panel
        containerPanel.setOpaque(false);
        add(containerPanel, BorderLayout.CENTER);

        // Initialize Cards
        initLoginCard();
        initStudentRegCard();
        initRecruiterRegCard();

        cardLayout.show(containerPanel, "LOGIN");
    }

    private void initLoginCard() {
        JPanel loginPanel = new JPanel(new GridLayout(1, 2));
        loginPanel.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER, 1));
        loginPanel.setBackground(UIHelper.WHITE);

        // Left Banner Panel
        JPanel bannerPanel = new JPanel(new GridBagLayout());
        bannerPanel.setBackground(UIHelper.NAVY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitle = new JLabel("SPaRMS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(UIHelper.WHITE);
        bannerPanel.add(lblTitle, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.8;
        JLabel lblSubtitle = new JLabel("<html><center>Smart Placement &<br>Recruitment Management System</center></html>");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(0xD1, 0xD5, 0xDB));
        bannerPanel.add(lblSubtitle, gbc);

        loginPanel.add(bannerPanel);

        // Right Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIHelper.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(8, 0, 8, 0);
        fgbc.gridx = 0;
        fgbc.weightx = 1.0;

        JLabel lblHeading = new JLabel("System Login");
        lblHeading.setFont(UIHelper.FONT_TITLE);
        lblHeading.setForeground(UIHelper.NAVY);
        fgbc.gridy = 0;
        formPanel.add(lblHeading, fgbc);

        // Role Combobox
        JLabel lblRole = new JLabel("Login As");
        lblRole.setFont(UIHelper.FONT_BOLD);
        lblRole.setForeground(UIHelper.TEXT_DARK);
        fgbc.gridy = 1;
        formPanel.add(lblRole, fgbc);

        comboRole = new JComboBox<>(SessionManager.Role.values());
        comboRole.setFont(UIHelper.FONT_BODY);
        comboRole.setPreferredSize(new Dimension(200, 35));
        fgbc.gridy = 2;
        formPanel.add(comboRole, fgbc);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIHelper.FONT_BOLD);
        fgbc.gridy = 3;
        formPanel.add(lblUser, fgbc);

        txtUsername = UIHelper.createTextField();
        fgbc.gridy = 4;
        formPanel.add(txtUsername, fgbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIHelper.FONT_BOLD);
        fgbc.gridy = 5;
        formPanel.add(lblPass, fgbc);

        txtPassword = UIHelper.createPasswordField();
        fgbc.gridy = 6;
        formPanel.add(txtPassword, fgbc);

        // Login Button
        JButton btnLogin = UIHelper.createPrimaryButton("Login");
        fgbc.gridy = 7;
        fgbc.insets = new Insets(15, 0, 10, 0);
        formPanel.add(btnLogin, fgbc);

        // Register Student Link
        JLabel lblRegStud = new JLabel("<html><a href='#'>New Student? Register here</a></html>");
        lblRegStud.setFont(UIHelper.FONT_SMALL);
        lblRegStud.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fgbc.gridy = 8;
        fgbc.insets = new Insets(5, 0, 2, 0);
        formPanel.add(lblRegStud, fgbc);

        // Register Recruiter Link
        JLabel lblRegRec = new JLabel("<html><a href='#'>New Recruiter? Register here</a></html>");
        lblRegRec.setFont(UIHelper.FONT_SMALL);
        lblRegRec.setCursor(new Cursor(Cursor.HAND_CURSOR));
        fgbc.gridy = 9;
        fgbc.insets = new Insets(2, 0, 5, 0);
        formPanel.add(lblRegRec, fgbc);

        loginPanel.add(formPanel);
        containerPanel.add(loginPanel, "LOGIN");

        // Action Listeners
        btnLogin.addActionListener(e -> performLogin());
        
        lblRegStud.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadStudentRegDropdowns();
                cardLayout.show(containerPanel, "STUDENT_REG");
            }
        });

        lblRegRec.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadRecruiterRegDropdowns();
                cardLayout.show(containerPanel, "RECRUITER_REG");
            }
        });
    }

    private void initStudentRegCard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER, 1));
        mainPanel.setBackground(UIHelper.WHITE);

        // ScrollPane for fields since there are many
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(UIHelper.WHITE);
        fieldsPanel.setBorder(new EmptyBorder(25, 40, 25, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 0.5;

        // Title Row
        JLabel titleLabel = new JLabel("Student Registration");
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(UIHelper.NAVY);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Name
        gbc.gridy = 1; gbc.gridx = 0; fieldsPanel.add(new JLabel("Full Name*"), gbc);
        txtStudName = UIHelper.createTextField();
        gbc.gridx = 1; fieldsPanel.add(txtStudName, gbc);

        // Roll Number
        gbc.gridy = 2; gbc.gridx = 0; fieldsPanel.add(new JLabel("Roll Number*"), gbc);
        txtStudRoll = UIHelper.createTextField();
        gbc.gridx = 1; fieldsPanel.add(txtStudRoll, gbc);

        // Email
        gbc.gridy = 3; gbc.gridx = 0; fieldsPanel.add(new JLabel("Email*"), gbc);
        txtStudEmail = UIHelper.createTextField();
        gbc.gridx = 1; fieldsPanel.add(txtStudEmail, gbc);

        // Phone
        gbc.gridy = 4; gbc.gridx = 0; fieldsPanel.add(new JLabel("Phone Number"), gbc);
        txtStudPhone = UIHelper.createTextField();
        gbc.gridx = 1; fieldsPanel.add(txtStudPhone, gbc);

        // Username
        gbc.gridy = 5; gbc.gridx = 0; fieldsPanel.add(new JLabel("Username*"), gbc);
        txtStudUser = UIHelper.createTextField();
        gbc.gridx = 1; fieldsPanel.add(txtStudUser, gbc);

        // Password
        gbc.gridy = 6; gbc.gridx = 0; fieldsPanel.add(new JLabel("Password*"), gbc);
        txtStudPass = UIHelper.createPasswordField();
        gbc.gridx = 1; fieldsPanel.add(txtStudPass, gbc);

        // Department
        gbc.gridy = 7; gbc.gridx = 0; fieldsPanel.add(new JLabel("Department*"), gbc);
        comboStudDept = new JComboBox<>();
        comboStudDept.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1; fieldsPanel.add(comboStudDept, gbc);

        // CGPA
        gbc.gridy = 8; gbc.gridx = 0; fieldsPanel.add(new JLabel("Current CGPA*"), gbc);
        txtStudCgpa = UIHelper.createTextField();
        txtStudCgpa.setText("0.00");
        gbc.gridx = 1; fieldsPanel.add(txtStudCgpa, gbc);

        // Class 10 & 12
        gbc.gridy = 9; gbc.gridx = 0; fieldsPanel.add(new JLabel("Class 10 %*"), gbc);
        txtStud10 = UIHelper.createTextField();
        txtStud10.setText("0.00");
        gbc.gridx = 1; fieldsPanel.add(txtStud10, gbc);

        gbc.gridy = 10; gbc.gridx = 0; fieldsPanel.add(new JLabel("Class 12 %*"), gbc);
        txtStud12 = UIHelper.createTextField();
        txtStud12.setText("0.00");
        gbc.gridx = 1; fieldsPanel.add(txtStud12, gbc);

        // Backlogs
        gbc.gridy = 11; gbc.gridx = 0; fieldsPanel.add(new JLabel("Active Backlogs*"), gbc);
        txtStudBacklogs = UIHelper.createTextField();
        txtStudBacklogs.setText("0");
        gbc.gridx = 1; fieldsPanel.add(txtStudBacklogs, gbc);

        // Buttons
        gbc.gridy = 12;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        JButton btnRegister = UIHelper.createPrimaryButton("Register");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRegister);
        fieldsPanel.add(buttonPanel, gbc);

        JScrollPane scroll = new JScrollPane(fieldsPanel);
        scroll.setBorder(null);
        mainPanel.add(scroll, BorderLayout.CENTER);

        containerPanel.add(mainPanel, "STUDENT_REG");

        // Listeners
        btnCancel.addActionListener(e -> cardLayout.show(containerPanel, "LOGIN"));
        btnRegister.addActionListener(e -> performStudentRegistration());
    }

    private void initRecruiterRegCard() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER, 1));
        formPanel.setBackground(UIHelper.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Title
        JLabel titleLabel = new JLabel("Recruiter Registration");
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(UIHelper.NAVY);
        gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // Name
        gbc.gridy = 1; gbc.gridx = 0; formPanel.add(new JLabel("Full Name*"), gbc);
        txtRecName = UIHelper.createTextField();
        gbc.gridx = 1; formPanel.add(txtRecName, gbc);

        // Email
        gbc.gridy = 2; gbc.gridx = 0; formPanel.add(new JLabel("Email*"), gbc);
        txtRecEmail = UIHelper.createTextField();
        gbc.gridx = 1; formPanel.add(txtRecEmail, gbc);

        // Username
        gbc.gridy = 3; gbc.gridx = 0; formPanel.add(new JLabel("Username*"), gbc);
        txtRecUser = UIHelper.createTextField();
        gbc.gridx = 1; formPanel.add(txtRecUser, gbc);

        // Password
        gbc.gridy = 4; gbc.gridx = 0; formPanel.add(new JLabel("Password*"), gbc);
        txtRecPass = UIHelper.createPasswordField();
        gbc.gridx = 1; formPanel.add(txtRecPass, gbc);

        // Company
        gbc.gridy = 5; gbc.gridx = 0; formPanel.add(new JLabel("Representing Company*"), gbc);
        
        JPanel compPanel = new JPanel(new BorderLayout(5, 0));
        compPanel.setOpaque(false);
        comboRecCompany = new JComboBox<>();
        compPanel.add(comboRecCompany, BorderLayout.CENTER);
        JButton btnAddCompany = new JButton("+");
        btnAddCompany.setToolTipText("Register New Company");
        compPanel.add(btnAddCompany, BorderLayout.EAST);
        gbc.gridx = 1; formPanel.add(compPanel, gbc);

        // Buttons
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        JButton btnRegister = UIHelper.createPrimaryButton("Register");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnRegister);
        formPanel.add(buttonPanel, gbc);

        containerPanel.add(formPanel, "RECRUITER_REG");

        // Listeners
        btnCancel.addActionListener(e -> cardLayout.show(containerPanel, "LOGIN"));
        btnRegister.addActionListener(e -> performRecruiterRegistration());
        btnAddCompany.addActionListener(e -> addNewCompanyInline());
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        SessionManager.Role role = (SessionManager.Role) comboRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            authService.login(username, password, role);
            callback.onLoginSuccess();
        } catch (AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudentRegDropdowns() {
        try {
            comboStudDept.removeAllItems();
            List<Department> list = departmentDAO.getAll();
            for (Department d : list) {
                comboStudDept.addItem(d);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecruiterRegDropdowns() {
        try {
            comboRecCompany.removeAllItems();
            List<Company> list = companyDAO.getAll();
            for (Company c : list) {
                comboRecCompany.addItem(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading companies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performStudentRegistration() {
        try {
            Student s = new Student();
            s.setName(txtStudName.getText().trim());
            s.setEmail(txtStudEmail.getText().trim());
            s.setPhone(txtStudPhone.getText().trim());
            s.setUsername(txtStudUser.getText().trim());
            s.setRollNumber(txtStudRoll.getText().trim());
            
            Department dept = (Department) comboStudDept.getSelectedItem();
            if (dept != null) {
                s.setDepartmentId(dept.getId());
            }

            s.setCgpa(Double.parseDouble(txtStudCgpa.getText().trim()));
            s.setTenthPercentage(Double.parseDouble(txtStud10.getText().trim()));
            s.setTwelfthPercentage(Double.parseDouble(txtStud12.getText().trim()));
            s.setActiveBacklogs(Integer.parseInt(txtStudBacklogs.getText().trim()));

            String pass = new String(txtStudPass.getPassword()).trim();

            authService.registerStudent(s, pass);

            JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(containerPanel, "LOGIN");

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "CGPA, Percentages, and Backlogs must be valid numeric values.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (ValidationException | AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRecruiterRegistration() {
        try {
            Recruiter r = new Recruiter();
            r.setName(txtRecName.getText().trim());
            r.setEmail(txtRecEmail.getText().trim());
            r.setUsername(txtRecUser.getText().trim());

            Company company = (Company) comboRecCompany.getSelectedItem();
            if (company != null) {
                r.setCompanyId(company.getId());
            }

            String pass = new String(txtRecPass.getPassword()).trim();

            authService.registerRecruiter(r, pass);

            JOptionPane.showMessageDialog(this, "Registration Successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(containerPanel, "LOGIN");

        } catch (ValidationException | AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewCompanyInline() {
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
                    loadRecruiterRegDropdowns();
                    // Select the newly added company
                    for (int i = 0; i < comboRecCompany.getItemCount(); i++) {
                        Company c = comboRecCompany.getItemAt(i);
                        if (c.getName().equals(name)) {
                            comboRecCompany.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving company: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
