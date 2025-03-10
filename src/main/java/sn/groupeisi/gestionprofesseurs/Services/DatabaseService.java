package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DatabaseService {
    public static void testDatabaseConnection() {
        EntityManagerFactory entityManagerFactory = JPAUtil.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        System.out.println("Connexion à la base de données réussie !");

        entityManager.close();
        JPAUtil.shutdown();
    }
}
