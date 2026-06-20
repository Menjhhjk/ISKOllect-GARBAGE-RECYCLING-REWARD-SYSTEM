package com.iskollect.service;

import com.iskollect.dao.UserDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.util.DBConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BadgeService {
    private final UserDAO userDAO;

    //constructors for a BadgeService object
    public BadgeService() {
        this(new UserDAO());
    }
    public BadgeService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    //evaluates the badges
    public BadgeResult evaluateBadge(int weeklyBottles) {
        return evaluateBadgeForBottles(weeklyBottles);
    }

    //returns a badge based on the number of submitted bottles
    public BadgeResult evaluateBadgeForBottles(int bottleCount) {
        if (bottleCount >= 31) {
            return new BadgeResult("Constellation", 10);
        }
        if (bottleCount >= 21) {
            return new BadgeResult("Gold", 5);
        }
        if (bottleCount >= 11) {
            return new BadgeResult("Emerald", 3);
        }
        if (bottleCount >= 6) {
            return new BadgeResult("Silver", 1);
        }
        return new BadgeResult("Bronze", 0);
    }

    //gets the user's current badge
    public BadgeResult getCurrentBadge(int userId) {
        try {
            User user = userDAO.findById(userId);
            return user == null ? new BadgeResult("Bronze", 0) : evaluateBadgeForBottles(user.getWeeklyBottles());
        } catch (DatabaseException e) {
            return new BadgeResult("Bronze", 0);
        }
    }

    //gets the user's current badge level
    public int getBadgeLevel(BadgeResult badge) {
        if (badge == null || badge.getTierName() == null) {
            return 0;
        }
        switch (badge.getTierName()) {
            case "Constellation": return 5;
            case "Gold": return 4;
            case "Emerald": return 3;
            case "Silver": return 2;
            case "Bronze": return 1;
            default: return 0;
        }
    }

    //resets the user's weekly stats
    public void resetWeeklyData(int userId) {
        try {
            userDAO.resetWeeklyStats(userId);
        } catch (DatabaseException e) {
            System.err.println("resetWeeklyData failed: " + e.getMessage());
        }
    }

    //awards based on user's badge
    public boolean awardWeeklyBadge(int userId, BadgeResult badge) throws DatabaseException {
        return awardBadge(userId, badge, LocalDate.now());
    }

    //awards the user with a badge
    private boolean awardBadge(int userId, BadgeResult badge, LocalDate dateAwarded) throws DatabaseException {
        String sql = "INSERT INTO user_badges (user_id, badge_id, date_awarded, week_start_date) "
                + "SELECT ?, badge_id, ?, DATE_TRUNC('week', ?::date)::date "
                + "FROM badges b WHERE b.badge_name = ? "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM user_badges ub "
                + "WHERE ub.user_id = ? AND ub.badge_id = b.badge_id"
                + ")";
        try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(dateAwarded));
            ps.setDate(3, Date.valueOf(dateAwarded));
            ps.setString(4, badge.getTierName());
            ps.setInt(5, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to award weekly badge.", e);
        }
    }

    //
    public boolean awardBadgeOnPromotion(int userId, BadgeResult previousBadge, BadgeResult newBadge)
            throws DatabaseException {
        if (getBadgeLevel(newBadge) <= getBadgeLevel(previousBadge)) {
            return false;
        }
        return awardWeeklyBadge(userId, newBadge);
    }

    public List<BadgeResult> awardReachedBadges(int userId, int previousBottleCount, int newBottleCount)
            throws DatabaseException {
        List<BadgeResult> awarded = new ArrayList<>();
        int previousLevel = getBadgeLevel(evaluateBadgeForBottles(previousBottleCount));
        int newLevel = getBadgeLevel(evaluateBadgeForBottles(newBottleCount));

        for (BadgeResult badge : getBadgeTiers()) {
            int level = getBadgeLevel(badge);
            if ((previousBottleCount == 0 && newBottleCount > 0 && level == 1)
                    || (level > previousLevel && level <= newLevel)) {
                if (awardWeeklyBadge(userId, badge)) {
                    awarded.add(badge);
                }
            }
        }
        return awarded;
    }

    //badge tiers
    public List<BadgeResult> getBadgeTiers() {
        return List.of(
                new BadgeResult("Bronze", 0),
                new BadgeResult("Silver", 1),
                new BadgeResult("Emerald", 3),
                new BadgeResult("Gold", 5),
                new BadgeResult("Constellation", 10)
        );
    }

    //returns the user's badge history
    public List<BadgeHistoryEntry> getBadgeHistory(int userId, int limit) {
        syncEarnedBadgeHistory(userId);
        String sql = "SELECT b.badge_name, ub.date_awarded, ub.week_start_date, "
                + "COALESCE((SELECT SUM(br.bottles_collected) FROM bottle_records br "
                + "WHERE br.user_id = ub.user_id AND br.week_start_date = ub.week_start_date), 0) AS total_bottles "
                + "FROM user_badges ub JOIN badges b ON ub.badge_id = b.badge_id "
                + "WHERE ub.user_id = ? "
                + "ORDER BY ub.user_badge_id DESC "
                + "LIMIT ?";
        List<BadgeHistoryEntry> result = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement ps =
                     DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new BadgeHistoryEntry(
                            rs.getString("badge_name"),
                            rs.getDate("date_awarded").toLocalDate(),
                            rs.getDate("week_start_date").toLocalDate(),
                            rs.getInt("total_bottles")
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("getBadgeHistory failed: " + e.getMessage());
        }
        return result;
    }

    //returns complete badge history
    public List<BadgeHistoryEntry> getAllBadgeHistory(int userId) {
        syncEarnedBadgeHistory(userId);
        String sql = "SELECT b.badge_name, ub.date_awarded, ub.week_start_date, "
                + "COALESCE((SELECT SUM(br.bottles_collected) FROM bottle_records br "
                + "WHERE br.user_id = ub.user_id AND br.week_start_date = ub.week_start_date), 0) AS total_bottles "
                + "FROM user_badges ub JOIN badges b ON ub.badge_id = b.badge_id "
                + "WHERE ub.user_id = ? "
                + "ORDER BY ub.user_badge_id DESC";
        List<BadgeHistoryEntry> result = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement ps =
                     DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new BadgeHistoryEntry(
                            rs.getString("badge_name"),
                            rs.getDate("date_awarded").toLocalDate(),
                            rs.getDate("week_start_date").toLocalDate(),
                            rs.getInt("total_bottles")
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("getAllBadgeHistory failed: " + e.getMessage());
        }
        return result;
    }

    //updates the user's badge data to ensure accuracy
    private void syncEarnedBadgeHistory(int userId) {
        String sql = "WITH running AS ("
                + "SELECT collection_date, "
                + "SUM(bottles_collected) OVER (ORDER BY collection_date, record_id) AS total_bottles "
                + "FROM bottle_records WHERE user_id = ?"
                + ") SELECT MIN(collection_date) AS reached_date FROM running WHERE total_bottles >= ?";

        for (BadgeResult badge : getBadgeTiers()) {
            int threshold = getBadgeThreshold(badge.getTierName());
            try (PreparedStatement ps = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setInt(2, threshold);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getDate("reached_date") != null) {
                        awardBadge(userId, badge, rs.getDate("reached_date").toLocalDate());
                    }
                }
            } catch (Exception e) {
                System.err.println("syncEarnedBadgeHistory failed for " + badge.getTierName()
                        + ": " + e.getMessage());
            }
        }
    }

    //needed values (bottles) to earn each badge
    private int getBadgeThreshold(String tierName) {
        switch (tierName) {
            case "Silver":        return 6;
            case "Emerald":       return 11;
            case "Gold":          return 21;
            case "Constellation": return 31;
            default:              return 1;
        }
    }

    public static final class BadgeHistoryEntry {
        private final String badgeName;
        private final LocalDate dateAwarded;
        private final LocalDate weekStartDate;
        private final int totalBottles;

        public BadgeHistoryEntry(String badgeName, LocalDate dateAwarded,
                                 LocalDate weekStartDate, int totalBottles) {
            this.badgeName     = badgeName;
            this.dateAwarded   = dateAwarded;
            this.weekStartDate = weekStartDate;
            this.totalBottles  = totalBottles;
        }

        public BadgeHistoryEntry(String badgeName, LocalDate dateAwarded) {
            this(badgeName, dateAwarded, dateAwarded, 0);
        }

        public String    getBadgeName()     { return badgeName; }
        public LocalDate getDateAwarded()   { return dateAwarded; }
        public LocalDate getWeekStartDate() { return weekStartDate; }
        public int       getTotalBottles()  { return totalBottles; }
    }

    //seperate class
    public static final class BadgeResult {
        private final String tierName;
        private final double bonusPoints;

        //constructor for creating a BadgeResult object
        public BadgeResult(String tierName, double bonusPoints) {
            this.tierName = tierName;
            this.bonusPoints = bonusPoints;
        }

        //getters
        public String getTierName()    { return tierName; }
        public double getBonusPoints() { return bonusPoints; }
    }
}
