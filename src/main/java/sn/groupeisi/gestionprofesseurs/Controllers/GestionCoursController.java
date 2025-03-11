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
import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Entities.Salle;
import sn.groupeisi.gestionprofesseurs.Services.CoursService;
import sn.groupeisi.gestionprofesseurs.Services.SalleService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class GestionCoursController implements Initializable {

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
        // Charger les salles dans le ComboBox
        chargerSalles();

        // Configurer les colonnes de la TableView
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colHeureDebut.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        colHeureFin.setCellValueFactory(new PropertyValueFactory<>("heureFin"));
        colSalle.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));



        tableCours.setRowFactory(tv -> {
            TableRow<Cours> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) { // Double-clic
                    Cours selectedCours = row.getItem();
                    chargerFormulaire(selectedCours); // Remplit le formulaire
                }
            });
            return row;
        });
        btnAjouter.setDisable(false);  // Ajouter est actif par défaut
        btnModifier.setDisable(true);  // Modifier désactivé par défaut
        btnSupprimer.setDisable(true); // Supprimer désactivé par défaut


        // Charger les cours
        chargerCours();
        chargerHeures(); // Charger les heures
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
    private void chargerFormulaire(Cours cours) {
        txtNom.setText(cours.getNom());
        txtDescription.setText(cours.getDescription());

        // Sélection de l'heure dans les ComboBox
        cbHeureDebut.setValue(cours.getHeureDebut().toString());
        cbHeureFin.setValue(cours.getHeureFin().toString());

        // Sélection de la salle
        cbSalle.getSelectionModel().select(cours.getSalle());

        // Désactiver le bouton Ajouter et activer Modifier / Supprimer
        btnAjouter.setDisable(true);
        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }

    private void chargerSalles() {
        salleList.clear();
        List<Salle> salles = salleService.listerSalles();
        salleList.addAll(salles);
        cbSalle.setItems(salleList);

        // Afficher uniquement le libellé de la salle
        cbSalle.setCellFactory(param -> new ListCell<Salle>() {
            @Override
            protected void updateItem(Salle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLibelle());
                }
            }
        });

        cbSalle.setButtonCell(new ListCell<Salle>() {
            @Override
            protected void updateItem(Salle item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLibelle());
                }
            }
        });
    }

    private void chargerCours() {
        coursList.clear();
        List<Cours> cours = coursService.listerCours();
        if (cours == null || cours.isEmpty()) {
            System.out.println("Aucun cours à afficher.");
        } else {
            coursList.addAll(cours);
        }
        tableCours.setItems(coursList);
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

        Cours cours = new Cours(null, nom, description, heureDebut, heureFin, salle);
        coursService.ajouterCours(cours);

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

        selectedCours.setNom(nom);
        selectedCours.setDescription(description);
        selectedCours.setHeureDebut(heureDebut);
        selectedCours.setHeureFin(heureFin);
        selectedCours.setSalle(salle);

        coursService.modifierCours(selectedCours);

        showAlert("Succès", "Cours modifié avec succès !", Alert.AlertType.INFORMATION);
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce cours ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                coursService.supprimerCours(selectedCours.getId());
                showAlert("Succès", "Cours supprimé avec succès !", Alert.AlertType.INFORMATION);
                resetFields();
                chargerCours();
            }
        });
    }

    @FXML
    private void btnFermer(ActionEvent event) {
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
        txtNom.clear();
        txtDescription.clear();
        cbHeureDebut.getSelectionModel().clearSelection();
        cbHeureFin.getSelectionModel().clearSelection();
        cbSalle.getSelectionModel().clearSelection();

        // Activer le bouton Ajouter et désactiver Modifier / Supprimer
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
}
