package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.InOutLog;
import com.iskollect.model.InOutLog.EventType;
import com.iskollect.model.InOutLog.LogStatus;
import com.iskollect.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InOutLogDAO {

    //database queries or commands

    private static final String SQL_INSERT =
        "INSERT INTO inout_logs (user_id, action, performed_at, notes) VALUES (?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
        "SELECT * FROM inout_logs WHERE log_id = ?";

    private static final String SQL_GET_BY_USER =
        "SELECT * FROM inout_logs WHERE user_id = ? ORDER BY performed_at DESC";

    private static final String SQL_GET_ALL =
        "SELECT * FROM inout_logs ORDER BY performed_at DESC";

    private static final String SQL_GET_BY_DATE_RANGE =
        "SELECT * FROM inout_logs WHERE user_id = ? AND performed_at::date BETWEEN ? AND ? " +
        "ORDER BY performed_at DESC";

    private static final String SQL_GET_LAST_EVENT =
        "SELECT * FROM inout_logs WHERE user_id = ? ORDER BY performed_at DESC LIMIT 1";

    private static final String SQL_GET_LAST_EVENT_OF_TYPE =
        "SELECT * FROM inout_logs WHERE user_id = ? AND action = ? " +
        "ORDER BY performed_at DESC LIMIT 1";

    private static final String SQL_GET_RECENT_SAME_EVENT =
        "SELECT * FROM inout_logs " +
        "WHERE user_id = ? AND action = ? " +
        "AND performed_at >= ? " +
        "ORDER BY performed_at DESC LIMIT 1";

    private static final String SQL_COUNT_BY_DATE =
        "SELECT COUNT(*) FROM inout_logs WHERE performed_at::date = ?";

    private static final String SQL_GET_BY_DATE =
        "SELECT * FROM inout_logs WHERE performed_at::date = ? ORDER BY performed_at DESC";

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }


    //inserts a log into inoutlogs
    public void insert(InOutLog log) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getUserId());
            ps.setString(2, log.getEventType().name());
            ps.setTimestamp(3, Timestamp.valueOf(log.getTimestamp()));
            ps.setString(4, log.getStaffNote());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    log.setLogId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert InOutLog for user " + log.getUserId(), e);
        }
    }

    //returns a log by its ID
    public InOutLog findById(int logId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, logId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find log by ID " + logId, e);
        }
    }

    //returns all logs of a user
    public List<InOutLog> getByUserId(int userId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_BY_USER)) {
            ps.setInt(1, userId);
            return collectResults(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch logs for user " + userId, e);
        }
    }

    //returns all logs from all users
    public List<InOutLog> getAll() throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_ALL)) {
            return collectResults(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all logs.", e);
        }
    }

    //returns all logs from a user by a data range
    public List<InOutLog> getByUserAndDateRange(int userId, LocalDate from, LocalDate to)
            throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_BY_DATE_RANGE)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            return collectResults(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch logs for user " + userId
                + " between " + from + " and " + to, e);
        }
    }

    //returns the recent log of a user
    public InOutLog getLastEvent(int userId) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_LAST_EVENT)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get last event for user " + userId, e);
        }
    }

    //returns the recent log of a user by a specific type
    public InOutLog getLastEventOfType(int userId, EventType eventType) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_LAST_EVENT_OF_TYPE)) {
            ps.setInt(1, userId);
            ps.setString(2, eventType.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get last " + eventType + " for user " + userId, e);
        }
    }

    //returns a log of the same type within a time window
    public InOutLog getRecentSameEvent(int userId, EventType eventType, LocalDateTime windowStart)
            throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_RECENT_SAME_EVENT)) {
            ps.setInt(1, userId);
            ps.setString(2, eventType.name());
            ps.setTimestamp(3, Timestamp.valueOf(windowStart));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check for recent " + eventType
                + " for user " + userId, e);
        }
    }

    //returns all logs from a specific day
    public List<InOutLog> getByDate(LocalDate date) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_GET_BY_DATE)) {
            ps.setDate(1, Date.valueOf(date));
            return collectResults(ps);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch logs for date " + date, e);
        }
    }

    //returns a count of all logs from a specific day
    public int countByDate(LocalDate date) throws DatabaseException {
        try (PreparedStatement ps = conn().prepareStatement(SQL_COUNT_BY_DATE)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count logs for date " + date, e);
        }
    }

    //formats database data into an InOutLog object
    private InOutLog map(ResultSet rs) throws SQLException {
        return new InOutLog(
            rs.getInt("log_id"),
            rs.getInt("user_id"),
            EventType.valueOf(rs.getString("action")),
            rs.getTimestamp("performed_at").toLocalDateTime(),
            rs.getString("notes")
        );
    }

    //formats database data into an InOutLog list
    private List<InOutLog> collectResults(PreparedStatement ps) throws SQLException {
        List<InOutLog> logs = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(map(rs));
            }
        }
        return logs;
    }
}
