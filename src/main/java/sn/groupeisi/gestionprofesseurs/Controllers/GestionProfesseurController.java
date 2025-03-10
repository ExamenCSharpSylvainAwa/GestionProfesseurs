package sn.groupeisi.gestionprofesseurs.Controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;  // Correct MouseEvent import
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class GestionProfesseurController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private TableView<Users> userTable;
    @FXML
    private TableColumn<Users, String> colnom;
    @FXML
    private TableColumn<Users, String> colprenom;
    @FXML
    private TableColumn<Users, String> colemail;
    @FXML
    private TableColumn<Users, String> colrole;
    @FXML
    private Button btnFermer;

    private final UserService userService = new UserService();
    private ObservableList<Users> userList = FXCollections.observableArrayList();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll("Administrateur", "Professeur", "Gestionnaire");
        roleComboBox.setValue("Professeur"); // Valeur par défaut

        colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colrole.setCellValueFactory(new PropertyValueFactory<>("role"));

        chargerUtilisateurs(); // Charger les utilisateurs existants
        userTable.setOnMouseClicked(this::onTableRowDoubleClick);  // Using the correct MouseEvent
    }

    @FXML
    void btnAjouter(ActionEvent event) {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de la validité de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur", "L'email fourni n'est pas valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification si l'email existe déjà
        if (userService.emailExist(email)) {
            showAlert("Erreur", "Cet email est déjà utilisé par un autre utilisateur.", Alert.AlertType.ERROR);
            return;
        }

        // Hachage du mot de passe
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Ajout de null pour l'ID (généré automatiquement par la BD)
        Users newUser = new Users(null, nom, prenom, email, hashedPassword, role);
        userService.ajouterUtilisateur(newUser);

        showAlert("Succès", "Utilisateur ajouté avec succès !", Alert.AlertType.INFORMATION);
        resetFields();
        chargerUtilisateurs();
    }

    @FXML
    private Button btnAjouter;

    @FXML
    private void onTableRowDoubleClick(MouseEvent event) {
        // Vérifie si l'événement est un double-clic
        if (event.getClickCount() == 2) {
            Users selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Remplir les champs avec les données de l'utilisateur sélectionné
                idField.setText(String.valueOf(selectedUser.getId())); // ID pour la modification ou suppression
                nomField.setText(selectedUser.getNom());
                prenomField.setText(selectedUser.getPrenom());
                emailField.setText(selectedUser.getEmail());
                roleComboBox.setValue(selectedUser.getRole());
                // Ne pas remplir le champ mot de passe

                // Désactiver le bouton Ajouter car on est en mode modification
                btnAjouter.setDisable(true);
            }
        }
    }


    @FXML
    void btnModifier(ActionEvent event) {
        if (idField.getText().trim().isEmpty() || !idField.getText().matches("\\d+")) {
            showAlert("Erreur", "Veuillez entrer un ID valide pour modifier l'utilisateur.", Alert.AlertType.ERROR);
            return;
        }

        Long userId = Long.parseLong(idField.getText().trim());
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String role = roleComboBox.getValue();
        String password = passwordField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || role == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !", Alert.AlertType.ERROR);
            return;
        }

        // Vérification de la validité de l'email
        if (!isValidEmail(email)) {
            showAlert("Erreur", "L'email fourni n'est pas valide.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification si l'email existe déjà pour un autre utilisateur (sauf pour l'utilisateur en cours de modification)
        if (userService.emailExist(email) && !email.equals(userService.getCurrentUserEmail(userId))) {
            showAlert("Erreur", "Cet email est déjà utilisé par un autre utilisateur.", Alert.AlertType.ERROR);
            return;
        }

        // Si le mot de passe est modifié, on le hache
        String hashedPassword = password.isEmpty() ? "" : BCrypt.hashpw(password, BCrypt.gensalt());

        // Mise à jour de l'utilisateur
        Users updatedUser = new Users(userId, nom, prenom, email, hashedPassword, role);
        if (userService.modifierUtilisateur(updatedUser)) {
            showAlert("Succès", "Utilisateur modifié avec succès !", Alert.AlertType.INFORMATION);
            resetFields();
        } else {
            showAlert("Erreur", "Utilisateur introuvable.", Alert.AlertType.ERROR);
        }
        chargerUtilisateurs();
    }


    @FXML
    void btnSupprimer(ActionEvent event) {
        if (idField.getText().trim().isEmpty() || !idField.getText().matches("\\d+")) {
            showAlert("Erreur", "Veuillez entrer un ID valide pour supprimer l'utilisateur.", Alert.AlertType.ERROR);
            return;
        }

        // Afficher une boîte de confirmation avant de supprimer
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
        alert.setContentText("Cette action est irréversible.");

        // Attendre la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Si l'utilisateur confirme, procéder à la suppression
                Long userId = Long.parseLong(idField.getText().trim());
                if (userService.supprimerUtilisateur(userId)) {
                    showAlert("Succès", "Utilisateur supprimé avec succès !", Alert.AlertType.INFORMATION);
                    resetFields();
                } else {
                    showAlert("Erreur", "Échec de la suppression. Vérifiez l'ID.", Alert.AlertType.ERROR);
                }
                chargerUtilisateurs();
            } else {
                // Si l'utilisateur annule, ne faire rien
                showAlert("Annulé", "La suppression a été annulée.", Alert.AlertType.INFORMATION);
            }
        });
    }



    @FXML
    private void btnFermer(ActionEvent event) {
        // Charger la page Admin
        try {
            // Charge l'FXML de la page Admin
            URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Admin.fxml");
            if (fxmlLocation == null) {
                showAlert("Erreur", "Le fichier FXML de la page Admin est introuvable.", Alert.AlertType.ERROR);
                return;
            }

            // Créer une nouvelle scène avec la page Admin
            Parent adminPage = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) btnFermer.getScene().getWindow();
            stage.setTitle("Page Administrateur");

            // Charger la nouvelle scène dans la fenêtre existante
            stage.setScene(new Scene(adminPage));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Admin : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private void resetFields() {
        idField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("Professeur");
        btnAjouter.setDisable(false);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void chargerUtilisateurs() {
        userList.clear(); // Vider la liste avant de la recharger
        List<Users> utilisateurs = userService.listerUtilisateurs();
        userList.addAll(utilisateurs);
        userTable.setItems(userList);
    }

    // Vérifier la validité de l'email avec une expression régulière
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
