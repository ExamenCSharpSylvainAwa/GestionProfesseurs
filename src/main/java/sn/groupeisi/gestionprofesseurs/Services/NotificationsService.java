package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Notifications;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Utils.EmailService;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationsService {

    private static final Logger logger = Logger.getLogger(NotificationsService.class.getName());

    public void envoyerNotification(Users destinataire, String message) {
        if (destinataire == null || message == null || message.isEmpty()) {
            logger.log(Level.WARNING, "Destinataire ou message invalide");
            return;
        }

        EntityTransaction transaction = null;
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Créer la notification
            Notifications notification = new Notifications(message, destinataire, LocalDateTime.now());
            entityManager.persist(notification);

            // Commit de la transaction
            transaction.commit();

            // Vérification du rôle (exemple : uniquement les professeurs)
            if ("Professeur".equalsIgnoreCase(destinataire.getRole())) {
                // Envoi de l'e-mail
                EmailService.envoyerEmail(destinataire.getEmail(), "Nouvelle Notification", message);
            } else {
                logger.log(Level.INFO, "Le destinataire n'est pas un professeur, pas d'email envoyé.");
            }

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, "Erreur lors de l'envoi de la notification", e);
        } finally {
            entityManager.close();
        }
    }
}
