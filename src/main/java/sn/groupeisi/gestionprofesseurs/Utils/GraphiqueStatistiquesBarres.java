package sn.groupeisi.gestionprofesseurs.Utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GraphiqueStatistiquesBarres {

    public static void afficherGraphiqueNombreEmargements(List<Emargements> emargements) {
        if (emargements == null || emargements.isEmpty()) {
            System.out.println("⚠️ Aucune donnée disponible pour afficher le graphique.");
            return;
        }

        // 🔍 Regrouper le nombre d’émargements par professeur
        Map<String, Integer> emargementsParProf = new HashMap<>();
        Map<String, String> nomsComplets = new HashMap<>();

        for (Emargements emargement : emargements) {
            String nomPrenomProf = emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom();
            emargementsParProf.put(nomPrenomProf, emargementsParProf.getOrDefault(nomPrenomProf, 0) + 1);
            nomsComplets.put(nomPrenomProf, nomPrenomProf);
        }

        // 📊 Créer le dataset pour le graphique
        CategoryDataset dataset = creerDataset(emargementsParProf);

        // 📈 Générer le graphique en barres
        JFreeChart barChart = ChartFactory.createBarChart(
                "Nombre d'émargements par professeur",
                "Professeurs",
                "Nombre d'émargements",
                dataset
        );

        // 🎨 Personnalisation des couleurs
        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        attribuerCouleurs(renderer, emargementsParProf);

        // 🎨 Affichage du graphique dans une fenêtre Swing
        JFrame frame = new JFrame("Graphique en barres - Émargements");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(barChart));
        frame.pack();
        frame.setVisible(true);
    }

    private static CategoryDataset creerDataset(Map<String, Integer> emargementsParProf) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : emargementsParProf.entrySet()) {
            dataset.addValue(entry.getValue(), "Émargements", entry.getKey()); // Ajout du nom complet
        }
        return dataset;
    }

    private static void attribuerCouleurs(BarRenderer renderer, Map<String, Integer> emargementsParProf) {
        Map<String, Color> couleursProfesseurs = new HashMap<>();
        Random random = new Random();
        int index = 0;

        // Générer une couleur unique pour chaque professeur et les assigner à chaque barre
        for (String nomProf : emargementsParProf.keySet()) {
            if (!couleursProfesseurs.containsKey(nomProf)) {
                // Génère une couleur unique pour chaque professeur
                couleursProfesseurs.put(nomProf, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            renderer.setSeriesPaint(index, couleursProfesseurs.get(nomProf));
            index++;
        }
    }
}
