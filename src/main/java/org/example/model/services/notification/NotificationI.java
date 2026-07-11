package org.example.model.services.notification;

import org.example.model.services.EntityI;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;

/**
 * interfaccia che definisce la struttura dati di una Notifica con supporto Fluent API.
 */
public interface NotificationI extends EntityI<String>, WorkI {

    String getMessage();
    NotificationI setMessage(String message); // 🎯 Cambiato void -> NotificationI

    String getRecipientEmail();
    NotificationI setRecipientEmail(String recipientEmail); // 🎯 Cambiato void -> NotificationI

    String getSenderEmail();
    NotificationI setSenderEmail(String senderEmail); // 🎯 Cambiato void -> NotificationI

    boolean isRead();
    NotificationI setRead(boolean read); // 🎯 Cambiato void -> NotificationI

    @Override
    default boolean canWork(SessionI sessionI, UserI userI) {//todo
        if (userI instanceof ActionsNotificationI worker) {
            return worker.execute(sessionI, this);
        }
        return false;
    }
}