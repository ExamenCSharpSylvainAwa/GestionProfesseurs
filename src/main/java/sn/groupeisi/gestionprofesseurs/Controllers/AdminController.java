package sn.groupeisi.gestionprofesseurs.Controllers;
import javafx.event.ActionEvent;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    void btnCours(ActionEvent event) {

    }

    @FXML
    void btnEmergements(ActionEvent event) {

    }

    @FXML
    void btnProfesseurs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/GestionProfesseurs.fxml"));
            Parent fxml = loader.load();
            // Charge le fichier FXML correctement


            // Récupère la scène actuelle
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Remplace la scène avec la nouvelle page
            stage.setScene(new Scene(fxml));
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnRapport(ActionEvent event) {

    }
    @FXML
    private Button btnDeconnexion;

    @FXML
    private void btnDeconnexion(ActionEvent event) {
        // Afficher une boîte de confirmation avant de déconnecter
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de déconnexion");
        alert.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        alert.setContentText("Vous allez revenir à la page de connexion.");

        // Attendre la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Charger la page de connexion
                    URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Login.fxml");
                    if (fxmlLocation == null) {
                        showAlert("Erreur", "Le fichier FXML de la page de connexion est introuvable.", Alert.AlertType.ERROR);
                        return;
                    }

                    Parent loginPage = FXMLLoader.load(fxmlLocation);
                    Stage stage = (Stage) btnDeconnexion.getScene().getWindow();
                    stage.setTitle("Connexion");
                    stage.setScene(new Scene(loginPage));

                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible de charger la page de connexion : " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                showAlert("Annulé", "La déconnexion a été annulée.", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
