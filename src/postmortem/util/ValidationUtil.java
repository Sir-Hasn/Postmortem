package postmortem.util;

import java.util.HashMap;
import java.util.Map;

public class ValidationUtil {
    private static final int MIN_TITLE_LENGTH = 3;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MIN_MESSAGE_LENGTH = 5;
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int MIN_CONTEXT_LENGTH = 5;
    private static final int MAX_CONTEXT_LENGTH = 5000;

    public static class ValidationResult {
        public boolean isValid;
        public Map<String, String> errors = new HashMap<>();

        public ValidationResult(boolean isValid) {
            this.isValid = isValid;
        }

        public void addError(String field, String message) {
            this.errors.put(field, message);
            this.isValid = false;
        }
    }

    public static ValidationResult validateBugEntry(String title, String errorMessage, String errorContext) {
        ValidationResult result = new ValidationResult(true);

        if (title == null || title.trim().isEmpty()) {
            result.addError("title", "Title is required");
        } else if (title.length() < MIN_TITLE_LENGTH) {
            result.addError("title", "Title must be at least " + MIN_TITLE_LENGTH + " characters");
        } else if (title.length() > MAX_TITLE_LENGTH) {
            result.addError("title", "Title must not exceed " + MAX_TITLE_LENGTH + " characters");
        }

        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            result.addError("errorMessage", "Error message is required");
        } else if (errorMessage.length() < MIN_MESSAGE_LENGTH) {
            result.addError("errorMessage", "Error message must be at least " + MIN_MESSAGE_LENGTH + " characters");
        } else if (errorMessage.length() > MAX_MESSAGE_LENGTH) {
            result.addError("errorMessage", "Error message must not exceed " + MAX_MESSAGE_LENGTH + " characters");
        }

        if (errorContext == null || errorContext.trim().isEmpty()) {
            result.addError("errorContext", "Error context is required");
        } else if (errorContext.length() < MIN_CONTEXT_LENGTH) {
            result.addError("errorContext", "Error context must be at least " + MIN_CONTEXT_LENGTH + " characters");
        } else if (errorContext.length() > MAX_CONTEXT_LENGTH) {
            result.addError("errorContext", "Error context must not exceed " + MAX_CONTEXT_LENGTH + " characters");
        }

        return result;
    }

    public static ValidationResult validateTagFormat(String tags) {
        ValidationResult result = new ValidationResult(true);
        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagArray = tags.split(",");
            if (tagArray.length > 10) {
                result.addError("tags", "Maximum 10 tags allowed");
            }
            for (String tag : tagArray) {
                if (tag.trim().isEmpty()) {
                    result.addError("tags", "Empty tags not allowed");
                    break;
                }
            }
        }
        return result;
    }
}
