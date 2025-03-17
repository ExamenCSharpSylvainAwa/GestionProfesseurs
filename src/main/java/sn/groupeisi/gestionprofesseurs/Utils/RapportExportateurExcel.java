package sn.groupeisi.gestionprofesseurs.Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sn.groupeisi.gestionprofesseurs.Entities.Emargements;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RapportExportateurExcel {

    public static void genererRapportExcel(String fichierSortie) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Emargements");

        // 📌 Créer la ligne d'en-tête avec Professeur Nom et Prénom
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Nom Nom");
        headerRow.createCell(1).setCellValue("Prénom Prénom");
        headerRow.createCell(2).setCellValue("Date");
        headerRow.createCell(3).setCellValue("Nombre d'Émargements");

        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();

        try {
            // 🔍 Récupérer tous les émargements depuis la base de données
            TypedQuery<Emargements> query = entityManager.createQuery("FROM Emargements", Emargements.class);
            List<Emargements> emargementsList = query.getResultList();

            if (emargementsList.isEmpty()) {
                System.out.println("⚠️ Aucune donnée disponible pour l'exportation.");
                return;
            }

            // 📊 Ajouter les données des émargements
            int rowIndex = 1;
            for (Emargements emargement : emargementsList) {
                Row row = sheet.createRow(rowIndex++);

                // 📌 Récupérer les informations du professeur et ajouter les données
                String professeurNom = emargement.getProfesseur().getNom(); // Nom du professeur
                String professeurPrenom = emargement.getProfesseur().getPrenom(); // Prénom du professeur
                String date = emargement.getDate().toString(); // Date de l'émargement
                int nombreEmargements = calculerNombreEmargements(emargement); // Nombre d'émargements

                row.createCell(0).setCellValue(professeurNom); // Nom du professeur
                row.createCell(1).setCellValue(professeurPrenom); // Prénom du professeur
                row.createCell(2).setCellValue(date); // Date
                row.createCell(3).setCellValue(nombreEmargements); // Nombre d'émargements
            }

            // 📂 Écrire dans le fichier Excel
            try (FileOutputStream fileOut = new FileOutputStream(fichierSortie)) {
                workbook.write(fileOut);
                System.out.println("✅ Rapport généré avec succès : " + fichierSortie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 📝 Méthode pour calculer le nombre d'émargements (à adapter selon la logique réelle)
    private static int calculerNombreEmargements(Emargements emargement) {
        return 1; // Remplace ceci par la vraie logique si nécessaire
    }
}
