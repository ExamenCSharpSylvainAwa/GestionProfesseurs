package sn.groupeisi.gestionprofesseurs.Services;

import org.hibernate.Session;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EmargementService {

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

    public List<Emargements> listerEmargements() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createQuery("FROM Emargements", Emargements.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    public boolean emargementExiste(Long professeurId, Long coursId, LocalDate date) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            List<Emargements> result = entityManager.createQuery(
                            "FROM Emargements e WHERE e.professeur.id = :professeurId " +
                                    "AND e.cours.id = :coursId AND e.date = :date", Emargements.class)
                    .setParameter("professeurId", professeurId)
                    .setParameter("coursId", coursId)
                    .setParameter("date", Date.valueOf(date))
                    .setMaxResults(1)  // Limite la recherche à un seul résultat
                    .getResultList();
            return !result.isEmpty();
        } finally {
            entityManager.close();
        }
    }

    public List<Emargements> getEmargementsByProfesseur(Long professeurId) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("FROM Emargements e WHERE e.professeur.id = :profId", Emargements.class)
                    .setParameter("profId", professeurId)
                    .getResultList();
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
