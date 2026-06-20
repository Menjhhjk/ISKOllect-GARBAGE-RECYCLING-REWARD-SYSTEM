package com.iskollect.exception;

//for errors due to duplicate entries
public class DuplicateLogException extends Exception {

    private final int existingLogId;

    public DuplicateLogException(int existingLogId, String message) {
        super(message);
        this.existingLogId = existingLogId;
    }

    public int getExistingLogId() {
        return existingLogId;
    }
}