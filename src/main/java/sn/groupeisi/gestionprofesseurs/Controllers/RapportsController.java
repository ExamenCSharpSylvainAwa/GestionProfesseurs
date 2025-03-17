package sn.groupeisi.gestionprofesseurs.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;
import sn.groupeisi.gestionprofesseurs.Services.EmargementService;
import sn.groupeisi.gestionprofesseurs.Utils.GraphiqueStatistiquesBarres;
import sn.groupeisi.gestionprofesseurs.Utils.GraphiqueStatistiquesLignes;
import sn.groupeisi.gestionprofesseurs.Utils.GraphiqueStatistiquesdoughnut;
import sn.groupeisi.gestionprofesseurs.Utils.RapportExportateurExcel;

import java.io.File;
import java.util.List;

public class RapportsController {

    private List<Emargements> emargements; // Liste d'émargements

    private EmargementService emargementService; // Service d'émargements

    public void initialize() {
        emargementService = new EmargementService();  // Initialisation du service
        emargements = emargementService.listerEmargements(); // Récupérer la liste des émargements
    }

    public void setEmargements(List<Emargements> emargements) {
        if (emargements == null || emargements.isEmpty()) {
            System.out.println("⚠️ setEmargements() appelé mais la liste est vide ou null !");
        } else {
            System.out.println("✅ setEmargements() appelé avec " + emargements.size() + " éléments.");
        }
        this.emargements = emargements;
    }

    @FXML
    private void btnLignes(ActionEvent event) {
        System.out.println("📌 Vérification des données avant affichage du graphique...");
        if (emargements == null) {
            System.out.println("❌ Erreur : La liste emargements est NULL !");
        } else if (emargements.isEmpty()) {
            System.out.println("⚠️ Aucune donnée à afficher.");
        } else {
            System.out.println("📊 Affichage du graphique avec " + emargements.size() + " émargements.");
            GraphiqueStatistiquesLignes.afficherGraphiqueEvolution(emargements, "jour");
        }
    }

    @FXML
    private void btnDoughnut(ActionEvent event) {
        System.out.println("Affichage du graphique Doughnut (utilisation de afficherGraphiqueTauxPresence).");
        GraphiqueStatistiquesdoughnut.afficherGraphiqueTauxPresence();
    }
    @FXML
    private void btnBarres(ActionEvent event) {
        System.out.println("📌 Vérification des données avant affichage du graphique en barres...");

        if (emargements == null) {
            System.out.println("❌ Erreur : La liste emargements est NULL !");
        } else if (emargements.isEmpty()) {
            System.out.println("⚠️ Aucune donnée à afficher.");
        } else {
            System.out.println("📊 Affichage du graphique en barres avec " + emargements.size() + " émargements.");
            GraphiqueStatistiquesBarres.afficherGraphiqueNombreEmargements(emargements);
        }
    }


    @FXML
    private void btnExcel(ActionEvent event) {
        // Appel à la méthode sans paramètre de génération de rapport Excel
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            RapportExportateurExcel.genererRapportExcel(file.getAbsolutePath()); // Appel sans paramètres
            System.out.println("Rapport Excel généré : " + file.getAbsolutePath());
        } else {
            System.out.println("Aucune donnée à exporter.");
        }
    }

    @FXML
    private void btnPdf(ActionEvent event) {
        /*if (emargements != null && !emargements.isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le fichier PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
            File file = fileChooser.showSaveDialog(new Stage());

            if (file != null) {
                RapportExportateur.genererRapportPDF(emargements, file.getAbsolutePath());
                System.out.println("Rapport PDF généré : " + file.getAbsolutePath());
            }
        } else {
            System.out.println("Aucune donnée à exporter.");
        }
         */
    }
}
