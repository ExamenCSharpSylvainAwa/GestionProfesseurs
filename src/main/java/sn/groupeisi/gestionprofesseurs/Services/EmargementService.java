package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;

public class EmargementService {

    /**
     * Ajoute un nouvel émargement dans la base de données.
     */
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

    /**
     * Modifie un émargement existant.
     */
    public boolean modifierEmargement(Emargements emargement) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            Emargements existingEmargement = entityManager.find(Emargements.class, emargement.getId());

            if (existingEmargement != null) {
                existingEmargement.setDate(emargement.getDate());
                existingEmargement.setStatut(emargement.getStatut());
                existingEmargement.setProfesseur(emargement.getProfesseur());
                existingEmargement.setCours(emargement.getCours());

                entityManager.merge(existingEmargement);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    /**
     * Supprime un émargement de la base de données en fonction de son ID.
     */
    public boolean supprimerEmargement(Long id) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            Emargements emargement = entityManager.find(Emargements.class, id);
            if (emargement != null) {
                entityManager.remove(emargement);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
        return false;
    }

    /**
     * Retourne la liste de tous les émargements enregistrés.
     */
    public List<Emargements> listerEmargements() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createQuery("FROM Emargements", Emargements.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Vérifie si un émargement existe déjà pour un professeur et un cours à une date donnée.
     */
    public boolean emargementExiste(Long professeurId, Long coursId, LocalDate date) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            long count = (long) entityManager.createQuery(
                            "SELECT COUNT(e) FROM Emargements e WHERE e.professeur.id = :professeurId " +
                                    "AND e.cours.id = :coursId AND e.date = :date")
                    .setParameter("professeurId", professeurId)
                    .setParameter("coursId", coursId)
                    .setParameter("date", date)  // ici, passer un LocalDate directement
                    .getSingleResult();
            return count > 0;
        } finally {
            entityManager.close();
        }
    }

}
