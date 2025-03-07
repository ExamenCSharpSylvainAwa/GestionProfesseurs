package sn.groupeisi.examensf.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.query.Query;
import sn.groupeisi.examensf.Entities.Users;
import sn.groupeisi.examensf.Utils.HibernateUtil;

import java.io.IOException;

public class LoginController {

    @FXML
    public TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        // Ajouter un écouteur d'événements au bouton
    }
    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Vérification que les champs ne sont pas vides
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert("Erreur", "Veuillez entrer une adresse email valide.", Alert.AlertType.ERROR);
            return;
        }
        // Utilisation de Hibernate pour vérifier l'utilisateur
        try {
            // Ouvrir une session Hibernate
            Session session = HibernateUtil.getSessionFactory().openSession();

            // Requête HQL pour vérifier l'utilisateur
            String hql = "FROM Users u WHERE u.email = :email AND u.password = :password";
            Query<Users> query = session.createQuery(hql, Users.class);
            query.setParameter("email", email);
            query.setParameter("password", password); // Assurez-vous de hasher le mot de passe

            // Exécuter la requête et récupérer l'utilisateur
            Users user = query.uniqueResult();

            if (user != null) {
                // Authentification réussie
                System.out.println("Connexion réussie !");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("adminDashboard.fxml"));
                    Parent root = loader.load();

                    // Obtenir la scène actuelle
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Tableau de Bord - Admin");
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de charger le tableau de bord.", Alert.AlertType.ERROR);
                }
            } else {
                // Utilisateur non trouvé
                showAlert("Erreur", "Email ou mot de passe incorrect.", Alert.AlertType.ERROR);
            }

            // Fermer la session Hibernate
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la connexion.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
