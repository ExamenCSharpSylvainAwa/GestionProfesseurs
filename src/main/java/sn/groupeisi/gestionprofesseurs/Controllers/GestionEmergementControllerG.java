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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sn.groupeisi.gestionprofesseurs.Entities.Cours;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Entities.Users;
import sn.groupeisi.gestionprofesseurs.Services.CoursService;
import sn.groupeisi.gestionprofesseurs.Services.EmargementService;
import sn.groupeisi.gestionprofesseurs.Services.UserService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GestionEmergementControllerG implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private CheckBox cbPresent, cbRetard, cbAbsent, cbExcuse;

    @FXML
    private ComboBox<Users> comboProfesseur;

    @FXML
    private ComboBox<Cours> comboCours;

    @FXML
    private TableView<Emargements> tableView;

    @FXML
    private TableColumn<Emargements, String> colDate, colStatut, colProfesseur, colCours;

    @FXML
    private Button btnAjouter, btnModifier, btnSupprimer, btnFermer;

    private final ObservableList<Emargements> listeEmargements = FXCollections.observableArrayList();
    private final ObservableList<Users> listeProfesseurs = FXCollections.observableArrayList();
    private final ObservableList<Cours> listeCours = FXCollections.observableArrayList();

    private final CoursService coursService = new CoursService();
    private final UserService userService = new UserService();
    private final EmargementService emargementService = new EmargementService();

    private void configurerComboBoxProfesseur() {
        comboProfesseur.setCellFactory(param -> new ListCell<Users>() {
            @Override
            protected void updateItem(Users user, boolean empty) {
                super.updateItem(user, empty);
                setText((empty || user == null) ? null : user.getNom() + " " + user.getPrenom());
            }
        });
        comboProfesseur.setButtonCell(new ListCell<Users>() {
            @Override
            protected void updateItem(Users user, boolean empty) {
                super.updateItem(user, empty);
                setText((empty || user == null) ? null : user.getNom() + " " + user.getPrenom());
            }
        });
    }

    private void configurerComboBoxCours() {
        comboCours.setCellFactory(param -> new ListCell<Cours>() {
            @Override
            protected void updateItem(Cours cours, boolean empty) {
                super.updateItem(cours, empty);
                setText((empty || cours == null) ? null : cours.getNom());
            }
        });
        comboCours.setButtonCell(new ListCell<Cours>() {
            @Override
            protected void updateItem(Cours cours, boolean empty) {
                super.updateItem(cours, empty);
                setText((empty || cours == null) ? null : cours.getNom());
            }
        });
    }

    private void chargerProfesseurs() {
        List<Users> utilisateurs = userService.listerUtilisateurs();
        List<Users> professeurs = utilisateurs.stream()
                .filter(user -> "Professeur".equals(user.getRole()))
                .collect(Collectors.toList());
        System.out.println("Professeurs chargés : " + professeurs.size()); // Log
        listeProfesseurs.setAll(professeurs);
        comboProfesseur.setItems(listeProfesseurs);
    }

    private void chargerCours() {
        List<Cours> cours = coursService.listerCours();
        System.out.println("Cours chargés : " + cours.size()); // Log
        listeCours.setAll(cours);
        comboCours.setItems(listeCours);
    }

    private void chargerEmargements() {
        List<Emargements> emargements = emargementService.listerEmargements();
        System.out.println("Emargements chargés : " + emargements.size()); // Log
        listeEmargements.setAll(emargements);
        tableView.setItems(listeEmargements);
    }

    private void verifierChamps() {
        boolean dateRemplie = datePicker.getValue() != null;
        boolean statutSelectionne = cbPresent.isSelected() || cbRetard.isSelected() || cbAbsent.isSelected() || cbExcuse.isSelected();
        boolean professeurSelectionne = comboProfesseur.getValue() != null;
        boolean coursSelectionne = comboCours.getValue() != null;

        btnAjouter.setDisable(!(dateRemplie && statutSelectionne && professeurSelectionne && coursSelectionne));
    }

    @FXML
    private void handleDateChange() {
        verifierChamps();
    }

    @FXML
    private void cbPresent() {
        if (cbPresent.isSelected()) {
            cbRetard.setSelected(false);
            cbAbsent.setSelected(false);
            cbExcuse.setSelected(false);
        }
        verifierChamps();
    }

    @FXML
    private void cbRetard() {
        if (cbRetard.isSelected()) {
            cbPresent.setSelected(false);
            cbAbsent.setSelected(false);
            cbExcuse.setSelected(false);
        }
        verifierChamps();
    }

    @FXML
    private void cbAbsent() {
        if (cbAbsent.isSelected()) {
            cbPresent.setSelected(false);
            cbRetard.setSelected(false);
            cbExcuse.setSelected(false);
        }
        verifierChamps();
    }

    @FXML
    private void cbExcuse() {
        if (cbExcuse.isSelected()) {
            cbPresent.setSelected(false);
            cbRetard.setSelected(false);
            cbAbsent.setSelected(false);
        }
        verifierChamps();
    }

    @FXML
    private void comboProfesseur() {
        verifierChamps();
    }

    @FXML
    private void comboCours() {
        verifierChamps();
    }

    @FXML
    private void btnAjouter() {
        Users professeur = comboProfesseur.getValue();
        Cours cours = comboCours.getValue();
        String statut = obtenirStatutSelectionne();
        LocalDate date = datePicker.getValue();

        if (professeur != null && cours != null && statut != null && date != null) {
            if (!emargementService.emargementExiste(professeur.getId(), cours.getId(), date)) {
                Emargements emargement = new Emargements(date, statut, professeur, cours);
                emargementService.ajouterEmargement(emargement);
                listeEmargements.add(emargement);
                tableView.refresh();
                nettoyerChamps();
                showAlert("Succès", "L'émargement a été ajouté avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Un émargement existe déjà pour cette combinaison de professeur, cours et date.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Veuillez remplir tous les champs.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnModifier() {
        Emargements selectionne = tableView.getSelectionModel().getSelectedItem();
        if (selectionne != null) {
            selectionne.setDate(datePicker.getValue());
            selectionne.setStatut(obtenirStatutSelectionne());
            selectionne.setProfesseur(comboProfesseur.getValue());
            selectionne.setCours(comboCours.getValue());
            if (emargementService.modifierEmargement(selectionne)) {
                tableView.refresh();
                nettoyerChamps();
                showAlert("Succès", "L'émargement a été modifié avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la modification de l'émargement.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void btnSupprimer() {
        Emargements selectionne = tableView.getSelectionModel().getSelectedItem();
        if (selectionne != null) {
            if (emargementService.supprimerEmargement(selectionne.getId())) {
                listeEmargements.remove(selectionne);
                tableView.refresh();
                nettoyerChamps();
                showAlert("Succès", "L'émargement a été supprimé avec succès.", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la suppression de l'émargement.", Alert.AlertType.ERROR);
            }
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

    private String obtenirStatutSelectionne() {
        if (cbPresent.isSelected()) return "Présent";
        if (cbRetard.isSelected()) return "Retard";
        if (cbAbsent.isSelected()) return "Absent";
        if (cbExcuse.isSelected()) return "Excusé";
        return null;
    }

    private void nettoyerChamps() {
        datePicker.setValue(null);
        cbPresent.setSelected(false);
        cbRetard.setSelected(false);
        cbAbsent.setSelected(false);
        cbExcuse.setSelected(false);
        comboProfesseur.setValue(null);
        comboCours.setValue(null);
        btnAjouter.setDisable(false);
    }

    private void recupererLigneSelectionnee(MouseEvent event) {
        Emargements selectionne = tableView.getSelectionModel().getSelectedItem();
        if (selectionne != null) {
            datePicker.setValue(selectionne.getDate());
            setStatutSelectionne(selectionne.getStatut());
            comboProfesseur.setValue(selectionne.getProfesseur());
            comboCours.setValue(selectionne.getCours());
        }
        btnAjouter.setDisable(true);

    }

    private void setStatutSelectionne(String statut) {
        cbPresent.setSelected("Présent".equals(statut));
        cbRetard.setSelected("Retard".equals(statut));
        cbAbsent.setSelected("Absent".equals(statut));
        cbExcuse.setSelected("Excusé".equals(statut));
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        colStatut.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatut()));
        colProfesseur.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesseur().getNom() + " " + cellData.getValue().getProfesseur().getPrenom()));
        colCours.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCours().getNom()));

        tableView.setItems(listeEmargements);
        btnAjouter.setDisable(false);

        configurerComboBoxProfesseur();
        configurerComboBoxCours();

        chargerProfesseurs();
        chargerCours();
        chargerEmargements();

        datePicker.setOnAction(event -> verifierChamps());
        cbPresent.setOnAction(event -> verifierChamps());
        cbRetard.setOnAction(event -> verifierChamps());
        cbAbsent.setOnAction(event -> verifierChamps());
        cbExcuse.setOnAction(event -> verifierChamps());
        comboProfesseur.setOnAction(event -> verifierChamps());
        comboCours.setOnAction(event -> verifierChamps());

        tableView.setOnMouseClicked(this::recupererLigneSelectionnee);
    }
}
