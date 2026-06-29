package service;

import dao.ApplicationDAO;
import dao.InterviewRoundDAO;
import dao.NotificationDAO;
import dao.PlacementDriveDAO;
import dao.SelectionDAO;
import exception.DatabaseException;
import exception.ValidationException;
import model.Application;
import model.InterviewRound;
import model.Notification;
import model.PlacementDrive;
import model.Selection;
import utility.InputValidator;
import java.sql.Date;
import java.util.List;

public class RecruiterService {
    private final PlacementDriveDAO driveDAO = new PlacementDriveDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final InterviewRoundDAO roundDAO = new InterviewRoundDAO();
    private final SelectionDAO selectionDAO = new SelectionDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void createDrive(PlacementDrive drive) throws ValidationException, DatabaseException {
        try {
            InputValidator.validateRequiredString(drive.getTitle(), "Drive Title");
            InputValidator.validateRequiredString(drive.getJobRole(), "Job Role");
            InputValidator.validatePackage(drive.getPackageLpa());
            InputValidator.validateCgpa(drive.getMinCgpa());
            InputValidator.validateBacklogs(drive.getMaxBacklogs());
            if (drive.getDriveDate() == null) {
                throw new ValidationException("Drive Date is required.");
            }

            boolean success = driveDAO.save(drive);
            if (!success) {
                throw new DatabaseException("Failed to save placement drive.");
            }

            // Create global broadcast notification
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(null); // Broadcast
            notif.setTitle("New Placement Drive: " + drive.getTitle());
            notif.setMessage("A new placement drive has been posted for the role of " + drive.getJobRole() + 
                             ". Packages up to " + drive.getPackageLpa() + " LPA. Date: " + drive.getDriveDate());
            notificationDAO.save(notif);

        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error creating drive: " + e.getMessage(), e);
        }
    }

    public void updateDrive(PlacementDrive drive) throws ValidationException, DatabaseException {
        try {
            InputValidator.validateRequiredString(drive.getTitle(), "Drive Title");
            InputValidator.validateRequiredString(drive.getJobRole(), "Job Role");
            InputValidator.validatePackage(drive.getPackageLpa());
            InputValidator.validateCgpa(drive.getMinCgpa());
            InputValidator.validateBacklogs(drive.getMaxBacklogs());

            boolean success = driveDAO.update(drive);
            if (!success) {
                throw new DatabaseException("Failed to update placement drive.");
            }
        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error updating drive: " + e.getMessage(), e);
        }
    }

    public List<PlacementDrive> getDrivesByCompany(int companyId) throws DatabaseException {
        try {
            return driveDAO.getDrivesByCompany(companyId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve company drives: " + e.getMessage(), e);
        }
    }

    public List<Application> getApplicationsForCompany(int companyId) throws DatabaseException {
        try {
            return applicationDAO.getByCompany(companyId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to retrieve applications: " + e.getMessage(), e);
        }
    }

    public void shortlistCandidate(int applicationId) throws DatabaseException {
        try {
            boolean success = applicationDAO.updateStatus(applicationId, "SHORTLISTED");
            if (!success) throw new DatabaseException("Failed to update application status.");

            Application app = applicationDAO.getById(applicationId);
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(app.getStudentId());
            notif.setTitle("Application Shortlisted");
            notif.setMessage("Congratulations! Your application for " + app.getCompanyName() + " (" + app.getJobRole() + ") has been shortlisted.");
            notificationDAO.save(notif);
        } catch (Exception e) {
            throw new DatabaseException("Database error shortlisting candidate: " + e.getMessage(), e);
        }
    }

    public void rejectCandidate(int applicationId) throws DatabaseException {
        try {
            boolean success = applicationDAO.updateStatus(applicationId, "REJECTED");
            if (!success) throw new DatabaseException("Failed to update application status.");

            Application app = applicationDAO.getById(applicationId);
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(app.getStudentId());
            notif.setTitle("Application Update");
            notif.setMessage("Thank you for your interest. Unfortunately, your application for " + app.getCompanyName() + " (" + app.getJobRole() + ") has not been selected.");
            notificationDAO.save(notif);
        } catch (Exception e) {
            throw new DatabaseException("Database error rejecting candidate: " + e.getMessage(), e);
        }
    }

    public void scheduleInterviewRound(InterviewRound round) throws ValidationException, DatabaseException {
        try {
            if (round.getRoundName() == null || round.getRoundName().trim().isEmpty()) {
                throw new ValidationException("Round name cannot be empty.");
            }
            if (round.getRoundDate() == null) {
                throw new ValidationException("Round date is required.");
            }

            boolean success = roundDAO.save(round);
            if (!success) throw new DatabaseException("Failed to schedule interview round.");

            // Set application status to IN_PROGRESS
            applicationDAO.updateStatus(round.getApplicationId(), "IN_PROGRESS");

            // Notify Student
            Application app = applicationDAO.getById(round.getApplicationId());
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(app.getStudentId());
            notif.setTitle("Interview Scheduled: " + round.getRoundName());
            notif.setMessage("You have been scheduled for Round " + round.getRoundNumber() + " (" + round.getRoundName() + ") with " + 
                             app.getCompanyName() + " on " + round.getRoundDate().toString() + ".");
            notificationDAO.save(notif);
        } catch (ValidationException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Database error scheduling interview: " + e.getMessage(), e);
        }
    }

    public void updateInterviewRound(InterviewRound round) throws DatabaseException {
        try {
            boolean success = roundDAO.update(round);
            if (!success) throw new DatabaseException("Failed to update interview round.");

            Application app = applicationDAO.getById(round.getApplicationId());

            if ("FAILED".equals(round.getStatus())) {
                // If failed, reject application
                applicationDAO.updateStatus(round.getApplicationId(), "REJECTED");
                
                Notification notif = new Notification();
                notif.setUserRole("STUDENT");
                notif.setUserId(app.getStudentId());
                notif.setTitle("Interview Update");
                notif.setMessage("We regret to inform you that you did not clear " + round.getRoundName() + " with " + app.getCompanyName() + ".");
                notificationDAO.save(notif);
            } else if ("PASSED".equals(round.getStatus())) {
                Notification notif = new Notification();
                notif.setUserRole("STUDENT");
                notif.setUserId(app.getStudentId());
                notif.setTitle("Interview Cleared: " + round.getRoundName());
                notif.setMessage("Great news! You cleared " + round.getRoundName() + " with " + app.getCompanyName() + ". Stay tuned for next steps.");
                notificationDAO.save(notif);
            }
        } catch (Exception e) {
            throw new DatabaseException("Database error updating interview: " + e.getMessage(), e);
        }
    }

    public void selectCandidate(Selection selection) throws DatabaseException {
        try {
            boolean success = selectionDAO.save(selection);
            if (!success) throw new DatabaseException("Failed to save selection record.");

            // Update application status to SELECTED
            applicationDAO.updateStatus(selection.getApplicationId(), "SELECTED");

            // Notify Student
            Application app = applicationDAO.getById(selection.getApplicationId());
            Notification notif = new Notification();
            notif.setUserRole("STUDENT");
            notif.setUserId(selection.getStudentId());
            notif.setTitle("Congratulations! You are Selected!");
            notif.setMessage("You have been offered the role of " + app.getJobRole() + " at " + app.getCompanyName() + 
                             " with a package of " + selection.getOfferedPackageLpa() + " LPA!");
            notificationDAO.save(notif);

            // Notify Admins
            Notification adminNotif = new Notification();
            adminNotif.setUserRole("ADMIN");
            adminNotif.setUserId(null); // Broadcast to all admins
            adminNotif.setTitle("New Selection: " + app.getStudentName());
            adminNotif.setMessage(app.getStudentName() + " (" + app.getStudentRoll() + ") has been selected by " + 
                                  app.getCompanyName() + " for " + app.getJobRole() + " at " + selection.getOfferedPackageLpa() + " LPA.");
            notificationDAO.save(adminNotif);

        } catch (Exception e) {
            throw new DatabaseException("Database error selecting candidate: " + e.getMessage(), e);
        }
    }

    public List<InterviewRound> getRoundsForApplication(int applicationId) throws DatabaseException {
        try {
            return roundDAO.getByApplication(applicationId);
        } catch (Exception e) {
            throw new DatabaseException("Failed to get interview rounds: " + e.getMessage(), e);
        }
    }
}
