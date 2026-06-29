package service;

import dao.ApplicationDAO;
import dao.InterviewRoundDAO;
import dao.NotificationDAO;
import dao.PlacementDriveDAO;
import dao.SkillDAO;
import dao.StudentDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Application;
import model.InterviewRound;
import model.Notification;
import model.PlacementDrive;
import model.Skill;
import model.Student;
import utility.InputValidator;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO = new StudentDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final PlacementDriveDAO driveDAO = new PlacementDriveDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final InterviewRoundDAO roundDAO = new InterviewRoundDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void updateProfile(Student student) throws ValidationException, DatabaseException {
        try {
            InputValidator.validateRequiredString(student.getName(), "Name");
            InputValidator.validateEmail(student.getEmail());
            InputValidator.validatePhone(student.getPhone());
            InputValidator.validateCgpa(student.getCgpa());
            InputValidator.validatePercentage(student.getTenthPercentage(), "Class 10");
            InputValidator.validatePercentage(student.getTwelfthPercentage(), "Class 12");
            InputValidator.validateBacklogs(student.getActiveBacklogs());

            boolean success = studentDAO.update(student);
            if (!success) {
                throw new DatabaseException("Failed to update student profile in database.");
            }
        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error during profile update: " + e.getMessage(), e);
        }
    }

    public List<Skill> getStudentSkills(int studentId) throws DatabaseException {
        try {
            return skillDAO.getForStudent(studentId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve student skills: " + e.getMessage(), e);
        }
    }

    public void updateStudentSkills(int studentId, List<Skill> skills) throws DatabaseException {
        try {
            skillDAO.saveStudentSkills(studentId, skills);
        } catch (Exception e) {
            throw new DatabaseException("Failed to update student skills: " + e.getMessage(), e);
        }
    }

    public boolean isEligible(Student student, PlacementDrive drive) {
        if (student == null || drive == null) return false;
        return student.getCgpa() >= drive.getMinCgpa() &&
               student.getActiveBacklogs() <= drive.getMaxBacklogs();
    }

    public void applyToDrive(int studentId, int driveId) throws ValidationException, DatabaseException {
        try {
            Student student = studentDAO.getById(studentId);
            PlacementDrive drive = driveDAO.getById(driveId);

            if (student == null || drive == null) {
                throw new ValidationException("Invalid student or placement drive.");
            }

            // Check eligibility
            if (!isEligible(student, drive)) {
                throw new ValidationException("You do not meet the eligibility criteria (CGPA/Backlogs) for this drive.");
            }

            // Check duplicate application
            if (applicationDAO.exists(studentId, driveId)) {
                throw new ValidationException("You have already applied to this placement drive.");
            }

            // Submit application
            Application app = new Application();
            app.setStudentId(studentId);
            app.setDriveId(driveId);
            app.setStatus("APPLIED");

            boolean success = applicationDAO.save(app);
            if (!success) {
                throw new DatabaseException("Failed to submit application.");
            }

            // Create notification
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(studentId);
            notif.setTitle("Application Submitted");
            notif.setMessage("You successfully applied to " + drive.getCompanyName() + " for " + drive.getJobRole() + ".");
            notificationDAO.save(notif);

        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error submitting application: " + e.getMessage(), e);
        }
    }

    public List<Application> getAppliedDrives(int studentId) throws DatabaseException {
        try {
            return applicationDAO.getByStudent(studentId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to get applied drives: " + e.getMessage(), e);
        }
    }

    public List<InterviewRound> getInterviewSchedule(int studentId) throws DatabaseException {
        try {
            return roundDAO.getForStudent(studentId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to get interview schedule: " + e.getMessage(), e);
        }
    }
}
