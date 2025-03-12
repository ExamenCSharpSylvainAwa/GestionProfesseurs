package sn.groupeisi.gestionprofesseurs.Services;

import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Utils.HibernateUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

public class UserService {

    public void ajouterUtilisateur(Users user) {
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

    public boolean modifierUtilisateur(Users user) {
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Users existingUser = entityManager.find(Users.class, user.getId());
            if (existingUser != null) {
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

    public boolean supprimerUtilisateur(Long id) { // Modifier le type de paramètre à Long
        EntityTransaction transaction = null;
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            transaction = entityManager.getTransaction();
            transaction.begin();
            Users user = entityManager.find(Users.class, id); // Utiliser Long ici également
            if (user != null) {
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


    public List<Users> listerUtilisateurs() {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("FROM Users", Users.class).getResultList();
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }
    public boolean emailExist(String email) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            // Recherche d'un utilisateur avec l'email spécifié
            long count = (long) entityManager.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0; // Retourne true si un utilisateur avec cet email existe
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
                return user.getEmail();
            }
        } finally {
            if (entityManager != null) entityManager.close();
        }
        return null;
    }
    public Users findByEmail(String email) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Si l'utilisateur n'est pas trouvé
        } finally {
            if (entityManager != null) entityManager.close();
        }
    }
    public Users getUtilisateurConnecte(Long userId) {
        EntityManager entityManager = null;
        try {
            entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
            return entityManager.find(Users.class, userId); // Recherche de l'utilisateur par son ID
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }


}
