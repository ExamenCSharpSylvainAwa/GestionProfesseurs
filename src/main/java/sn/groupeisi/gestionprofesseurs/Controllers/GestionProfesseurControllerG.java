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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Services.ProfesseurService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class GestionProfesseurControllerG implements Initializable {

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
    private Button btnFermer,btnAjouter;

    private final ProfesseurService professeurService = new ProfesseurService();
    private ObservableList<Users> userList = FXCollections.observableArrayList();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.getItems().addAll("Professeur");
        roleComboBox.setValue("Professeur");

        colnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colrole.setCellValueFactory(new PropertyValueFactory<>("role"));

        chargerProfesseurs();
        userTable.setOnMouseClicked(this::onTableRowDoubleClick);
        btnAjouter.setDisable(false);
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

        if (!isValidEmail(email)) {
            showAlert("Erreur", "L'email fourni n'est pas valide.", Alert.AlertType.ERROR);
            return;
        }

        if (professeurService.emailExist(email)) {
            showAlert("Erreur", "Cet email est déjà utilisé par un autre professeur.", Alert.AlertType.ERROR);
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Users newUser = new Users(null, nom, prenom, email, hashedPassword, role);
        professeurService.ajouterProfesseur(newUser);

        showAlert("Succès", "Professeur ajouté avec succès !", Alert.AlertType.INFORMATION);
        resetFields();
        chargerProfesseurs();
    }

    @FXML
    private void onTableRowDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Users selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                idField.setText(String.valueOf(selectedUser.getId()));
                nomField.setText(selectedUser.getNom());
                prenomField.setText(selectedUser.getPrenom());
                emailField.setText(selectedUser.getEmail());
                roleComboBox.setValue(selectedUser.getRole());
            }
        }
        btnAjouter.setDisable(true);

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

        if (!isValidEmail(email)) {
            showAlert("Erreur", "L'email fourni n'est pas valide.", Alert.AlertType.ERROR);
            return;
        }

        if (professeurService.emailExist(email) && !email.equals(professeurService.getCurrentUserEmail(userId))) {
            showAlert("Erreur", "Cet email est déjà utilisé par un autre professeur.", Alert.AlertType.ERROR);
            return;
        }

        String hashedPassword = password.isEmpty() ? "" : BCrypt.hashpw(password, BCrypt.gensalt());

        Users updatedUser = new Users(userId, nom, prenom, email, hashedPassword, role);
        if (professeurService.modifierProfesseur(updatedUser)) {
            showAlert("Succès", "Professeur modifié avec succès !", Alert.AlertType.INFORMATION);
            resetFields();
        } else {
            showAlert("Erreur", "Professeur introuvable.", Alert.AlertType.ERROR);
        }
        chargerProfesseurs();
    }

    @FXML
    void btnSupprimer(ActionEvent event) {
        if (idField.getText().trim().isEmpty() || !idField.getText().matches("\\d+")) {
            showAlert("Erreur", "Veuillez entrer un ID valide pour supprimer l'utilisateur.", Alert.AlertType.ERROR);
            return;
        }

        Long userId = Long.parseLong(idField.getText().trim());

        if (professeurService.supprimerProfesseur(userId)) {
            showAlert("Succès", "Professeur supprimé avec succès !", Alert.AlertType.INFORMATION);
            resetFields();
            chargerProfesseurs();
        } else {
            showAlert("Erreur", "Professeur introuvable.", Alert.AlertType.ERROR);
        }
    }

    private void chargerProfesseurs() {
        userList.clear();
        List<Users> professeurs = professeurService.listerProfesseurs();
        userList.addAll(professeurs);
        userTable.setItems(userList);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void resetFields() {
        idField.clear();
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue("Professeur");
    }

    @FXML
    void btnFermer(ActionEvent event) {
        try {
            // Charge l'FXML de la page Admin
            URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Gestionnaire.fxml");
            if (fxmlLocation == null) {
                showAlert("Erreur", "Le fichier FXML de la page Admin est introuvable.", Alert.AlertType.ERROR);
                return;
            }

            // Créer une nouvelle scène avec la page Gestionnaire
            Parent gestionnairepage = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) btnFermer.getScene().getWindow();
            stage.setTitle("Page Gestionnaire");

            // Charger la nouvelle scène dans la fenêtre existante
            stage.setScene(new Scene(gestionnairepage));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Gestionnaire : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
