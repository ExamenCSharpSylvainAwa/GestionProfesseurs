package sn.groupeisi.gestionprofesseurs.Utils;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.Getter;

public class HibernateUtil {
    @Getter
    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            // Crée l'EntityManagerFactory à partir de persistence.xml
            return Persistence.createEntityManagerFactory("PERSISTENCE");
        } catch (Throwable ex) {
            ex.printStackTrace(); // Afficher l'exception complète
            throw new ExceptionInInitializerError("Initial EntityManagerFactory creation failed: " + ex);
        }
    }
}
