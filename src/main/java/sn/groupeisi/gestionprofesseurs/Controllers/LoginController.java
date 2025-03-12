package sn.groupeisi.gestionprofesseurs.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane connexionPage;

    private final UserService userService = new UserService();

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Email et mot de passe sont requis", Alert.AlertType.ERROR);
            return;
        }

        // Vérifier si l'utilisateur existe
        Users user = userService.findByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) { // Utilisation de getPassword() au lieu de getMotDePasse()
            // Vérifier si l'utilisateur est un administrateur
            if ("Administrateur".equals(user.getRole())) {
                // Si l'utilisateur est un administrateur, charger la page Admin
                loadAdminPage();
            }if ("Gestionnaire".equals(user.getRole())){
                loadGestionnairePage();
            }
            else {
                showAlert("Erreur", "Vous n'êtes pas autorisé à accéder à cette page", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
        }
    }


    private void loadAdminPage() {
        try {
            // Vérifie si la ressource existe
            URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Admin.fxml");

            if (fxmlLocation == null) {
                showAlert("Erreur", "Le fichier FXML est introuvable : pages/Admin.fxml", Alert.AlertType.ERROR);
                return;
            }

            Parent fxml = FXMLLoader.load(fxmlLocation);
            this.connexionPage.getChildren().clear();
            this.connexionPage.getChildren().add(fxml);
            Stage stage = (Stage) connexionPage.getScene().getWindow();
            stage.setTitle("Admin");

        } catch (IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console
            showAlert("Erreur", "Impossible de charger le tableau de bord : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void loadGestionnairePage() {
        try {
            // Vérifie si la ressource existe
            URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Gestionnaire.fxml");

            if (fxmlLocation == null) {
                showAlert("Erreur", "Le fichier FXML est introuvable : pages/Admin.fxml", Alert.AlertType.ERROR);
                return;
            }

            Parent fxml = FXMLLoader.load(fxmlLocation);
            this.connexionPage.getChildren().clear();
            this.connexionPage.getChildren().add(fxml);
            Stage stage = (Stage) connexionPage.getScene().getWindow();
            stage.setTitle("Admin");

        } catch (IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console
            showAlert("Erreur", "Impossible de charger le tableau de bord : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation si nécessaire
    }
}
