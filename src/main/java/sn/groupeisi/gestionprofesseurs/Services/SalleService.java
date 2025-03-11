package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Salle;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class SalleService {

    /**
     * Ajoute une nouvelle salle dans la base de données.
     */
    public void ajouterSalle(Salle salle) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(salle);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Modifie les informations d'une salle existante.
     */
    public boolean modifierSalle(Salle salle) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Recherche de la salle existante en fonction de l'ID
            Salle existingSalle = entityManager.find(Salle.class, salle.getId());

            if (existingSalle != null) {
                // Mise à jour des attributs de la salle avec les valeurs passées
                existingSalle.setLibelle(salle.getLibelle());  // Utilisation du setter pour mettre à jour le libellé

                // Vous pouvez ajouter d'autres champs à mettre à jour si nécessaire
                // Par exemple : existingSalle.setCapacite(salle.getCapacite());

                // Fusion des changements dans la base de données
                entityManager.merge(existingSalle);
                transaction.commit();
                return true;
            }
        } catch (Exception e) {
            // Si une erreur se produit, rollback de la transaction
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            // Fermeture de l'entity manager dans le bloc finally
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return false;
    }

    /**
     * Supprime une salle de la base de données en fonction de son ID.
     */
    public boolean supprimerSalle(Long id) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            Salle salle = entityManager.find(Salle.class, id);
            if (salle != null) {
                entityManager.remove(salle);
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
     * Retourne la liste de toutes les salles enregistrées.
     */
    public List<Salle> listerSalles() {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            return entityManager.createQuery("FROM Salle", Salle.class).getResultList();
        } finally {
            entityManager.close();
        }
    }

    /**
     * Vérifie si une salle existe déjà en fonction de son libelle.
     */
    public boolean salleExist(String libelle) {
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        try {
            long count = (long) entityManager.createQuery("SELECT COUNT(s) FROM Salle s WHERE s.libelle = :libelle")
                    .setParameter("libelle", libelle)
                    .getSingleResult();
            return count > 0;
        } finally {
            entityManager.close();
        }
    }
}
