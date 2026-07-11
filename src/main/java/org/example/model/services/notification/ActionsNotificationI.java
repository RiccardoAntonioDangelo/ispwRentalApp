package org.example.model.services.notification;

import org.example.model.services.CollectionI;
import org.example.model.services.session.SessionI;

/**
 * interfaccia per il comportamento di gestione e invio delle notifiche.
 */
public interface ActionsNotificationI {

    String UNREAD_NOTIFICATION_KEY = "UnreadNotificationCollection";
    String READ_NOTIFICATION_KEY   = "ReadNotificationCollection";

    default void initNotificationI(SessionI sessionI) {
        sessionI.ensureCollection(UNREAD_NOTIFICATION_KEY);
        sessionI.ensureCollection(READ_NOTIFICATION_KEY);
    }

    default void addNotification(NotificationI notification, SessionI sessionI) {
        initNotificationI(sessionI);
        if (notification.isRead()) {
            sessionI.addItem(READ_NOTIFICATION_KEY, notification);
        } else {
            sessionI.addItem(UNREAD_NOTIFICATION_KEY, notification);
        }
    }

    default void markAsRead(NotificationI notification, SessionI sessionI) {
        initNotificationI(sessionI);
        sessionI.removeItem(UNREAD_NOTIFICATION_KEY, notification);
        notification.setRead(true);
        sessionI.addItem(READ_NOTIFICATION_KEY, notification);
    }

    @SuppressWarnings("unchecked")
    default CollectionI<NotificationI> getUnreadNotifications(SessionI sessionI) {
        initNotificationI(sessionI);
        return sessionI.getCollection(UNREAD_NOTIFICATION_KEY);
    }

    @SuppressWarnings("unchecked")
    default CollectionI<NotificationI> getReadNotifications(SessionI sessionI) {
        initNotificationI(sessionI);
        return sessionI.getCollection(READ_NOTIFICATION_KEY);
    }

    /**
     * 🎯 Rinominato in execute per uniformità con il resto del sistema
     */
    default boolean execute(SessionI session, NotificationI notification) {
        if (session == null) {
            throw new IllegalArgumentException("Sessione non valida durante l'esecuzione della notifica.");
        }
        addNotification(notification, session);
        return true;
    }

    default boolean validateNotification(String message, String recipient) {
        return message != null && !message.trim().isEmpty() && recipient != null && !recipient.trim().isEmpty();
    }
}