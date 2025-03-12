package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class ProfesseurService {

    // Ajouter un utilisateur de type Professeur
    public void ajouterProfesseur(Users user) {
        if (!"Professeur".equals(user.getRole())) {
            throw new IllegalArgumentException("Le rôle de l'utilisateur doit être Professeur");
        }

        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }
    public String getCurrentUserEmail(Long userId) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            Users user = entityManager.find(Users.class, userId);
            if (user != null) {
                return user.getEmail(); // Retourne l'email de l'utilisateur trouvé
            }
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return null;
    }
    // Modifier un utilisateur de type Professeur
    public boolean modifierProfesseur(Users user) {
        if (!"Professeur".equals(user.getRole())) {
            throw new IllegalArgumentException("Le rôle de l'utilisateur doit être Professeur");
        }

        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Users existingUser = entityManager.find(Users.class, user.getId());
            if (existingUser != null && "Professeur".equals(existingUser.getRole())) {
                existingUser.setNom(user.getNom());
                existingUser.setPrenom(user.getPrenom());
                existingUser.setEmail(user.getEmail());
                existingUser.setRole(user.getRole());
                entityManager.merge(existingUser);
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

    // Supprimer un utilisateur de type Professeur
    public boolean supprimerProfesseur(Long id) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Users user = entityManager.find(Users.class, id);
            if (user != null && "Professeur".equals(user.getRole())) {
                entityManager.remove(user);
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

    // Lister uniquement les utilisateurs de type Professeur
    public List<Users> listerProfesseurs() {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("FROM Users u WHERE u.role = 'Professeur'", Users.class).getResultList();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    // Vérifier si un email existe déjà pour un Professeur
    public boolean emailExist(String email) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            long count = (long) entityManager.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email AND u.role = 'Professeur'")
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    // Trouver un Professeur par email
    public Users findProfesseurByEmail(String email) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email AND u.role = 'Professeur'", Users.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }

    // Trouver un utilisateur Professeur par son ID
    public Users getProfesseurById(Long userId) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.find(Users.class, userId); // Recherche de l'utilisateur Professeur par son ID
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }
}
