package com.iskollect.util;

import com.iskollect.dao.BottleRecordDAO;
import com.iskollect.exception.DatabaseException;
import com.iskollect.model.User;
import com.iskollect.service.PointsService;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static User loggedInUser;
    private static final PointsService pointsService = new PointsService();
    private static final BottleRecordDAO bottlerecordDAO = new BottleRecordDAO();
    private static final List<Runnable> pointUpdateListeners = new ArrayList<>();

    //sets the user's session token when they login
    public static synchronized void setSession(User user) {
        if (user != null) {
            String token = java.util.UUID.randomUUID().toString();
            user.setSessionToken(token);
        }
        loggedInUser = user;
    }

    //refreshes the user's points and bottle count
    public static void refreshUserSession() {
        User user = getSession();
        if (user != null) {
            try {
                double latestPoints = pointsService.getTotalPoints(user.getUserId());
                int latestBottles = bottlerecordDAO.getTotalBottles(user.getUserId());
                user.setTotalPoints(latestPoints);
                user.setRawBottleCount(latestBottles);
            } catch (DatabaseException e) {
                System.err.println("Error refreshing data: " + e.getMessage());
            }
        }
    }

    //stores and updates user's points and bottle count for other classes
    public static synchronized void addPointUpdateListener(Runnable listener) {
        if (listener != null && !pointUpdateListeners.contains(listener)) {
            pointUpdateListeners.add(listener);
        }
    }
    public static void notifyPointUpdate() {
        refreshUserSession();
        List<Runnable> listeners;
        synchronized (SessionManager.class) {
            listeners = new ArrayList<>(pointUpdateListeners);
        }
        for (Runnable listener : listeners) {
            if (listener != null) {
                listener.run();
            }
        }
    }

    //returns the current user
    public static User getSession() { return loggedInUser; }

    //clears the session
    public static synchronized void clearSession() {
        loggedInUser = null;
        pointUpdateListeners.clear();
    }
}
