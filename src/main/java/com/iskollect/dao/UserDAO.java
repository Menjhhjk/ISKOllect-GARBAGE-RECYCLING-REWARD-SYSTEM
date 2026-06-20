package com.iskollect.dao;

import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.util.DBConnection;
import com.iskollect.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection conn() {
        return DBConnection.getInstance().getConnection();
    }

    //
    private void ensureDisplayNameColumn() throws SQLException {
        try (Statement st = conn().createStatement()) {
            st.executeUpdate("ALTER TABLE users ADD COLUMN IF NOT EXISTS display_name VARCHAR(50)");
            st.executeUpdate("ALTER TABLE users ALTER COLUMN display_name TYPE VARCHAR(50) USING LEFT(display_name, 50)");
        }
    }

    private void ensureCreatedAtColumn() throws SQLException {
        try (Statement st = conn().createStatement()) {
            st.executeUpdate("ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP");
            st.executeUpdate("UPDATE users u SET created_at = COALESCE("
                    + "(SELECT MIN(l.performed_at) FROM inout_logs l WHERE l.user_id = u.user_id), "
                    + "u.last_activity, CURRENT_TIMESTAMP) WHERE u.created_at IS NULL");
            st.executeUpdate("ALTER TABLE users ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP");
        }
    }


    private String userSelect(String whereClause) {
        return "SELECT u.*, "
                + "COALESCE((SELECT SUM(br.bottles_collected) FROM bottle_records br "
                + "WHERE br.user_id = u.user_id "
                + "AND br.week_start_date = DATE_TRUNC('week', CURRENT_DATE)::date), 0) AS weekly_bottles, "
                + "COALESCE((SELECT MAX(st.streak_days) FROM streaks st "
                + "WHERE st.user_id = u.user_id "
                + "AND st.date_logged::date >= DATE_TRUNC('week', CURRENT_DATE)::date), 0) AS streak, "
                + "(SELECT MAX(br.collection_date) FROM bottle_records br WHERE br.user_id = u.user_id) AS last_submit_date "
                + "FROM users u " + whereClause;
    }

    //inserts a new user into the database
    public boolean registerUser(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, display_name, email, password_hash, created_at) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try {
            ensureDisplayNameColumn();
            ensureCreatedAtColumn();
            try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getName());
            ps.setString(3, user.getWebmail());
            ps.setString(4, PasswordUtil.hashPassword(user.getPassword()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
            return true;
            }
        } catch (SQLException e) {
            // PostgreSQL unique-violation SQLState is always "23505".
            // Auto-generated constraint names from the schema:
            //   email    → users_email_key
            //   username → users_username_key
            if ("23505".equals(e.getSQLState())) {
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (msg.contains("users_email_key") || msg.contains("\"email\"")) {
                    throw new DatabaseException("This webmail address is already registered. Please log in or use a different address.", e);
                } else if (msg.contains("users_username_key") || msg.contains("\"username\"")) {
                    throw new DatabaseException("This username is already taken. Please choose a different one.", e);
                }
                throw new DatabaseException("An account with these details already exists.", e);
            }
            throw new DatabaseException("Registration failed due to a database error. Please try again.", e);
        }
    }

    //searches for a specific user from the database using inputted webmail
    public User searchUser(String webmail) throws DatabaseException {
        String sql = userSelect("WHERE LOWER(u.email) = LOWER(?)");
        try {
            ensureCreatedAtColumn();
            try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, webmail);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search user credential.", e);
        }
    }

    //searches for a specific user from the database using an ID value
    public User findById(int userId) throws DatabaseException {
        String sql = userSelect("WHERE u.user_id = ?");
        try {
            ensureCreatedAtColumn();
            try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user " + userId + ".", e);
        }
    }

    //returns all user IDs from the database
    public List<Integer> getAllUserIds() throws DatabaseException {
        String sql = "SELECT user_id FROM users ORDER BY user_id";
        try (PreparedStatement ps = conn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Integer> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
            return ids;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch user IDs.", e);
        }
    }

    //updates the user's session token in the database
    public void updateSessionToken(int userId, String token) throws DatabaseException {
        String sql = "UPDATE users SET session_token = ?, last_activity = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update token in database.", e);
        }
    }

    //updates the user's last activity in the database
    public void updateLastActivity(int userId) throws DatabaseException {
        String sql = "UPDATE users SET last_activity = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update last activity.", e);
        }
    }

    //returns the user's session token from the database
    public String getSessionTokenDB(int userId) {
        String sql = "SELECT session_token FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("session_token") : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying token against database.", e);
        }
    }

    //updates the user's profile info (display name, username, age) in the database
    public void updateProfile(int userId, String displayName, String username, int age) throws DatabaseException {
        String sql = "UPDATE users SET display_name = ?, username = ?, age = ? WHERE user_id = ?";
        try {
            ensureDisplayNameColumn();
            try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, displayName);
            ps.setString(2, username);
            ps.setInt(3, age);
            ps.setInt(4, userId);
            ps.executeUpdate();
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (msg.contains("users_username_key") || msg.contains("\"username\"")) {
                    throw new DatabaseException("That username is already taken.", e);
                }
                throw new DatabaseException("A profile conflict occurred. Please check your details and try again.", e);
            }
            throw new DatabaseException("Could not save profile changes. Please try again.", e);
        }
    }

    //updates the user's profile photo path in the database
    public void updateProfilePicture(int userId, String imagePath) throws DatabaseException {
        String sql = "UPDATE users SET profile_photo = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update profile picture path.", e);
        }
    }

    //updates the user's total points in the database
    public void updatePoints(int userId, double totalPoints) throws DatabaseException {
        String sql = "UPDATE users SET total_points = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, totalPoints);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update points.", e);
        }
    }

    //deducts the user's points in the database by the inputted amount
    public boolean deductPointsAtomic(int userId, double amount) throws DatabaseException {
        String sql = "UPDATE users SET total_points = total_points - ? WHERE user_id = ? AND total_points >= ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            ps.setDouble(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deduct points.", e);
        }
    }

    //updates the user's weekly stats (raw bottle count)
    public void updateWeeklyStats(int userId, int weeklyBottles, int streak, java.time.LocalDate lastSubmitDate)
            throws DatabaseException {
        String sql = "UPDATE users SET raw_bottle_count = "
                + "(SELECT COALESCE(SUM(bottles_collected), 0) FROM bottle_records WHERE user_id = ?) "
                + "WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update weekly stats.", e);
        }
    }

    //resets the user's weekly stats (streak days, last submit date) in the database
    public void resetWeeklyStats(int userId) throws DatabaseException {
        String sql = "UPDATE users SET streak_days = 0, last_submit_date = NULL WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to reset weekly stats.", e);
        }
    }

    //updates the user's password in the database
    public void updatePasswordHash(int userId, String newHash) throws DatabaseException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Could not update your password. Please try again.", e);
        }
    }

    //formats a row from the database into a User object
    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setName(readString(rs, "display_name", user.getUsername()));
        user.setWebmail(rs.getString("email"));
        user.setPassword(rs.getString("password_hash"));
        user.setAge(readInt(rs, "age", 0));
        user.setProfilePhoto(readString(rs, "profile_photo", null));
        user.setTotalPoints(readDouble(rs, "total_points", 0));
        user.setRawBottleCount(readInt(rs, "raw_bottle_count", 0));
        user.setWeeklyBottles(readInt(rs, "weekly_bottles", 0));
        user.setStreak(readInt(rs, "streak", 0));
        Date lastSubmitDate = readDate(rs, "last_submit_date");
        user.setLastSubmitDate(lastSubmitDate == null ? null : lastSubmitDate.toLocalDate());
        user.setSessionToken(readString(rs, "session_token", null));
        java.sql.Timestamp activity = readTimestamp(rs, "last_activity");
        if (activity != null) {
            user.setLastActivity(activity.toLocalDateTime());
        }
        java.sql.Timestamp createdAt = readTimestamp(rs, "created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        return user;
    }

    //checks the columns of the database
    private boolean hasColumn(ResultSet rs, String column) throws SQLException {
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            if (column.equalsIgnoreCase(rs.getMetaData().getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    private String readString(ResultSet rs, String column, String fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getString(column) : fallback;
    }

    private int readInt(ResultSet rs, String column, int fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getInt(column) : fallback;
    }

    private double readDouble(ResultSet rs, String column, double fallback) throws SQLException {
        return hasColumn(rs, column) ? rs.getDouble(column) : fallback;
    }

    private Date readDate(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? rs.getDate(column) : null;
    }

    private java.sql.Timestamp readTimestamp(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? rs.getTimestamp(column) : null;
    }
}
