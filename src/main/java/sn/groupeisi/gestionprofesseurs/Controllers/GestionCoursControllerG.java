package sn.groupeisi.gestionprofesseurs.Controllers;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Entities.Salle;
import sn.groupeisi.gestionprofesseurs.Services.CoursService;
import sn.groupeisi.gestionprofesseurs.Services.SalleService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class GestionCoursControllerG implements Initializable {

    @FXML
    private TextField txtNom;

    @FXML
    private TextArea txtDescription;

    @FXML
    private ComboBox<String> cbHeureDebut;

    @FXML
    private ComboBox<String> cbHeureFin;

    @FXML
    private ComboBox<Salle> cbSalle;

    @FXML
    private TableView<Cours> tableCours;

    @FXML
    private TableColumn<Cours, String> colNom;

    @FXML
    private TableColumn<Cours, String> colDescription;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureDebut;

    @FXML
    private TableColumn<Cours, LocalTime> colHeureFin;

    @FXML
    private TableColumn<Cours, String> colSalle;

    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnFermer;

    private final CoursService coursService = new CoursService();
    private final SalleService salleService = new SalleService();
    private ObservableList<Cours> coursList = FXCollections.observableArrayList();
    private ObservableList<Salle> salleList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerSalles();
        chargerHeures();
        configurerTableCours();
        chargerCours();
    }

    private void chargerHeures() {
        for (int heure = 0; heure < 24; heure++) {
            for (int minute = 0; minute < 60; minute += 15) { // Intervalle de 15 minutes
                String time = String.format("%02d:%02d", heure, minute);
                cbHeureDebut.getItems().add(time);
                cbHeureFin.getItems().add(time);
            }
        }
    }

    private void configurerTableCours() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colSalle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));

        tableCours.setRowFactory(tv -> {
            TableRow<Cours> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    chargerFormulaire(row.getItem());
                }
            });
            return row;
        });

        // Initialement, désactiver les boutons de modification et suppression
        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    private void chargerFormulaire(Cours cours) {
        if (cours == null) {
            showAlert("Erreur", "Aucun cours sélectionné.", Alert.AlertType.ERROR);
            return;
        }

        txtNom.setText(cours.getNom());
        txtDescription.setText(cours.getDescription());
        cbHeureDebut.setValue(cours.getHeureDebut() != null ? cours.getHeureDebut().toString() : null);
        cbHeureFin.setValue(cours.getHeureFin() != null ? cours.getHeureFin().toString() : null);
        cbSalle.getSelectionModel().select(cours.getSalle());

        btnAjouter.setDisable(true);
        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }

    private void chargerSalles() {
        salleList.clear();
        salleList.addAll(salleService.listerSalles());

        // Personnaliser l'affichage du ComboBox
        cbSalle.setItems(salleList);

        // Utiliser un StringConverter pour afficher le libellé de la salle
        cbSalle.setConverter(new StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getLibelle() : ""; // Remplacer getNom() par getLibelle()
            }

            @Override
            public Salle fromString(String string) {
                // Cette méthode peut être vide si vous n'avez pas besoin de convertir le texte en objet Salle.
                return null;
            }
        });
    }


    private void chargerCours() {
        coursList.clear();
        coursList.addAll(coursService.listerCours());
        tableCours.setItems(coursList);
    }

    private boolean existeConflitHoraire(Salle salle, LocalTime heureDebut, LocalTime heureFin, Cours coursExclu) {
        return coursList.stream().anyMatch(cours ->
                !cours.equals(coursExclu) &&
                        cours.getSalle().equals(salle) &&
                        ((heureDebut.isBefore(cours.getHeureFin()) && heureDebut.isAfter(cours.getHeureDebut())) ||
                                (heureFin.isAfter(cours.getHeureDebut()) && heureFin.isBefore(cours.getHeureFin())) ||
                                (heureDebut.equals(cours.getHeureDebut()) || heureFin.equals(cours.getHeureFin())))
        );
    }

    @FXML
    void btnAjouter(ActionEvent event) {
        String nom = txtNom.getText().trim();
        String description = txtDescription.getText().trim();
        Salle salle = cbSalle.getSelectionModel().getSelectedItem();
        String heureDebutStr = cbHeureDebut.getValue();
        String heureFinStr = cbHeureFin.getValue();

        if (nom.isEmpty() || description.isEmpty() || salle == null || heureDebutStr == null || heureFinStr == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
            return;
        }

        LocalTime heureDebut = LocalTime.parse(heureDebutStr);
        LocalTime heureFin = LocalTime.parse(heureFinStr);

        if (existeConflitHoraire(salle, heureDebut, heureFin, null)) {
            showAlert("Erreur", "Un cours est déjà prévu dans cette salle à ce créneau horaire.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification si le cours avec ce nom existe déjà
        if (coursService.existeCoursParNom(nom)) {
            showAlert("Erreur", "Un cours avec ce nom existe déjà.", Alert.AlertType.ERROR);
            return;
        }

        Cours nouveauCours = new Cours(null, nom, description, heureDebut, heureFin, salle);
        coursService.ajouterCours(nouveauCours);
        showAlert("Succès", "Cours ajouté avec succès !", Alert.AlertType.INFORMATION);
        resetFields();
        chargerCours();
    }

    @FXML
    void btnModifier(ActionEvent event) {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours à modifier.", Alert.AlertType.ERROR);
            return;
        }

        LocalTime heureDebut = LocalTime.parse(cbHeureDebut.getValue());
        LocalTime heureFin = LocalTime.parse(cbHeureFin.getValue());
        Salle salle = cbSalle.getSelectionModel().getSelectedItem();

        // Vérification si un conflit horaire existe
        if (existeConflitHoraire(salle, heureDebut, heureFin, selectedCours)) {
            showAlert("Erreur", "Un autre cours existe déjà sur ce créneau dans cette salle.", Alert.AlertType.ERROR);
            return;
        }

        // Mise à jour des informations du cours
        selectedCours.setNom(txtNom.getText());
        selectedCours.setDescription(txtDescription.getText());
        selectedCours.setHeureDebut(heureDebut);
        selectedCours.setHeureFin(heureFin);
        selectedCours.setSalle(salle);

        // Modification du cours via le service
        boolean isModified = coursService.modifierCours(selectedCours);
        if (isModified) {
            showAlert("Succès", "Cours modifié !", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Impossible de modifier le cours.", Alert.AlertType.ERROR);
        }
        resetFields();
        chargerCours();
    }

    @FXML
    void btnSupprimer(ActionEvent event) {
        Cours selectedCours = tableCours.getSelectionModel().getSelectedItem();
        if (selectedCours == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours à supprimer.", Alert.AlertType.ERROR);
            return;
        }

        boolean confirmation = showConfirmationDialog("Confirmer la suppression", "Êtes-vous sûr de vouloir supprimer ce cours ?");
        if (confirmation) {
            // Supprimer le cours via son ID
            boolean isDeleted = coursService.supprimerCours(selectedCours.getId());
            if (isDeleted) {
                showAlert("Succès", "Cours supprimé avec succès !", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Impossible de supprimer le cours.", Alert.AlertType.ERROR);
            }
            resetFields();
            chargerCours();
        }
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


    private void resetFields() {
        txtNom.clear();
        txtDescription.clear();
        cbHeureDebut.getSelectionModel().clearSelection();
        cbHeureFin.getSelectionModel().clearSelection();
        cbSalle.getSelectionModel().clearSelection();

        btnAjouter.setDisable(false);
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().get() == ButtonType.OK;
    }
}
