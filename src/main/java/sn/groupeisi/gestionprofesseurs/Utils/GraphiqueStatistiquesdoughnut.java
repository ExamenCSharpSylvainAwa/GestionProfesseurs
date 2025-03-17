package sn.groupeisi.gestionprofesseurs.Utils;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import sn.groupeisi.gestionprofesseurs.Entities.Cours;


import javax.persistence.EntityManager;
import javax.swing.*;
import java.util.List;

public class GraphiqueStatistiquesdoughnut {

    public static void afficherGraphiqueTauxPresence() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            // 🔍 Récupérer la liste des cours
            List<Cours> coursList = entityManager.createQuery("FROM Cours", Cours.class).getResultList();

            if (coursList.isEmpty()) {
                System.out.println("⚠️ Aucune donnée disponible pour le graphique.");
                return;
            }

            for (Cours cours : coursList) {
                // 📝 Compter les émargements pour ce cours
                Long nombreEmargements = entityManager.createQuery(
                                "SELECT COUNT(e) FROM Emargements e WHERE e.cours = :cours", Long.class)
                        .setParameter("cours", cours)
                        .getSingleResult();

                double tauxPresence = (nombreEmargements != null) ? nombreEmargements.doubleValue() : 0;
                dataset.setValue(cours.getNom(), tauxPresence);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

        // 📊 Création du graphique
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Taux de Présence par Cours",
                dataset,
                true, true, false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSimpleLabels(true);
        plot.setIgnoreZeroValues(true);
        plot.setCircular(true);

        JFrame frame = new JFrame("Graphique Taux de Présence");
        frame.setContentPane(new ChartPanel(pieChart));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
