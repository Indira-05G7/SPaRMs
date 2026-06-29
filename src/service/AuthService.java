package service;

import dao.AdminDAO;
import dao.RecruiterDAO;
import dao.StudentDAO;
import exception.AuthException;
import exception.ValidationException;
import model.Admin;
import model.Recruiter;
import model.Student;
import utility.InputValidator;
import utility.PasswordHasher;
import utility.SessionManager;

public class AuthService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final RecruiterDAO recruiterDAO = new RecruiterDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public void login(String username, String password, SessionManager.Role role) throws AuthException {
        try {
            String hashedPassword = PasswordHasher.hash(password);
            switch (role) {
                case ADMIN:
                    Admin admin = adminDAO.getByUsername(username);
                    if (admin == null || !admin.getPassword().equals(hashedPassword)) {
                        throw new AuthException("Invalid Admin username or password.");
                    }
                    SessionManager.login(SessionManager.Role.ADMIN, admin);
                    break;
                case RECRUITER:
                    Recruiter recruiter = recruiterDAO.getByUsername(username);
                    if (recruiter == null || !recruiter.getPassword().equals(hashedPassword)) {
                        throw new AuthException("Invalid Recruiter username or password.");
                    }
                    SessionManager.login(SessionManager.Role.RECRUITER, recruiter);
                    break;
                case STUDENT:
                    Student student = studentDAO.getByUsername(username);
                    if (student == null || !student.getPassword().equals(hashedPassword)) {
                        throw new AuthException("Invalid Student username or password.");
                    }
                    SessionManager.login(SessionManager.Role.STUDENT, student);
                    break;
            }
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Authentication database error: " + e.getMessage());
        }
    }

    public void registerStudent(Student student, String password) throws ValidationException, AuthException {
        try {
            InputValidator.validateRequiredString(student.getUsername(), "Username");
            InputValidator.validateRequiredString(student.getName(), "Full Name");
            InputValidator.validateRequiredString(student.getRollNumber(), "Roll Number");
            InputValidator.validateEmail(student.getEmail());
            InputValidator.validateRequiredString(password, "Password");
            
            // Check if username already exists
            if (studentDAO.getByUsername(student.getUsername()) != null) {
                throw new ValidationException("Username already exists.");
            }

            student.setPassword(PasswordHasher.hash(password));
            boolean saved = studentDAO.save(student);
            if (!saved) {
                throw new AuthException("Failed to register student profile.");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Student registration database error: " + e.getMessage());
        }
    }

    public void registerRecruiter(Recruiter recruiter, String password) throws ValidationException, AuthException {
        try {
            InputValidator.validateRequiredString(recruiter.getUsername(), "Username");
            InputValidator.validateRequiredString(recruiter.getName(), "Full Name");
            InputValidator.validateEmail(recruiter.getEmail());
            InputValidator.validateRequiredString(password, "Password");
            if (recruiter.getCompanyId() <= 0) {
                throw new ValidationException("Please select or register a valid company.");
            }

            // Check if username already exists
            if (recruiterDAO.getByUsername(recruiter.getUsername()) != null) {
                throw new ValidationException("Username already exists.");
            }

            recruiter.setPassword(PasswordHasher.hash(password));
            boolean saved = recruiterDAO.save(recruiter);
            if (!saved) {
                throw new AuthException("Failed to register recruiter profile.");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Recruiter registration database error: " + e.getMessage());
        }
    }
}
