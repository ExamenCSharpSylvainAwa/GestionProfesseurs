package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class ProfesseurEmargementService {

    // Ajouter un émargement à la base de données
    public void ajouterEmargement(Emargements emargement) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(emargement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    // Modifier un émargement existant
    public void modifierEmargement(Emargements emargement) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(emargement);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    // Vérifier si un professeur a déjà émargé pour un cours à une date donnée
    public boolean existeEmargement(Long professeurId, Long coursId, LocalDate date) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(e) FROM Emargements e WHERE e.professeur.id = :profId AND e.cours.id = :coursId AND e.date = :date",
                    Long.class
            );
            query.setParameter("profId", professeurId);
            query.setParameter("coursId", coursId);
            query.setParameter("date", date);

            return query.getSingleResult() > 0;
        } finally {
            entityManager.close();
        }
    }

    // Obtenir les émargements d'un professeur par son ID
    public List<Emargements> getEmargementsByProfesseur(Long professeurId) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createQuery(
                            "FROM Emargements e WHERE e.professeur.id = :profId", Emargements.class
                    ).setParameter("profId", professeurId)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    // Obtenir tous les cours disponibles
    public List<Cours> getAllCours() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Cours> query = entityManager.createQuery("SELECT c FROM Cours c", Cours.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Retourne null en cas d'erreur
        } finally {
            entityManager.close();
        }
    }
}
