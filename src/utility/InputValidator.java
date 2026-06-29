package utility;

import exception.ValidationException;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\-\\s]{10,15}$");

    public static void validateEmail(String email) throws ValidationException {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format (e.g., user@example.com).");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new ValidationException("Invalid phone format (must be 10-15 digits).");
            }
        }
    }

    public static void validateCgpa(double cgpa) throws ValidationException {
        if (cgpa < 0.0 || cgpa > 10.0) {
            throw new ValidationException("CGPA must be between 0.0 and 10.0.");
        }
    }

    public static void validatePercentage(double pct, String fieldName) throws ValidationException {
        if (pct < 0.0 || pct > 100.0) {
            throw new ValidationException(fieldName + " percentage must be between 0.0 and 100.0.");
        }
    }

    public static void validateBacklogs(int backlogs) throws ValidationException {
        if (backlogs < 0) {
            throw new ValidationException("Backlogs cannot be negative.");
        }
    }

    public static void validateRequiredString(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty.");
        }
    }

    public static void validatePackage(double pkg) throws ValidationException {
        if (pkg <= 0.0) {
            throw new ValidationException("Package (LPA) must be greater than 0.");
        }
    }
}
