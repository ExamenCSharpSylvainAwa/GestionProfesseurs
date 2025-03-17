package sn.groupeisi.gestionprofesseurs.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Services.CoursService;
import sn.groupeisi.gestionprofesseurs.Services.ProfesseurEmargementService;
import sn.groupeisi.gestionprofesseurs.Services.UserService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ProfesseursEmargementController implements Initializable {

    @FXML
    private ComboBox<String> cmbCours;  // Modification ici pour afficher seulement le nom
    @FXML
    private DatePicker dpDate;
    @FXML
    private RadioButton rbPresent;
    @FXML
    private RadioButton rbRetard;
    @FXML
    private TableView<Emargements> tableEmargements;
    @FXML
    private TableColumn<Emargements, LocalDate> colDate;
    @FXML
    private TableColumn<Emargements, String> colCours;
    @FXML
    private TableColumn<Emargements, String> colStatut;
    @FXML
    private TableColumn<Emargements, String> colProfesseur;
    @FXML
    private Button btnFermer,btnEmarger;

    private static final Logger logger = Logger.getLogger(ProfesseursEmargementController.class.getName());

    private final CoursService coursService = new CoursService();
    private final ProfesseurEmargementService emargementService = new ProfesseurEmargementService();
    private Users professeurConnecte;
    private List<Cours> listeCours; // Liste pour stocker les cours avec leur ID

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        professeurConnecte = UserService.getCurrentUser();

        if (professeurConnecte == null || !professeurConnecte.getRole().equals("Professeur")) {
            showAlert("Erreur", "Aucun professeur connecté.");
            return;
        }

        chargerCours();
        chargerEmargements();

        colDate.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));
        colCours.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCours().getNom()));
        colStatut.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatut()));
        colProfesseur.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProfesseur().getPrenom() + " " + cellData.getValue().getProfesseur().getNom()));

        dpDate.setDayCellFactory(d -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isAfter(LocalDate.now()));
            }
        });

        // Ajouter l'événement de double-clic sur un émargement
        tableEmargements.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-clic détecté
                recupererEmargementSelectionne();
            }
        });

        viderChamps();
    }

    private LocalTime heureDebut;
    private void chargerCours() {
        listeCours = emargementService.getAllCours();
        ObservableList<String> coursNoms = FXCollections.observableArrayList();

        for (Cours cours : listeCours) {
            coursNoms.add(cours.getNom());
        }

        cmbCours.setItems(coursNoms);
    }

    private void chargerEmargements() {
        List<Emargements> emargements = emargementService.getEmargementsByProfesseur(professeurConnecte.getId());
        tableEmargements.setItems(FXCollections.observableArrayList(emargements));
    }

    @FXML
    private void btnEmarger() {
        String coursNomSelectionne = cmbCours.getValue();
        LocalDate dateEmargement = dpDate.getValue();
        String statut = rbPresent.isSelected() ? "Présent" : rbRetard.isSelected() ? "Retard" : "";

        if (coursNomSelectionne == null || dateEmargement == null || statut.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Cours coursSelectionne = listeCours.stream()
                .filter(c -> c.getNom().equals(coursNomSelectionne))
                .findFirst()
                .orElse(null);

        if (coursSelectionne == null) {
            showAlert("Erreur", "Cours invalide.");
            return;
        }

        // Vérification de l'heure d'émargement
        LocalTime heureActuelle = LocalTime.now();
        LocalTime heureCours = coursSelectionne.getHeureDebut(); // Suppose que tu as une colonne heureDebut
        if (heureCours == null) {
            showAlert("Erreur", "L'heure du cours n'est pas définie.");
            return;
        }

        if (heureActuelle.isBefore(heureCours.minusMinutes(15)) || heureActuelle.isAfter(heureCours.plusMinutes(15))) {
            showAlert("Erreur", "Vous ne pouvez émarger que 15 minutes avant ou après le début du cours.");
            return;
        }

        if (emargementService.existeEmargement(professeurConnecte.getId(), coursSelectionne.getId(), dateEmargement)) {
            showAlert("Erreur", "Vous avez déjà émargé pour ce cours aujourd'hui !");
            return;
        }

        Emargements nouvelEmargement = new Emargements();
        nouvelEmargement.setDate(dateEmargement);
        nouvelEmargement.setStatut(statut);
        nouvelEmargement.setProfesseur(professeurConnecte);
        nouvelEmargement.setCours(coursSelectionne);

        emargementService.ajouterEmargement(nouvelEmargement);

        chargerEmargements();
        viderChamps();

        showAlert("Succès", "Émargement enregistré avec succès !");
    }



    @FXML
    private void btnModifierEmargement() {
        Emargements emargementSelectionne = tableEmargements.getSelectionModel().getSelectedItem();
        String coursNomSelectionne = cmbCours.getValue();
        LocalDate dateEmargement = dpDate.getValue();
        String statut = rbPresent.isSelected() ? "Présent" : rbRetard.isSelected() ? "Retard" : "";

        if (emargementSelectionne == null || coursNomSelectionne == null || dateEmargement == null || statut.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner un émargement et remplir tous les champs.");
            return;
        }

        // Vérification si la date d'émargement est dans le passé
        if (dateEmargement.isBefore(LocalDate.now())) {
            showAlert("Erreur", "Vous ne pouvez pas modifier un émargement pour une date passée.");
            return;
        }

        // Vérification de l'existence du cours sélectionné
        Cours coursSelectionne = listeCours.stream()
                .filter(c -> c.getNom().equals(coursNomSelectionne))
                .findFirst()
                .orElse(null);

        if (coursSelectionne == null) {
            showAlert("Erreur", "Cours invalide.");
            return;
        }

        // Mise à jour des informations de l'émargement
        emargementSelectionne.setDate(dateEmargement);
        emargementSelectionne.setStatut(statut);
        emargementSelectionne.setCours(coursSelectionne);

        // Modification de l'émargement dans la base de données
        emargementService.modifierEmargement(emargementSelectionne);

        // Rechargement de la liste des émargements
        chargerEmargements();
        viderChamps();



        // Affichage du message de succès
        showAlert("Succès", "L'émargement a été modifié avec succès !");
    }

    @FXML
    private void recupererEmargementSelectionne() {
        Emargements emargementSelectionne = tableEmargements.getSelectionModel().getSelectedItem();

        if (emargementSelectionne != null) {
            cmbCours.setValue(emargementSelectionne.getCours().getNom());
            dpDate.setValue(emargementSelectionne.getDate());

            if (emargementSelectionne.getStatut().equals("Présent")) {
                rbPresent.setSelected(true);
                rbRetard.setSelected(false);
            } else {
                rbPresent.setSelected(false);
                rbRetard.setSelected(true);
            }
        }
        btnEmarger.setDisable(true);
    }

    @FXML
    private void btnFermer() {
        try {
            URL fxmlLocation = getClass().getResource("/sn/groupeisi/gestionprofesseurs/pages/Login.fxml");
            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) btnFermer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de fermer la session.");
            logger.severe("Erreur lors de la fermeture de la session : " + e.getMessage());
        }
    }

    private void viderChamps() {
        cmbCours.getSelectionModel().clearSelection();
        dpDate.setValue(null);
        rbPresent.setSelected(false);
        rbRetard.setSelected(false);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
