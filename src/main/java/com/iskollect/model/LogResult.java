package com.iskollect.model;

import java.time.LocalDateTime;

public class LogResult {
    private final Outcome outcome;
    private final InOutLog log;
    private final String message;
    private final LocalDateTime at;

    //enum to force the use of constant values
    public enum Outcome {
        SUCCESS,
        DUPLICATE,
        USER_NOT_FOUND,
        INVALID_INPUT,
        DB_ERROR
    }

    //constructor
    public LogResult(Outcome outcome, InOutLog log, String message) {
        this.outcome = outcome;
        this.log     = log;
        this.message = message;
        this.at      = LocalDateTime.now();
    }

    public static LogResult success(InOutLog log) {
        return new LogResult(
            Outcome.SUCCESS,
            log,
            String.format("Logged: User %d — %s at %s",
                log.getUserId(), log.getEventType(), log.getTimestamp())
        );
    }

    public static LogResult duplicate(InOutLog existing) {
        return new LogResult(
            Outcome.DUPLICATE,
            existing,
            String.format("Duplicate: User %d already has an active %s log (ID %d).",
                existing.getUserId(), existing.getEventType(), existing.getLogId())
        );
    }

    public static LogResult userNotFound(int userId) {
        return new LogResult(
            Outcome.USER_NOT_FOUND,
            null,
            "User ID " + userId + " not found. Registration module not active."
        );
    }

    public static LogResult invalidInput(String reason) {
        return new LogResult(Outcome.INVALID_INPUT, null, "Invalid input: " + reason);
    }

    public static LogResult dbError(String detail) {
        return new LogResult(Outcome.DB_ERROR, null, "Database error: " + detail);
    }

    //getters
    public Outcome getOutcome()    { return outcome; }
    public InOutLog getLog()       { return log; }
    public String getMessage()     { return message; }
    public LocalDateTime getAt()   { return at; }
    public boolean isSuccess()     { return outcome == Outcome.SUCCESS; }
}
