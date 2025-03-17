package sn.groupeisi.gestionprofesseurs.Utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;

import javax.swing.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GraphiqueStatistiquesLignes {

    public static void afficherGraphiqueEvolution(List<Emargements> emargements, String typeVue) {
        if (emargements == null || emargements.isEmpty()) {
            System.out.println("⚠️ Aucune donnée disponible pour afficher le graphique.");
            return;
        }

        // 📊 Création de la série de données
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries series = new TimeSeries("Présences");

        // 🗂️ Regrouper les données selon la vue choisie (jour/semaine/mois)
        Map<String, Integer> aggregations = new HashMap<>();

        for (Emargements emargement : emargements) {
            LocalDate localDate = emargement.getDate();
            if (localDate == null) continue;

            String key;
            switch (typeVue.toLowerCase()) {
                case "semaine":
                    int weekNumber = localDate.get(WeekFields.of(Locale.getDefault()).weekOfYear());
                    key = localDate.getYear() + "-S" + weekNumber;
                    break;
                case "mois":
                    key = localDate.getYear() + "-" + localDate.getMonthValue();
                    break;
                case "jour":
                default:
                    key = localDate.toString(); // yyyy-MM-dd
                    break;
            }

            aggregations.put(key, aggregations.getOrDefault(key, 0) + 1);
        }

        // 📅 Ajouter les données agrégées à la série
        for (Map.Entry<String, Integer> entry : aggregations.entrySet()) {
            String key = entry.getKey();
            int valeur = entry.getValue();

            LocalDate date = null;
            switch (typeVue.toLowerCase()) {
                case "semaine":
                    // Utiliser le premier jour de la semaine pour le graphique (lundi)
                    int weekNumber = Integer.parseInt(key.split("-S")[1]);
                    date = LocalDate.of(Integer.parseInt(key.split("-")[0]), 1, 1)
                            .with(WeekFields.of(Locale.getDefault()).getFirstDayOfWeek())
                            .plusWeeks(weekNumber - 1);
                    break;
                case "mois":
                    date = LocalDate.of(Integer.parseInt(key.split("-")[0]), Integer.parseInt(key.split("-")[1]), 1);
                    break;
                case "jour":
                default:
                    date = LocalDate.parse(key); // yyyy-MM-dd
                    break;
            }

            // Ajouter ou mettre à jour les données dans la série
            if (date != null) {
                switch (typeVue.toLowerCase()) {
                    case "semaine":
                        series.addOrUpdate(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), valeur);
                        break;
                    case "mois":
                        series.addOrUpdate(new Month(date.getMonthValue(), date.getYear()), valeur);
                        break;
                    case "jour":
                    default:
                        series.addOrUpdate(new Day(date.getDayOfMonth(), date.getMonthValue(), date.getYear()), valeur);
                        break;
                }
            }
        }

        dataset.addSeries(series);

        // 📈 Création du graphique
        JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
                "Évolution des Émargements",
                typeVue.equals("jour") ? "Date" : typeVue.equals("semaine") ? "Semaine" : "Mois",
                "Nombre d'Émargements",
                dataset,
                true, true, false
        );

        // 📌 Affichage du graphique dans une fenêtre
        JFrame frame = new JFrame("Graphique Évolution des Émargements");
        frame.getContentPane().add(new ChartPanel(lineChart));
        frame.pack();
        frame.setVisible(true);
    }

}
