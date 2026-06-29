package utility;

import model.Admin;
import model.Recruiter;
import model.Student;

public class SessionManager {
    public enum Role {
        ADMIN, RECRUITER, STUDENT
    }

    private static Role currentRole;
    private static Object currentUser;

    public static void login(Role role, Object user) {
        currentRole = role;
        currentUser = user;
    }

    public static void logout() {
        currentRole = null;
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static Role getCurrentRole() {
        return currentRole;
    }

    public static Object getCurrentUser() {
        return currentUser;
    }

    public static Admin getAdmin() {
        if (currentRole == Role.ADMIN) {
            return (Admin) currentUser;
        }
        return null;
    }

    public static Recruiter getRecruiter() {
        if (currentRole == Role.RECRUITER) {
            return (Recruiter) currentUser;
        }
        return null;
    }

    public static Student getStudent() {
        if (currentRole == Role.STUDENT) {
            return (Student) currentUser;
        }
        return null;
    }
}
