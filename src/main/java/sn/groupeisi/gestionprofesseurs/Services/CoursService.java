package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class CoursService {

    public void ajouterCours(Cours cours) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(cours);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public boolean modifierCours(Cours cours) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Cours existingCours = entityManager.find(Cours.class, cours.getId());
            if (existingCours != null) {
                existingCours.setNom(cours.getNom());
                existingCours.setDescription(cours.getDescription());
                existingCours.setHeureDebut(cours.getHeureDebut());
                existingCours.setHeureFin(cours.getHeureFin());
                existingCours.setSalle(cours.getSalle());
                entityManager.merge(existingCours);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return false;
    }

    public boolean supprimerCours(Long id) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Cours cours = entityManager.find(Cours.class, id);
            if (cours != null) {
                entityManager.remove(cours);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return false;
    }

    public List<Cours> listerCours() {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("FROM Cours", Cours.class).getResultList();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public Cours trouverCoursParId(Long id) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.find(Cours.class, id);
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    public boolean existeCoursParNom(String nom) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            long count = (long) entityManager.createQuery("SELECT COUNT(c) FROM Cours c WHERE c.nom = :nom")
                    .setParameter("nom", nom)
                    .getSingleResult();
            return count > 0;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }
}
