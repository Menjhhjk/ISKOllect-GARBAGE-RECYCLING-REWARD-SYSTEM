package com.iskollect.service;

import com.iskollect.dao.InOutLogDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.InOutLog;
import com.iskollect.model.InOutLog.EntryMethod;
import com.iskollect.model.InOutLog.EventType;
import com.iskollect.model.InOutLog.LogStatus;
import com.iskollect.model.LogResult;
import com.iskollect.util.UserValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InOutService {
    private static final int DUPLICATE_WINDOW_MINUTES = 5;
    private final InOutLogDAO     logDAO;
    private final UserValidator userValidator;

    //constructors
    public InOutService() {
        this.logDAO           = new InOutLogDAO();
        this.userValidator = new UserValidator();  // stub until registration module
    }
    public InOutService(InOutLogDAO logDAO, UserValidator userValidator) {
        this.logDAO           = logDAO;
        this.userValidator = userValidator;
    }

    public LogResult logEvent(int userId, EventType eventType, String staffNote) {
        if (userId <= 0) {
            return LogResult.invalidInput("User ID must be a positive integer.");
        }
        if (eventType == null) {
            return LogResult.invalidInput("Event type (INGRESS / EGRESS) must be specified.");
        }

        if (!userValidator.exists(userId)) {
            return persistUnresolved(userId, eventType, staffNote);
        }

       //checks for duplicate window
        try {
            LocalDateTime windowStart = LocalDateTime.now()
                    .minusMinutes(DUPLICATE_WINDOW_MINUTES);

            InOutLog recent = logDAO.getRecentSameEvent(userId, eventType, windowStart);

            if (recent != null) {
                return LogResult.duplicate(recent);
            }
        } catch (DatabaseException e) {
            return LogResult.dbError("Duplicate check failed: " + e.getMessage());
        }

        //sets the log
        InOutLog log = new InOutLog(
            userId,
            eventType,
            EntryMethod.MANUAL,
            LocalDateTime.now(),
            normalizeNote(staffNote),
            LogStatus.VALID
        );

        try {
            logDAO.insert(log);
            return LogResult.success(log);
        } catch (DatabaseException e) {
            return LogResult.dbError("Could not persist log: " + e.getMessage());
        }
    }

    //logging with no staff note
    public LogResult logIngress(int userId) {
        return logEvent(userId, EventType.INGRESS, null);
    }
    public LogResult logEgress(int userId) {
        return logEvent(userId, EventType.EGRESS, null);
    }

    //checks the user's last action (if the user logged in or logged out)
    public InOutLog getCurrentStatus(int userId) {
        try {
            return logDAO.getLastEvent(userId);
        } catch (DatabaseException e) {
            System.err.println("getCurrentStatus failed for user " + userId + ": " + e.getMessage());
            return null;
        }
    }

    //checks if the user is currently active
    public boolean isCurrentlyInside(int userId) {
        InOutLog last = getCurrentStatus(userId);
        return last != null && last.getEventType() == EventType.INGRESS;
    }


    //returns the user's log history
    public List<InOutLog> getUserHistory(int userId) {
        try {
            return logDAO.getByUserId(userId);
        } catch (DatabaseException e) {
            System.err.println("getUserHistory failed: " + e.getMessage());
            return List.of();
        }
    }

    //returns the user's log history in a date range
    public List<InOutLog> getUserHistoryByDateRange(int userId, LocalDate from, LocalDate to) {
        try {
            return logDAO.getByUserAndDateRange(userId, from, to);
        } catch (DatabaseException e) {
            System.err.println("getUserHistoryByDateRange failed: " + e.getMessage());
            return List.of();
        }
    }

    //returns all logs recorded today
    public List<InOutLog> getTodayLogs() {
        try {
            return logDAO.getByDate(LocalDate.now());
        } catch (DatabaseException e) {
            System.err.println("getTodayLogs failed: " + e.getMessage());
            return List.of();
        }
    }

    //returns all events recorded today
    public int getTodayCount() {
        try {
            return logDAO.countByDate(LocalDate.now());
        } catch (DatabaseException e) {
            System.err.println("getTodayCount failed: " + e.getMessage());
            return 0;
        }
    }

    //returns all logs
    public List<InOutLog> getAllLogs() {
        try {
            return logDAO.getAll();
        } catch (DatabaseException e) {
            System.err.println("getAllLogs failed: " + e.getMessage());
            return List.of();
        }
    }

    //sets a log and sets the log status as unresolved
    private LogResult persistUnresolved(int userId, EventType eventType, String staffNote) {
        InOutLog log = new InOutLog(
            userId,
            eventType,
            EntryMethod.MANUAL,
            LocalDateTime.now(),
            normalizeNote(staffNote),
            LogStatus.UNRESOLVED
        );
        try {
            logDAO.insert(log);
        } catch (DatabaseException e) {
            System.err.println("Failed to persist UNRESOLVED log: " + e.getMessage());
        }
        return LogResult.userNotFound(userId);
    }

    //trims a note and returns null if no note is found
    private String normalizeNote(String note) {
        if (note == null) return null;
        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
