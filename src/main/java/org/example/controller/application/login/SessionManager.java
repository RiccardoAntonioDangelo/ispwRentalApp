package org.example.controller.application.login;

import org.example.model.entity.actors.User;
import org.example.model.entity.session.Session;
import org.example.util.singleton.SingletonI;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionManager implements SingletonI {
    private static final Map<String, Session> activeSessions = new ConcurrentHashMap<>();

    /**
     * SingletonService
     */
    private SessionManager() {/*Costruttore privato per impedire istanziazioni esterne*/}
    public static SessionManager getSingletonInstance() {return SingletonI.getOrCreateSingleton(SessionManager.class, SessionManager::new);}
    /**
     * Tenta di inserire una sessione nella cache.
     * @return true se l'inserimento riesce, false se l'ID è già presente (utente già loggato)
     */
    public static Session create(String email, String password, String role) {
        Session newSession = new Session(User.create(email, password, role));
        if (registerSession(newSession)) {return newSession;}
        throw new IllegalStateException("Accesso negato: l'utente con email " + email + " ha già una sessione attiva.");
    }

    public static boolean registerSession(Session session) {
        if (session == null || session.getId() == null) return false;
        return activeSessions.putIfAbsent(session.getId(), session) == null;
    }

    /**
     * Rimuove la sessione dalla cache e invoca il metodo logout sulla sessione stessa.
     */
    public static void closeSession(Session session) {if (session != null && session.getId() != null) {removeSession(session.getId());}}
    public static void removeSession(String email) {
        activeSessions.remove(email);
    }
    public static boolean isUserLoggedIn(String email) {return email != null && activeSessions.containsKey(email);}
    public static Optional<Session> getActiveSession(String email) {return Optional.ofNullable(activeSessions.get(email));}
    public static boolean isSessionValid(Session session) {
        if (session == null || session.getId() == null) return false;
        Session stored = activeSessions.get(session.getId());
        return session.equals(stored) && session.isValid();
    }

    public static int getActiveSessionsCount() {
        return activeSessions.size();
    }


}