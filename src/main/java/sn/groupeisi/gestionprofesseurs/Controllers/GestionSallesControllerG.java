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
import sn.groupeisi.gestionprofesseurs.Entities.Salle;
import sn.groupeisi.gestionprofesseurs.Services.SalleService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GestionSallesControllerG implements Initializable {

    @FXML
    private TextField txtLibelle;
    @FXML
    private TableView<Salle> salleTable;
    @FXML
    private TableColumn<Salle, Integer> colId;
    @FXML
    private TableColumn<Salle, String> colLibelle;
    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnFermer;

    private final SalleService salleService = new SalleService();
    private ObservableList<Salle> salleList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des colonnes de la table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLibelle.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        chargerSalles();

        // Action lors du clic sur une ligne de la table
        salleTable.setOnMouseClicked(this::onTableRowClick);
    }

    @FXML
    void btnAjouter(ActionEvent event) {
        String libelle = txtLibelle.getText().trim();
        if (libelle.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un libellé de salle.", Alert.AlertType.ERROR);
            return;
        }
        Salle salle = new Salle(null, libelle);
        salleService.ajouterSalle(salle);
        showAlert("Succès", "Salle ajoutée avec succès !", Alert.AlertType.INFORMATION);
        resetFields();
        chargerSalles();
    }

    @FXML
    void btnModifier(ActionEvent event) {
        Salle selectedSalle = salleTable.getSelectionModel().getSelectedItem();
        if (selectedSalle == null) {
            showAlert("Erreur", "Veuillez sélectionner une salle à modifier.", Alert.AlertType.ERROR);
            return;
        }
        String libelle = txtLibelle.getText().trim();
        if (libelle.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un libellé.", Alert.AlertType.ERROR);
            return;
        }
        selectedSalle.setLibelle(libelle);
        salleService.modifierSalle(selectedSalle);
        showAlert("Succès", "Salle modifiée avec succès !", Alert.AlertType.INFORMATION);
        resetFields();
        chargerSalles();
    }

    @FXML
    void btnSupprimer(ActionEvent event) {
        Salle selectedSalle = salleTable.getSelectionModel().getSelectedItem();
        if (selectedSalle == null) {
            showAlert("Erreur", "Veuillez sélectionner une salle à supprimer.", Alert.AlertType.ERROR);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette salle ?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                salleService.supprimerSalle(selectedSalle.getId());
                showAlert("Succès", "Salle supprimée avec succès !", Alert.AlertType.INFORMATION);
                resetFields();
                chargerSalles();
            }
        });
    }

    @FXML
    void btnFermer(ActionEvent event) {
        try {
            // Assurez-vous que le fichier fxml est correct
            String fxmlPath = "/sn/groupeisi/gestionprofesseurs/pages/Gestionnaire.fxml";
            URL fxmlLocation = getClass().getResource(fxmlPath);
            if (fxmlLocation == null) {
                showAlert("Erreur", "Le fichier FXML de la page Gestionnaire est introuvable.", Alert.AlertType.ERROR);
                return;
            }

            // Charger explicitement la page Gestionnaire.fxml
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent gestionnairePage = loader.load();

            // Récupérer la scène actuelle et changer la scène
            Stage stage = (Stage) btnFermer.getScene().getWindow();
            stage.setTitle("Page Gestionnaire");

            // Charger la nouvelle scène dans la fenêtre existante
            stage.setScene(new Scene(gestionnairePage));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page Gestionnaire : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }



    private void onTableRowClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            Salle selectedSalle = salleTable.getSelectionModel().getSelectedItem();
            if (selectedSalle != null) {
                txtLibelle.setText(selectedSalle.getLibelle());
                btnAjouter.setDisable(true);
            }
        }
    }

    private void chargerSalles() {
        salleList.clear();
        List<Salle> salles = salleService.listerSalles();
        salleList.addAll(salles);
        salleTable.setItems(salleList);
    }

    private void resetFields() {
        txtLibelle.clear();
        btnAjouter.setDisable(false);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
