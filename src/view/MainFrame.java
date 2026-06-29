package view;

import utility.SessionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainContainer = new JPanel(cardLayout);
    private JPanel sidebarPanel;
    private JLabel lblUserName;
    private JLabel lblUserRole;
    private JPanel navButtonsPanel;

    private StudentDashboard studentDashboard;
    private RecruiterDashboard recruiterDashboard;
    private AdminDashboard adminDashboard;
    private LoginView loginView;

    public MainFrame() {
        setTitle("SPaRMS - Smart Placement & Recruitment Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        initSidebar();
        
        loginView = new LoginView(this::handleLoginSuccess);
        mainContainer.add(loginView, "LOGIN");

        add(mainContainer, BorderLayout.CENTER);

        // Hide sidebar initially on login screen
        sidebarPanel.setVisible(false);
        add(sidebarPanel, BorderLayout.WEST);

        cardLayout.show(mainContainer, "LOGIN");
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setBackground(UIHelper.NAVY);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Logo / Title
        JLabel lblLogo = new JLabel("SPaRMS");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblLogo.setForeground(UIHelper.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblLogo);

        JLabel lblTagline = new JLabel("Recruitment Portal");
        lblTagline.setFont(UIHelper.FONT_SMALL);
        lblTagline.setForeground(new Color(0x94, 0xA3, 0xB8)); // Gray-400
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTagline.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebarPanel.add(lblTagline);

        sidebarPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // User profile card in sidebar
        lblUserName = new JLabel("User Name");
        lblUserName.setFont(UIHelper.FONT_BOLD);
        lblUserName.setForeground(UIHelper.WHITE);
        lblUserName.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblUserName);

        lblUserRole = new JLabel("ROLE_BADGE");
        lblUserRole.setFont(UIHelper.FONT_SMALL);
        lblUserRole.setForeground(new Color(0x38, 0xBD, 0xF8)); // Light blue Accent
        lblUserRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUserRole.setBorder(new EmptyBorder(4, 0, 15, 0));
        sidebarPanel.add(lblUserRole);

        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Navigation Buttons Container
        navButtonsPanel = new JPanel();
        navButtonsPanel.setOpaque(false);
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        sidebarPanel.add(navButtonsPanel);

        // Push everything up
        sidebarPanel.add(Box.createVerticalGlue());

        // Logout Button
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(UIHelper.FONT_BOLD);
        btnLogout.setForeground(UIHelper.WHITE);
        btnLogout.setBackground(new Color(0xEF, 0x44, 0x44)); // Red-500
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> handleLogout());
        sidebarPanel.add(btnLogout);
    }

    private void handleLoginSuccess() {
        // Load appropriate Dashboard
        SessionManager.Role role = SessionManager.getCurrentRole();
        Object user = SessionManager.getCurrentUser();

        navButtonsPanel.removeAll();

        if (role == SessionManager.Role.ADMIN) {
            model.Admin admin = (model.Admin) user;
            lblUserName.setText(admin.getName());
            lblUserRole.setText("Placement Officer");

            adminDashboard = new AdminDashboard();
            mainContainer.add(adminDashboard, "ADMIN");
            cardLayout.show(mainContainer, "ADMIN");

            // Setup Admin sidebar buttons
            addSidebarNavButton("Dashboard Summary", 0, "ADMIN");
            addSidebarNavButton("Students Directory", 1, "ADMIN");
            addSidebarNavButton("Companies Directory", 2, "ADMIN");
            addSidebarNavButton("Database Backup", 3, "ADMIN");

        } else if (role == SessionManager.Role.RECRUITER) {
            model.Recruiter rec = (model.Recruiter) user;
            lblUserName.setText(rec.getName());
            lblUserRole.setText("Recruiter");

            recruiterDashboard = new RecruiterDashboard();
            mainContainer.add(recruiterDashboard, "RECRUITER");
            cardLayout.show(mainContainer, "RECRUITER");

            // Setup Recruiter sidebar buttons
            addSidebarNavButton("Company Profile", 0, "RECRUITER");
            addSidebarNavButton("Post Job Drive", 1, "RECRUITER");
            addSidebarNavButton("Manage Drives", 2, "RECRUITER");
            addSidebarNavButton("Applications", 3, "RECRUITER");
            addSidebarNavButton("Interview Rounds", 4, "RECRUITER");
            addSidebarNavButton("Selections List", 5, "RECRUITER");

        } else if (role == SessionManager.Role.STUDENT) {
            model.Student stud = (model.Student) user;
            lblUserName.setText(stud.getName());
            lblUserRole.setText("Student (" + stud.getRollNumber() + ")");

            studentDashboard = new StudentDashboard();
            mainContainer.add(studentDashboard, "STUDENT");
            cardLayout.show(mainContainer, "STUDENT");

            // Setup Student sidebar buttons
            addSidebarNavButton("My Profile", 0, "STUDENT");
            addSidebarNavButton("Technical Skills", 1, "STUDENT");
            addSidebarNavButton("Placement Drives", 2, "STUDENT");
            addSidebarNavButton("Applied Drives", 3, "STUDENT");
            addSidebarNavButton("Interviews Schedule", 4, "STUDENT");
            addSidebarNavButton("Notifications Alert", 5, "STUDENT");
        }

        sidebarPanel.setVisible(true);
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
        revalidate();
        repaint();
    }

    private void addSidebarNavButton(String text, final int tabIndex, final String dashboardType) {
        JButton btn = new JButton(text);
        btn.setFont(UIHelper.FONT_BODY);
        btn.setForeground(new Color(0xE2, 0xE8, 0xF0)); // slate-200
        btn.setBackground(UIHelper.NAVY);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 40));

        btn.addActionListener(e -> {
            if ("ADMIN".equals(dashboardType) && adminDashboard != null) {
                adminDashboard.refreshData();
                JTabbedPane tp = (JTabbedPane) adminDashboard.getComponent(0);
                tp.setSelectedIndex(tabIndex);
            } else if ("RECRUITER".equals(dashboardType) && recruiterDashboard != null) {
                recruiterDashboard.refreshData();
                JTabbedPane tp = (JTabbedPane) recruiterDashboard.getComponent(0);
                tp.setSelectedIndex(tabIndex);
            } else if ("STUDENT".equals(dashboardType) && studentDashboard != null) {
                studentDashboard.refreshData();
                JTabbedPane tp = (JTabbedPane) studentDashboard.getComponent(0);
                tp.setSelectedIndex(tabIndex);
            }
        });

        // Hover styling
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIHelper.BLUE);
                btn.setForeground(UIHelper.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(UIHelper.NAVY);
                btn.setForeground(new Color(0xE2, 0xE8, 0xF0));
            }
        });

        navButtonsPanel.add(btn);
        navButtonsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    private void handleLogout() {
        SessionManager.logout();
        
        // Remove dashboard panels
        mainContainer.remove(loginView);
        if (studentDashboard != null) mainContainer.remove(studentDashboard);
        if (recruiterDashboard != null) mainContainer.remove(recruiterDashboard);
        if (adminDashboard != null) mainContainer.remove(adminDashboard);

        studentDashboard = null;
        recruiterDashboard = null;
        adminDashboard = null;

        // Recreate login view
        loginView = new LoginView(this::handleLoginSuccess);
        mainContainer.add(loginView, "LOGIN");

        sidebarPanel.setVisible(false);
        cardLayout.show(mainContainer, "LOGIN");

        revalidate();
        repaint();
    }
}
